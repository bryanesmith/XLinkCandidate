/*
 * InclusionListGeneratorGUI.fx
 *
 * Created on Apr 23, 2010, 10:50:08 AM
 */
package org.proteomecommons.xlinkcandidate.gui;

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.ext.swing.SwingComboBoxItem;
import javafx.ext.swing.SwingComboBox;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.ext.swing.SwingTextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.ext.swing.SwingButton;
import org.proteomecommons.xlinkcandidate.*;
import java.lang.RuntimeException;
import javax.swing.JFileChooser;
import org.proteomecommons.xlinkcandidate.InclusionListGenerator;
import javafx.stage.Alert;
import javafx.scene.control.ProgressBar;
import javafx.ext.swing.SwingComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Dimension;
import javafx.scene.control.CheckBox;
import javafx.scene.Group;
import org.proteomecommons.t2util.*;
import org.proteomecommons.t2util.utils.*;
import javafx.scene.control.ScrollBar;

/**
 * @author Bryan Smith - bryanesmith@gmail.com
 */
def VERSION: String = "2.2";
def WINDOW_WIDTH: Integer = 450;
//def WINDOW_HEIGHT: Integer = 840;
def WINDOW_HEIGHT: Integer = 600;
def WINDOW_SPACING: Integer = 10;
def subHeaderFont = Font.font("BirchStd", FontWeight.BOLD, 16);
def instrumentDropDownChoices = [
            SwingComboBoxItem {
                text: Instrument.INSTRUMENT_MALDI_4800
                value: Instrument.INSTRUMENT_MALDI_4800
                selected: true;
            }
        ];
def orderingDropDownChoices = [
            for (ordering in MALDIUtil.ALL_ORDERINGS) {
                SwingComboBoxItem {
                    text: ordering
                    value: ordering
                }
            }
        ];
def outputFileChooser: JFileChooser = new JFileChooser;
var outputFileChooserButtonLabel = "Choose output file";
def outputFileChooserButton = SwingButton {
            text: bind outputFileChooserButtonLabel
            action: function () {
                if (JFileChooser.APPROVE_OPTION == outputFileChooser.showSaveDialog(null)) {
                    outputFileChooserButtonLabel = outputFileChooser.getSelectedFile().getAbsolutePath();
                }
            }
        }
def instrumentDropDown = SwingComboBox {
            items: instrumentDropDownChoices
        }
def orderingDropDown = SwingComboBox {
            items: orderingDropDownChoices;
        }
def ipTextField = SwingTextField {
            columns: 15
            text: T2Instrument.DEFAULT_IP
            editable: true
        }
def portTextField = SwingTextField {
            columns: 4
            text: String.valueOf(T2Instrument.DEFAULT_PORT)
            editable: true
        }
def massDiffTextField = SwingTextField {
            columns: 5
            text: String.valueOf(InclusionListGenerator.DEFAULT_MASS_DIFFERENCE)
            editable: true
            background: Color.GRAY
        }
def highMassDiffTextField = SwingTextField {
            columns: 5
            text: String.valueOf(InclusionListGenerator.DEFAULT_HIGH_MASS_DIFFERENCE)
            editable: true
            background: Color.GRAY
        }
def massToleranceTextField = SwingTextField {
            columns: 5
            text: String.valueOf(InclusionListGenerator.DEFAULT_MASS_TOLERANCE)
            editable: true
        }
def spotWindowTextField = SwingTextField {
            columns: 2
            text: String.valueOf(InclusionListGenerator.DEFAULT_SPOT_WINDOW)
            editable: true
        }
def absRequiredIntensityTextField = SwingTextField {
            columns: 5
            text: "0.0"
            editable: true
        }
def isPrintCommentsCheckBox = CheckBox {
            text: "Print comments in output file"
            selected: InclusionListGenerator.DEFAULT_PRINT_COMMENTS
        }
var progressStepsCompleted: Number = 0;
var progressTotalSteps: Number = 1;
def progressBar = ProgressBar {
            width: WINDOW_WIDTH - WINDOW_SPACING * 4;
            progress: bind ProgressBar.computeProgress(progressTotalSteps, progressStepsCompleted)
            visible: bind runToolButtonDisabled;
        }
def consoleDimensions: Dimension = new Dimension(WINDOW_WIDTH - WINDOW_SPACING * 4, 125);
var consoleTextPane: JTextPane = new JTextPane();

consoleTextPane.setPreferredSize(consoleDimensions);
var consoleScrollPane: JScrollPane = new JScrollPane(consoleTextPane);

consoleScrollPane.setPreferredSize(consoleDimensions);
var console: SwingComponent = SwingComponent.wrap(consoleScrollPane);

console.width = consoleDimensions.width;
console.height = consoleDimensions.height;

var jobDropDownChoices: SwingComboBoxItem[] = [];
var jobDropDown = SwingComboBox {
            items: bind jobDropDownChoices;
        }
var spotSetsDropDownChoices: SwingComboBoxItem[] = [];
var spotSetsDropDown = SwingComboBox {
            items: bind spotSetsDropDownChoices;
        };
var connectButtonLabel = "Connect";
var instrument: T2Instrument;
var isConnected = false;
var isSpotSetSelected = false;
var runToolLabel = "start";
var runToolButtonDisabled = false;
def runToolButton = SwingButton {
            text: bind runToolLabel
            disable: bind runToolButtonDisabled
            action: function () {
                runTool();
            }
        }
def runTool = function (): Void {

            // Verify: selected job
            def selectedJob: String = jobDropDown.selectedItem.value as String;
            if (selectedJob == null or selectedJob.trim().equals("")) {
                Alert.inform("Please select a job id from the drop down.");
                return ;
            }

            // Verify: mass diff
            def massDiff: Float = Float.parseFloat(massDiffTextField.text);

            def highMassDiff: Float = Float.parseFloat(highMassDiffTextField.text);

            // Verify: spot order
            def spotOrder:String = orderingDropDown.text;

            if (spotOrder == null or spotOrder.trim().equals("")) {
                Alert.inform("Please select a spot order from the drop down.");
                return ;
            }

            // Verify: mass tolerance
            def massTolerance: Float = Float.parseFloat(massToleranceTextField.text);

            // Verify: spot window
            def spotWindow: Integer = Integer.parseInt(spotWindowTextField.text);

            // Verify: output file
            def outputFile = outputFileChooser.getSelectedFile();

            // Optional: absolute required intensity
            def absRequiredIntensity: Float = Float.parseFloat(absRequiredIntensityTextField.text);

            def isPrintCommentsVal: Boolean = isPrintCommentsCheckBox.selected;

            if (outputFile == null) {
                Alert.inform("Please select a place to save the output file.");
                return ;
            }

            if (outputFile.exists()) {
                var cont: Boolean = Alert.confirm("The output file already exists and will be overwritten. Continue?");

                if (not cont) {
                    return ;
                }
            }


            def listener = new InclusionListGeneratorGIUListener;

            def tool = new InclusionListGenerator(instrument, outputFile);
            tool.addListener(listener);

            tool.setSpotWindow(spotWindow);
            tool.setMassDifference(massDiff);
            tool.setHighMassDifference(highMassDiff);
            tool.setMassTolerance(massTolerance);
            tool.setSpotSetDescription(selectedSpotSet);
            tool.setJobId(jobDropDown.selectedItem.value as String);
            tool.setRequiredAbsoluteIntensity(absRequiredIntensity);
            tool.setPrintComments(isPrintCommentsVal);
            tool.setSpotOrder(spotOrder);

            runToolLabel = "running...";
            runToolButtonDisabled = true;

            // Clear console for new run
            consoleTextPane.setText("");

            tool.runOnSeparateThread();
        };
def initForNextRun = function () {
            runToolLabel = "start";
            runToolButtonDisabled = false;

            outputFileChooserButtonLabel = "Choose output file";
            outputFileChooser.setSelectedFile(null);

            progressStepsCompleted = 0;
            progressTotalSteps = 1;
        }
def printStatus = function (str: String): Void {
            println(str);
            consoleTextPane.setText("{consoleTextPane.getText()}\n{str}");
        }

class InclusionListGeneratorGIUListener extends InclusionListGeneratorListener {

    /**
     *
     */
    override function noteMessage(msg: String): Void {
        FX.deferAction(function (): Void {
            printStatus(msg);
            });
    }

    /**
     *
     */
    override function noteFinished(): Void {
        FX.deferAction(function (): Void {
            printStatus("finished");
            Alert.inform("The tool has finished. The output file is at: {outputFileChooser.getSelectedFile().getAbsolutePath()}");
            initForNextRun();
            });
    }

    /**
     *
     */
    override function noteFailed(msg: String): Void {
        FX.deferAction(function (): Void {
            printStatus("failed: {msg}");
            Alert.inform("The tool failed with the following message: {msg}");
            initForNextRun();
            });
    }

    /**
     *
     * @param step
     * @param totalSteps
     * @param msg
     */
    override function noteProgressUpdate(currentStep: Integer, totalSteps: Integer, msg: String) {
        FX.deferAction(function (): Void {
            printStatus("progress: {currentStep} of {totalSteps} with message \"{msg}\"");
            progressStepsCompleted = currentStep;
            progressTotalSteps = totalSteps;
            });
    }

}
def connectButton = SwingButton {
            text: bind connectButtonLabel
            action: function () {
                if (isConnected == false) {
                    connectToInstrument()
                } else {
                    disconnectFromInstrument()
                }
            }
        }
var selectedSpotSet: SpotSetDescription;
var selectedSpotSetName: String;
var selectedSpotSetCount: Integer;
def selectSpotSetButton = SwingButton {
            text: "Select"
            action: function () {
                def item = spotSetsDropDown.selectedItem;
                openSpotSet(item.text as String, item.value as String);
            }
        }
/**
 * Opens up a spot set by id.
 */
def openSpotSet = function (spotSetName: String, spotSetId: String): Void {

            if (spotSetId == null or spotSetId.trim().equals("")) {
                Alert.inform("Please select a spot set from the list.");
                return ;
            }

            closeSpotSet();

            for (obj in instrument.getSpotDescriptions()) {
                def spotSetDesc = obj as SpotSetDescription;

                if (spotSetDesc.getId().equals(spotSetId)) {
                    selectedSpotSet = spotSetDesc;
                    break;
                }
            }

            // Assert selected spot set found (not null)
            if (selectedSpotSet == null) {
                throw new RuntimeException("Could not find spot set with ID {spotSetId}");
            }

            // Assert that name matches
            if (not selectedSpotSet.getName().equals(spotSetName)) {
                throw new RuntimeException("Names do not match: \"{spotSetName}\" vs \"{selectedSpotSet.getName()}\"");
            }

            selectedSpotSetName = spotSetName;
            selectedSpotSetCount = selectedSpotSet.size();
            println("Selecting spot set \"{spotSetName}\" (id: {spotSetId}, |spots|: {selectedSpotSetCount}).");
            isSpotSetSelected = true;

            // Build job id drop down
            def tempJobIdsDropDownChoices = for (jobId: String in selectedSpotSet.getJobIds()) {
                        SwingComboBoxItem {
                            text: jobId
                            value: jobId
                        }
                    }
            jobDropDownChoices = tempJobIdsDropDownChoices;
        }
/**
 * Safely closes spot set, if one.
 */
def closeSpotSet = function () {
            if (selectedSpotSet != null) {
                selectedSpotSet.close();
            }
            isSpotSetSelected = false;
        }
/**
 *
 */
def connectToInstrument = function () {

            instrument = T2Instrument.connect(ipTextField.text, Integer.parseInt(portTextField.text));
            println("Connected to {instrument.getIP()}:{instrument.getPort()}");
            connectButtonLabel = "Disconnect";

            def tempSpotSetsDropDownChoices = for (obj in T2CollectionsUtil.sortSpotSetsByName(instrument.getSpotDescriptions())) {
                        def spotSetDesc = obj as SpotSetDescription;
                        SwingComboBoxItem {
                            text: spotSetDesc.getName()
                            value: spotSetDesc.getId()
                        }
                    }
            spotSetsDropDownChoices = tempSpotSetsDropDownChoices;

            // Flag connection last since toggles visibility of gui elements
            isConnected = true;
        }
/**
 *
 */
def disconnectFromInstrument = function () {

            closeSpotSet();

            instrument.close();
            println("Disconnected from {instrument.getIP()}:{instrument.getPort()}");
            instrument == null;
            connectButtonLabel = "Connect";

            // Flag disconnection. Also, no spot set can be selected if
            // disconnected.
            isConnected = false;
            isSpotSetSelected = false;

            // Remove drop down
            spotSetsDropDown = null;
        }
var scroll = ScrollBar {
            translateX: bind WINDOW_WIDTH - 10
            translateY: 0
            height: WINDOW_HEIGHT
            scaleX: 1
            blockIncrement: 16
            clickToPosition: false
            min: 0
            max: 1.25
            vertical: true
        };
var guiContent: Group = Group {
            translateY: bind 0 - scroll.value * WINDOW_HEIGHT
            content: [
                // --------------------------------------------------
                // Mass spectrometer information
                // --------------------------------------------------
                VBox {
                    translateX: 10
                    translateY: 20
                    spacing: 10
                    content: [
                        Text {
                            content: "Mass Spectrometer"
                            font: subHeaderFont
                        }
                        // Type
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Type"
                                }
                                instrumentDropDown
                            ]
                        }
                        // IP
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "IP"
                                }
                                ipTextField
                            ]
                        }
                        // Port
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Port"
                                }
                                portTextField
                            ]
                        }
                        // Connect button
                        connectButton
                    ]
                } // Group: Mass spectrometer information

                // --------------------------------------------------
                // Selected spot set
                // --------------------------------------------------
                VBox {
                    translateX: 10
                    translateY: 200
                    spacing: 10
                    visible: bind isConnected
                    content: [
                        Text {
                            content: "Spot Set"
                            font: subHeaderFont
                        },
                        spotSetsDropDown,
                        selectSpotSetButton
                    ]
                }
                // --------------------------------------------------
                // Parameters
                // --------------------------------------------------
                VBox {
                    translateX: 10
                    translateY: 330
                    spacing: 10
                    visible: bind isSpotSetSelected
                    content: [
                        Text {
                            content: bind "Select parameters for {selectedSpotSetName}"
                            font: subHeaderFont
                        },
                        Text {
                            content: bind "Total spot(s): {selectedSpotSetCount}"
                        },
                        // Job
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Job"
                                }
                                jobDropDown
                            ]
                        },
                        // Ordering
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Spot order"
                                }
                                orderingDropDown
                            ]
                        },
                        // Mass difference
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Mass difference"
                                },
                                massDiffTextField,
                                Text {
                                    content: "m/z"
                                }
                            ]
                        }
                        // High mass difference : highMassDiffTextField
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "High mass difference"
                                },
                                highMassDiffTextField,
                                Text {
                                    content: "m/z"
                                }
                            ]
                        }
                        // Mass tolerance
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Mass tolerance"
                                },
                                massToleranceTextField,
                                Text {
                                    content: "m/z"
                                }
                            ]
                        }
                        // Spot window
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Spot window"
                                },
                                spotWindowTextField,
                                Text {
                                    content: "spots"
                                }
                            ]
                        }
                        // Abs required intensity
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Absolute required intensity (optional)"
                                },
                                absRequiredIntensityTextField,
                            ]
                        }
                        // Output file
                        HBox {
                            spacing: 10
                            content: [
                                Text {
                                    content: "Output file"
                                },
                                outputFileChooserButton
                            ]
                        }
                        isPrintCommentsCheckBox,
                        // "Start" button
                        runToolButton,
                        progressBar,
                        console
                    ]
                }
            ]
        };

// --------------------------------------------------------------
//  GUI
// --------------------------------------------------------------
Stage {
    title: "XLink Inclusion Generator {VERSION}"
    scene: Scene {
        width: WINDOW_WIDTH
        height: WINDOW_HEIGHT
        fill: Color.LAVENDERBLUSH
        content: [
            scroll, guiContent
        ]
    }
}
