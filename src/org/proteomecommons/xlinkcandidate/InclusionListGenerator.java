/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.proteomecommons.t2util.PeakDescription;
import org.proteomecommons.t2util.PeakListDescription;
import org.proteomecommons.t2util.SpotDescription;
import org.proteomecommons.t2util.SpotSetDescription;
import org.proteomecommons.t2util.T2Instrument;
import org.proteomecommons.t2util.utils.AssertionUtil;
import org.proteomecommons.t2util.utils.T2CollectionsUtil;
import org.proteomecommons.xlinkcandidate.utils.IOUtil;
import org.proteomecommons.xlinkcandidate.utils.PeakPairUtil;

/**
 * <p>Generates an inclusion list for mass spec instrument that follows certain heuristics for identifying crosslinks from MS for MSMS.</p>
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public class InclusionListGenerator {
    
    /**
     * For P = A' + B' + mass difference + adjustment:
     * 
     * This last part is adjusted to account for chemical effects like protonation.
     * This should be adjusted so this formula is correct.
     */
    public static final int DEFAULT_ADJUSTMENT_FOR_CALCULATING_PARENT = -1;
    
    /**
     * 
     */
    public int adjustmentForCalculatingParent = DEFAULT_ADJUSTMENT_FOR_CALCULATING_PARENT;

    /**
     *
     */
    public static final boolean DEFAULT_PRINT_COMMENTS = true;
    /**
     * 
     */
    public static final float DEFAULT_MASS_DIFFERENCE = 112.0f;
    /**
     * 
     */
    public static float DEFAULT_HIGH_MASS_DIFFERENCE = 86.0f;
    /**
     *
     */
    public static final float DEFAULT_MASS_TOLERANCE = 1.0f;
    /**
     *
     */
    public static final int DEFAULT_SPOT_WINDOW = 5;
    // Final variables set in constructor
    private final String instrumentType, ip;
    private final File outputFile;
    private final int port;
    private final T2Instrument instrument;
    private SpotSetDescription spotSetDescription;
    private String jobId;
    private String spotOrder;
    // Default variables
    private float massDifference, massTolerance, highMassDifference;
    private int spotWindow;
    private File tmpDir = null;
    // Other variables
    private Set<InclusionListGeneratorListener> listeners;
    private static final float DEFAULT_REQUIRED_ABSOLUTE_INTENSITY = 0;
    private float requiredAbsoluteIntensity = DEFAULT_REQUIRED_ABSOLUTE_INTENSITY;
    private boolean printComments = DEFAULT_PRINT_COMMENTS;

    /**
     * 
     * @param instrument
     * @param outputFile
     */
    public InclusionListGenerator(T2Instrument instrument, File outputFile) {

        assertNotNull(instrument, "T2Instrument object from T2Util");
        assertNotNull(outputFile, "output file for includsion list");

        this.instrument = instrument;
        this.instrumentType = Instrument.INSTRUMENT_MALDI_4800;
        this.outputFile = outputFile;
        this.ip = instrument.getIP();
        this.port = instrument.getPort();

        init();
    }

    /**
     *
     * @param instrumentType Use org.proteomecommons.xlinkcandidate.instruments.Instruments to get name
     * @param ip The IP address of the instrument
     * @param port The port that the instrument server is listening on
     * @param outputFile The place to save the inclusion list and other output
     */
    public InclusionListGenerator(String instrumentType, String ip, int port, File outputFile) throws Exception {

        assertNotNull(instrumentType, "instrument type");
        assertNotNull(ip, "IP address for instrument");
        assertNotNull(outputFile, "output file for includsion list");

        // Final to cut down on getter/setter madness.
        this.instrumentType = instrumentType;
        this.ip = ip;
        this.port = port;
        this.outputFile = outputFile;
        this.instrument = T2Instrument.connect(ip, port);

        init();
    }

    private void init() {
        // Variables with defaults
        this.massDifference = DEFAULT_MASS_DIFFERENCE;
        this.massTolerance = DEFAULT_MASS_TOLERANCE;
        this.spotWindow = DEFAULT_SPOT_WINDOW;
        this.highMassDifference = DEFAULT_HIGH_MASS_DIFFERENCE;

        // Find a temporary directory
        String tmpDirPath = System.getProperty("java.io.tmpdir");

        if (tmpDirPath != null) {
            this.setTempDirPath(tmpDirPath);
        }

        if (this.tmpDir == null) {
            // Can't1 use system temp directory. Create a temporary directory in working
            // directory and use that.
            File workingDirTmpDir = new File("tmp");
            setTempDirPath(workingDirTmpDir.getAbsolutePath());
        }

        // Other variables
        this.listeners = new HashSet();
    }

    /**
     *
     * @param o
     * @param description
     * @throws Exception
     */
    private void assertNotNull(Object o, String description) {
        if (o == null) {
            throw new RuntimeException("Must specify value for " + description);
        }
    }

    /**
     *
     * @param listener
     * @return
     */
    public boolean addListener(InclusionListGeneratorListener listener) {
        return this.listeners.add(listener);
    }

    /**
     *
     * @param tmpDirPath
     * @return
     */
    public boolean setTempDirPath(String tmpDirPath) {
        File potentialTmpDir = new File(tmpDirPath);

        if (isTestDirUsable(potentialTmpDir)) {
            tmpDir = potentialTmpDir;
            return true;
        }

        return false;
    }

    /**
     *
     * @param potentialTmpDir
     * @return
     */
    private boolean isTestDirUsable(File potentialTmpDir) {
        try {
            potentialTmpDir.mkdirs();

            if (!potentialTmpDir.exists()) {
                return false;
            }

            File tmpFile = new File(potentialTmpDir, "deleteme");
            tmpFile.createNewFile();

            if (!tmpFile.exists()) {
                throw new IOException("Failed to make temp file <" + tmpFile.getAbsolutePath() + ">. Perhaps can't write in temp directory.");
            }

            IOUtil.safeDelete(tmpFile);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return the instrumentType
     */
    public String getInstrumentType() {
        return instrumentType;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @return the outputFile
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @return the massDifference
     */
    public float getMassDifference() {
        return massDifference;
    }

    /**
     * @param massDifference the massDifference to set
     */
    public void setMassDifference(float massDifference) {
        this.massDifference = massDifference;
    }

    /**
     * @return the massTolerance
     */
    public float getMassTolerance() {
        return massTolerance;
    }

    /**
     * @param massTolerance the massTolerance to set
     */
    public void setMassTolerance(float massTolerance) {
        this.massTolerance = massTolerance;
    }

    /**
     * @return the spotWindow
     */
    public int getSpotWindow() {
        return spotWindow;
    }

    /**
     * @param spotWindow the spotWindow to set
     */
    public void setSpotWindow(int spotWindow) {
        this.spotWindow = spotWindow;
    }

    /**
     *
     */
    public void run() {

        Set<PeakListDescription> peakListsToReset = new HashSet();

        try {
            assertNotNull(tmpDir, "temporary directory");
            assertNotNull(spotSetDescription, "spot set");
            assertNotNull(jobId, "job id");
            assertNotNull(getSpotOrder(), "spot order");

            fireNoteMessage("Job id: " + jobId + ", spot order: " + getSpotOrder());

            final int totalSteps = 5;

            // --------------------------------------------------------------------------------------------------
            // STEP 1:
            //   a. Get all spots that were included in job. Sort by fraction number so can use spot window.
            //   b. For each spot, identify the peaklist for that job with the highest job run id (the last one)
            //   c. Identify all valid peak pairs
            // --------------------------------------------------------------------------------------------------
            fireProgressUpdate(1, totalSteps, "Gathering peak lists.");
            List<SpotDescription> selectedSpots = T2CollectionsUtil.sortSpotsByFractionNumber(spotSetDescription.getSpotDescriptionsByJobId(jobId), getSpotOrder());
            fireNoteMessage("Found a total of " + selectedSpots.size() + " spot(s) for jobId: " + jobId);

            // -------------------------------------------------------------------------------
            // STEP 2:
            //   Build up collection of spots that match the job id. It is assumed that
            //   the newest peaklist should be used. This effectively removes spots without
            //   peaklists.
            // -------------------------------------------------------------------------------
            fireProgressUpdate(2, totalSteps, "Finding spots that match job ID.");
            List<CandidateSpot> candidateSpots = new LinkedList();
            for (SpotDescription spot : selectedSpots) {
                PeakListDescription newestPKL = T2CollectionsUtil.getNewestPeakListForSpotSetByJobId(spot, jobId);

                peakListsToReset.add(newestPKL);

                // Using monoisotopic peaks, so don't do this. Potentially remove valid peaks.
//                newestPKL.removeIsolopes();

                if (this.requiredAbsoluteIntensity > DEFAULT_REQUIRED_ABSOLUTE_INTENSITY) {
                    newestPKL.removePeakDescriptionsBelowAbsoluteThresholdIntensity(requiredAbsoluteIntensity);
                }

                if (newestPKL != null) {
                    CandidateSpot candidateSpot = new CandidateSpot(spot, newestPKL);
                    candidateSpots.add(candidateSpot);
                }
            }

            fireNoteMessage("After removing any spots without peak lists for specified job, there are a total of " + candidateSpots.size() + " candidate(s).");

            Set<CandidateSpot> spotsToRemove = new HashSet();

            // -------------------------------------------------------------------------------
            // STEP 3:
            //   Sweep to identify pairs and remove any spots that don't have pairs.
            // -------------------------------------------------------------------------------
            fireProgressUpdate(3, totalSteps, "Removing spots without any candidate crosslinks.");
            for (CandidateSpot candidateSpot : candidateSpots) {

                List<PeakPair> peakPairs = getPairsForSpot(candidateSpot);
                List<CandidateDeadEnd> deadEnds = getCandidateDeadEndsForSpot(candidateSpot);

                if (peakPairs.size() > 0 || deadEnds.size() > 0) {
                    candidateSpot.pairs.addAll(peakPairs);
                    candidateSpot.deadEnds.addAll(deadEnds);
                    fireNoteMessage("     Spot \"" + candidateSpot.spot.getWellName() + "\" (fraction: " + candidateSpot.spot.getFractionNumber(getSpotOrder()) + ") has total of " + peakPairs.size() + " peak pairs and " + deadEnds.size() + " dead ends.");
                } else {
                    spotsToRemove.add(candidateSpot);
                }
            }

            candidateSpots.removeAll(spotsToRemove);

            fireNoteMessage("After removing any spots without peak pairs of difference " + this.getMassDifference() + " +- " + this.massTolerance + " m/z, there are a total of " + candidateSpots.size() + " candidate(s).");

            // -------------------------------------------------------------------------------
            //   Dump all peak lists to file
            // -------------------------------------------------------------------------------
            {
                final File spectraOutputFile = new File(outputFile.getAbsolutePath() + "." + System.currentTimeMillis() + ".peaklists");

                BufferedWriter out = null;
                try {
                    if (spectraOutputFile.exists()) {
                        throw new RuntimeException("Spectra output file exists, but shouldn't: " + spectraOutputFile.getAbsolutePath());
                    }
                    out = new BufferedWriter(new FileWriter(spectraOutputFile));
                    for (CandidateSpot spot : candidateSpots) {
                        out.write("--- Well name: " + spot.spot.getWellName() + " Fraction number: " + spot.spot.getFractionNumber(this.spotOrder) + " ---");
                        out.newLine();
                        for (PeakDescription peak : spot.peakList.getPeakDescriptions()) {
                            out.write("\t" + peak.getCentroid() + "\t" + peak.getIntensity());
                            out.newLine();
                        }
                        out.newLine();
                    }
                    fireNoteMessage("Wrote all spectra to file: " + spectraOutputFile.getAbsolutePath());
                } catch (Exception e) {
                    fireNoteMessage("Failed to write all spectra due to " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    IOUtil.safeClose(out);
                }
            }

            // -------------------------------------------------------------------------------
            // STEP 4:
            //   Look for parents for peak pairs. If not found, then classify the pair as
            //   a CandidatePeakPair.
            // -------------------------------------------------------------------------------
            fireProgressUpdate(4, totalSteps, "Classifying candidate peak pairs by looking for parent peaks");

            List<CandidatePeakPair> candidatePeakPairs = new LinkedList();
            List<CandidatePeakPairsWithParent> candidatePeakPairsWithParent = new LinkedList();

            for (CandidateSpot candidateSpot : candidateSpots) {

                List<PeakPair> peakPairsCopy = new LinkedList(candidateSpot.pairs);

                if (peakPairsCopy.size() == 0 && candidateSpot.deadEnds.size() == 0) {
                    AssertionUtil.fail("Should be peak pairs and/or dead ends for candidate spot " + candidateSpot.spot.getWellName() + ", but there are neither.");
                }

                // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- 
                // Avoid creating CandidatePeakPair objects for secondPair-mass peak pairs with parent. =)
                //
                // To understand this, note that we iterate through all pairs, starting at lowest (which we'll call A),
                // and compare against another peak pair with secondPair mass (which we'll call B). If we find that A + B + weight
                // of xlink == some parent peak, we don't want to to add a CandidatePeakPair for either A nor B since they
                // will already be included in a CandidatePeakPairsWithParent
                // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
                Set<PeakPair> alreadyDiscoveredPeakPairs = new HashSet();

                CLASSIFIER:
                while (peakPairsCopy.size() > 0) {
                    PeakPair lowerPeakPair = peakPairsCopy.remove(0);

                    // If this lowerPeakPair ends up part of one (or more) CandidatePeakPairsWithParent,
                    // then we don't need to create a CandidatePeakPair for it.
                    boolean wasFound = alreadyDiscoveredPeakPairs.contains(lowerPeakPair);
                    
                    // PART A: Check to see if P = A` + A` + 112 - 1 
                    // i.e., A` = B`
                    {
                        // --------------------------------------------------------------------------
                        //  Do a sweep looking for parent peak at expected mass.
                        // --------------------------------------------------------------------------
                        PARENT_PEAK:
                        for (PeakDescription potentialParentPeak : candidateSpot.peakList.getPeakDescriptions()) {

                            boolean isParent = PeakPairUtil.isFoundParentPeakForXLinks(lowerPeakPair, lowerPeakPair, potentialParentPeak, this.getMassDifference(), this.adjustmentForCalculatingParent, massTolerance);
                            if (isParent) {
                                wasFound = true;
                                alreadyDiscoveredPeakPairs.add(lowerPeakPair);

                                String desc = candidateSpot.spot.getWellName() + ": Parent mass peak found at " + potentialParentPeak.getCentroid() + " m/z <intensity: " + potentialParentPeak.getIntensity() + "> for pairs {" + lowerPeakPair.peak1.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak1.getIntensity() + ">, " + lowerPeakPair.peak2.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak2.getIntensity() + ">} and {" + lowerPeakPair.peak1.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak1.getIntensity() + ">, " + lowerPeakPair.peak2.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak2.getIntensity() + ">}.";

                                CandidatePeakPairsWithParent c = new CandidatePeakPairsWithParent(candidateSpot, lowerPeakPair, lowerPeakPair, potentialParentPeak);
                                c.setDescription(desc);
                                candidatePeakPairsWithParent.add(c);

                                // Can't continue; might be another parent barely within the mass range!
//                                // Always possible that there are other pairs that match, so continue there
//                                continue OTHER_PAIRS;
                            }
                        }
                    }

                    // PART B: Check to see if P = A` + B` + 112 - 1 
                    // Where weight( B` ) > weight( A`)
                    OTHER_PAIRS:
                    for (PeakPair higherPeakPair : peakPairsCopy) {

                        if (higherPeakPair.peak1.getCentroid() <= lowerPeakPair.peak1.getCentroid() && higherPeakPair.peak2.getCentroid() <= lowerPeakPair.peak2.getCentroid()) {
                            AssertionUtil.fail("Higher peak pair " + (higherPeakPair.peak1.getCentroid() + " m/z, " + higherPeakPair.peak2.getCentroid() + " m/z") + " should not be lower mass or equal to lower peak pair " + (lowerPeakPair.peak1.getCentroid() + " m/z, " + lowerPeakPair.peak2.getCentroid() + " m/z") + " for spot " + candidateSpot.spot.getWellName() + ", but is.");
                        }

                        // --------------------------------------------------------------------------
                        //  Do a sweep looking for parent peak at expected mass.
                        // --------------------------------------------------------------------------
                        PARENT_PEAK:
                        for (PeakDescription potentialParentPeak : candidateSpot.peakList.getPeakDescriptions()) {

                            boolean isParent = PeakPairUtil.isFoundParentPeakForXLinks(lowerPeakPair, higherPeakPair, potentialParentPeak, this.getMassDifference(), this.adjustmentForCalculatingParent, massTolerance);
                            if (isParent) {
                                wasFound = true;
                                alreadyDiscoveredPeakPairs.add(higherPeakPair);

                                String desc = candidateSpot.spot.getWellName() + ": Parent mass peak found at " + potentialParentPeak.getCentroid() + " m/z <intensity: " + potentialParentPeak.getIntensity() + "> for pairs {" + lowerPeakPair.peak1.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak1.getIntensity() + ">, " + lowerPeakPair.peak2.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak2.getIntensity() + ">} and {" + higherPeakPair.peak1.getCentroid() + " m/z <intensity: " + higherPeakPair.peak1.getIntensity() + ">, " + higherPeakPair.peak2.getCentroid() + " m/z <intensity: " + higherPeakPair.peak2.getIntensity() + ">}.";

                                CandidatePeakPairsWithParent c = new CandidatePeakPairsWithParent(candidateSpot, lowerPeakPair, higherPeakPair, potentialParentPeak);
                                c.setDescription(desc);
                                candidatePeakPairsWithParent.add(c);

                                // Can't continue; might be another parent barely within the mass range!
//                                // Always possible that there are other pairs that match, so continue there
//                                continue OTHER_PAIRS;
                            }
                        }
                    }

                    // Only add as CandidatePeakPair if at least one parent mass not found
                    if (!wasFound) {

                        final String desc = candidateSpot.spot.getWellName() + ": No parent mass peak found for peak pair " + lowerPeakPair.peak1.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak1.getIntensity() + "> and " + lowerPeakPair.peak2.getCentroid() + " m/z <intensity: " + lowerPeakPair.peak2.getIntensity() + ">";

                        CandidatePeakPair c = new CandidatePeakPair(candidateSpot, lowerPeakPair);
                        c.setDescription(desc);
                        candidatePeakPairs.add(c);
                    }
                }
            } // Classifying candidate spots

            fireNoteMessage("Found a total of " + candidatePeakPairsWithParent.size() + " candidate peak-pair pair(s) with parent mass peak and " + candidatePeakPairs.size() + " candidate peak pair(s) without associated parent mass peaks.");

            // ======================================================================================
            //  Not using the spot window, since increases the noise-to-signal ration. Talk to
            //  Billy and Phil to determine whether this should go back.
            //
            //  If so, will need to implement remove for dead-ends. This includes deciding on
            //  which dead-end to remove.
            // ======================================================================================
            List<Candidate> allCandidates = new LinkedList();
            allCandidates.addAll(candidatePeakPairs);
            allCandidates.addAll(candidatePeakPairsWithParent);
//            fireProgressUpdate(4, totalSteps, "Removing peak pairs within spot window.");
//
//            final int beforeCount = allCandidates.size();
//
//            Set<Candidate> removedCandidates = new HashSet();
//
//            OUTER:
//            for (Candidate firstCandidate : allCandidates) {
//
//                INNER:
//                for (Candidate secondCandidate : allCandidates) {
//
//                    // If firstPair was recently removed or removed in past, stop checking it
//                    if (removedCandidates.contains(firstCandidate)) {
//                        continue OUTER;
//                    }
//
//                    // If high removed in past, skip it
//                    if (removedCandidates.contains(secondCandidate)) {
//                        continue INNER;
//                    }
//
//                    // Skip same object and make sure firstPair versus secondPair
//                    if (firstCandidate == secondCandidate) {
//                        continue INNER;
//                    }
//
//                    // Must be same class: CandidatePeakPair or CandidatePeakPairsWithParent
//                    if (!firstCandidate.getClass().equals(secondCandidate.getClass())) {
//                        continue INNER;
//                    }
//
//                    // Must be in same spot window (but not same spot)
//                    final int fractionDiff = Math.abs(secondCandidate.getCandidateSpot().spot.getFractionNumber(getSpotOrder()) - firstCandidate.getCandidateSpot().spot.getFractionNumber(getSpotOrder()));
//
//                    if (fractionDiff == 0 || fractionDiff > spotWindow) {
//                        continue INNER;
//                    }
//
//                    if (firstCandidate.getClass().equals(CandidatePeakPair.class)) {
//                        CandidatePeakPair firstPair = (CandidatePeakPair) firstCandidate;
//                        CandidatePeakPair secondPair = (CandidatePeakPair) secondCandidate;
//
//                        // Is this an equivalent peak pair within the same window?
//                        if (firstPair.peakPair.isEquivalentPair(secondPair.peakPair)) {
//
//                            // Maximize the lower mass peak
//                            if (firstPair.peakPair.peak1.getIntensity() > secondPair.peakPair.peak1.getIntensity()) {
//                                removedCandidates.add(secondCandidate);
//                            } else {
//                                removedCandidates.add(firstCandidate);
//                            }
//                        }
//
//                    } else if (firstCandidate.getClass().equals(CandidatePeakPairsWithParent.class)) {
//                        CandidatePeakPairsWithParent firstSet = (CandidatePeakPairsWithParent) firstCandidate;
//                        CandidatePeakPairsWithParent secondSet = (CandidatePeakPairsWithParent) secondCandidate;
//
//                        boolean areLowerPairsEquivalent = firstSet.lowerMassPeakPair.isEquivalentPair(secondSet.higherMassPeakPair);
//                        boolean areHigherPairsEquivalent = firstSet.lowerMassPeakPair.isEquivalentPair(secondSet.higherMassPeakPair);
//
//                        // Are these two the same [ 2 x peak pairs + parent mass peak ] within the same window?
//                        if (areLowerPairsEquivalent && areHigherPairsEquivalent) {
//
//                            // Maximize both the lower mass peak in the lower mass pair AND the parent intensities
//                            final double firstIntScore = intensityScore(firstSet.lowerMassPeakPair.peak1.getIntensity(), firstSet.parentMassPeak.getIntensity());
//                            final double secondIntScore = intensityScore(secondSet.lowerMassPeakPair.peak1.getIntensity(), secondSet.parentMassPeak.getIntensity());
//
//                            if (firstIntScore > secondIntScore) {
//                                removedCandidates.add(secondCandidate);
//                            } else {
//                                removedCandidates.add(firstCandidate);
//                            }
//                        }
//                    } else {
//                        AssertionUtil.fail("Unrecognized class: " + firstCandidate.getClass().getSimpleName());
//                    }
//                }
//            }
//
//            allCandidates.removeAll(removedCandidates);
//
//            final int afterCount = allCandidates.size();
//
//            fireNoteMessage("Before removing from same spot window, was " + beforeCount + " candiate(s). After, " + afterCount + " candidate(s).");

            int deadEnds = 0;
            for (CandidateSpot spot : candidateSpots) {
                deadEnds += spot.deadEnds.size();
            }

            final int totalPeakPairsNoParent = candidatePeakPairs.size();
            final int totalPeakPairsWithParent = candidatePeakPairsWithParent.size();
            final int totalDeadEnds = deadEnds;

            fireNoteMessage("----------------------------------------------------------------------");
            fireNoteMessage(" SUMMARY");
            fireNoteMessage("   * Total of " + (totalPeakPairsWithParent + totalPeakPairsNoParent + totalDeadEnds) + " candidate crosslinks");
            fireNoteMessage("     - " + totalDeadEnds + " candidate dead-ends");
            fireNoteMessage("     - " + (totalPeakPairsWithParent + totalPeakPairsNoParent) + " candidate crosslinks/link loops");
            fireNoteMessage("       . " + totalPeakPairsWithParent + " peak pairs with parent");
            fireNoteMessage("       . " + totalPeakPairsNoParent + " individual peak pairs (w/o parent peak)");
            fireNoteMessage("----------------------------------------------------------------------");

            fireProgressUpdate(5, totalSteps, "Writing to output file.");

            // Avoid duplicates
            Set<PeakDescription> alreadyPrintedPeaks = new HashSet();

            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(outputFile, false));

                // Print non-deadend crosslinks
                for (Candidate c : allCandidates) {

                    if (isPrintComments()) {
                        writer.write("# " + c.getDescription());
                        writer.newLine();
                    }

                    final int fractionNum = c.getCandidateSpot().spot.getFractionNumber(getSpotOrder());

                    if (c instanceof CandidatePeakPair) {
                        CandidatePeakPair candidate = (CandidatePeakPair) c;

//                        writeInclusionListEntry(writer, candidate.peakPair.peak1, fractionNum, alreadyPrintedPeaks);
//                        writeInclusionListEntry(writer, candidate.peakPair.peak2, fractionNum, alreadyPrintedPeaks);

                        writeTabSeparatedCandidatePeakPair(writer, candidate, fractionNum);
                    } else if (c instanceof CandidatePeakPairsWithParent) {
                        CandidatePeakPairsWithParent candidate = (CandidatePeakPairsWithParent) c;

//                        writeInclusionListEntry(writer, candidate.lowerMassPeakPair.peak1, fractionNum, alreadyPrintedPeaks);
//                        writeInclusionListEntry(writer, candidate.higherMassPeakPair.peak1, fractionNum, alreadyPrintedPeaks);
//                        writeInclusionListEntry(writer, candidate.parentMassPeak, fractionNum, alreadyPrintedPeaks);

                        writeTabSeparatedCandidatePeakPairsWithParent(writer, candidate, fractionNum);
                    } else {
                        AssertionUtil.fail("Unrecognized class: " + c.getClass().getSimpleName());
                    }

                    if (isPrintComments()) {
                        writer.newLine();
                    }
                }

                writer.newLine();

                // Print deadend candidates
                for (CandidateSpot spot : candidateSpots) {
                    for (CandidateDeadEnd deadEnd : spot.deadEnds) {
                        if (isPrintComments()) {
                            writer.write("# " + deadEnd.getDescription());
                            writer.newLine();
                        }

                        final int fractionNum = deadEnd.getCandidateSpot().spot.getFractionNumber(getSpotOrder());
                        writeDeadEnd(writer, deadEnd, fractionNum);
                    }
                }
            } finally {
                IOUtil.safeClose(writer);
            }

            fireFinished();
        } catch (Exception e) {
            fireFailed(e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            // These peak lists were modified to use abs required intensity, so reset
            for (PeakListDescription pkl : peakListsToReset) {
                pkl.restoreAllPeakDescriptions();
            }
        }
    }

    private static final int columnSize = 10;
    private static final int decimalPointSize = 2;
    private static final String floatingPointFormat = "%-" + columnSize + "." + decimalPointSize + "f";
    private static final String integerFormat = "%-" + columnSize + "d";
    private static final String stringFormat = "%-" + columnSize + "s";
    private static final String xlinkFormat = floatingPointFormat + "\t" + floatingPointFormat + "\t" + floatingPointFormat + "\t" + floatingPointFormat + "\t" + floatingPointFormat + "\t" + integerFormat;
    private static final String peakPairFormat = stringFormat + "\t" + floatingPointFormat + "\t" + floatingPointFormat + "\t" + stringFormat + "\t" + stringFormat + "\t" + integerFormat;
    private static boolean isWriteXlinkHeaderTabSeparated = true;
    private static boolean isWriteDeadEndHeaderTabSeparated = true;

    /**
     *
     * @param writer
     * @throws IOException
     */
    private void writeXlinkHeaderTabSeparated(final BufferedWriter writer) throws IOException {
        if (isWriteXlinkHeaderTabSeparated) {
            // [parent] [p1`] [p1] [p2] [p2`] [fraction number]
            String format = stringFormat + "\t" + stringFormat + "\t" + stringFormat + "\t" + stringFormat + "\t" + stringFormat + "\t" + stringFormat;
            String line = String.format(format, "\"PARENT\"", "P1`", "\"P1\"", "\"P2`\"", "\"P2\"", "\"FRACTION\"");
            writer.write(line);
            writer.newLine();
            isWriteXlinkHeaderTabSeparated = false;
        }
    }

    /**
     *
     * @param writer
     * @throws IOException
     */
    private void writeDeadEndHeaderTabSeparated(final BufferedWriter writer) throws IOException {
        if (isWriteDeadEndHeaderTabSeparated) {
            // [p`] [p] [p1] [fraction number]
            String format = stringFormat + "\t" + stringFormat + "\t" + stringFormat + "\t" + stringFormat;
            String line = String.format(format, "\"P`\"", "\"P\"", "\"P1\"", "\"FRACTION\"");
            writer.write(line);
            writer.newLine();
            isWriteDeadEndHeaderTabSeparated = false;
        }
    }

    /**
     *
     * @param fractionNum
     * @param alreadyPrintedPeaks
     * @throws IOException
     */
    private void writeTabSeparatedCandidatePeakPairsWithParent(final BufferedWriter writer, CandidatePeakPairsWithParent p, final int fractionNum) throws IOException {
        writeXlinkHeaderTabSeparated(writer);

        // [parent] [p1`] [p1] [p2] [p2`] [fraction number]
        String line = String.format(xlinkFormat, p.parentMassPeak.getCentroid(), p.lowerMassPeakPair.peak1.getCentroid(), p.lowerMassPeakPair.peak2.getCentroid(), p.higherMassPeakPair.peak1.getCentroid(), p.higherMassPeakPair.peak2.getCentroid(), fractionNum);
        writer.write(line);
        writer.newLine();
    }

    /**
     *
     * @param fractionNum
     * @param alreadyPrintedPeaks
     * @throws IOException
     */
    private void writeTabSeparatedCandidatePeakPair(final BufferedWriter writer, CandidatePeakPair peakPair, final int fractionNum) throws IOException {
        writeXlinkHeaderTabSeparated(writer);

        // - [p1`] [p1] - - [fraction number]
        String line = String.format(peakPairFormat, "-", peakPair.peakPair.peak1.getCentroid(), peakPair.peakPair.peak2.getCentroid(), "-", "-", fractionNum);
        writer.write(line);
        writer.newLine();
    }

    /**
     *
     * @param fractionNum
     * @param alreadyPrintedPeaks
     * @throws IOException
     */
    private void writeDeadEnd(final BufferedWriter writer, CandidateDeadEnd deadEnd, final int fractionNum) throws IOException {
        writeDeadEndHeaderTabSeparated(writer);

        // [p`] [p] [p1] [fraction number]

        // Peaks might be missing, so build the format string
        StringBuffer formatStr = new StringBuffer();

        final boolean[] isNotNullArr = {
            (deadEnd.peakAPrime != null),
            (deadEnd.peakA != null),
            (deadEnd.peakA1 != null),};

        final Object[] parameters = {
            isNotNullArr[0] ? deadEnd.peakAPrime.getCentroid() : "-",
            isNotNullArr[1] ? deadEnd.peakA.getCentroid() : "-",
            isNotNullArr[2] ? deadEnd.peakA1.getCentroid() : "-",};

        for (boolean isNotNull : isNotNullArr) {
            if (isNotNull) {
                formatStr.append(floatingPointFormat);
            } else {
                formatStr.append(stringFormat);
            }
            formatStr.append("\t");
        }
        formatStr.append(integerFormat);

        String line = String.format(formatStr.toString(), parameters[0], parameters[1], parameters[2], fractionNum);
        writer.write(line);
        writer.newLine();
    }

    /**
     *
     * @param writer
     * @param centroid
     * @param fractionNum
     * @throws IOException
     */
    private void writeInclusionListEntry(final BufferedWriter writer, final PeakDescription peak, final int fractionNum, final Set<PeakDescription> alreadyPrintedPeaks) throws IOException {
        // m/z, m/z tol, fxn, fxn tol
        // E.g., 296.7, 0.3, 24, 2
        final double centroid = peak.getCentroid();

        final String line = centroid + ", " + massTolerance + ", " + fractionNum + ", " + 1;

        if (!alreadyPrintedPeaks.contains(peak)) {
            writer.write(line);
            writer.newLine();
            alreadyPrintedPeaks.add(peak);
        } else if (isPrintComments()) {
            writer.write("# Already included: " + line);
            writer.newLine();
        }
    }

    /**
     * 
     * @param intensity1
     * @param intensity2
     * @return
     */
    private static double intensityScore(double intensity1, double intensity2) {
        // If want a better "average" between intensities
//        return Math.sqrt(intensity1 * intensity2);

        // If want a higher intensity peak
        return Math.sqrt((intensity1 * intensity1) + (intensity2 * intensity2));
    }

    /**
     *
     * @param spot
     * @return
     */
    private int getCandidateDeadEndCount(List<Candidate> candidates) {

        int count = 0;

        for (Candidate c : candidates) {

            // If CandidatePeakPair, just one pair to check. Otherwise, check both
            // to see whether potential dead-ends.
            Set<PeakDescription> peaksToChecks = new HashSet();

            if (c instanceof CandidatePeakPair) {
                CandidatePeakPair candidate = (CandidatePeakPair) c;
                peaksToChecks.add(candidate.peakPair.peak2);

            } else if (c instanceof CandidatePeakPairsWithParent) {
                CandidatePeakPairsWithParent candidate = (CandidatePeakPairsWithParent) c;
                peaksToChecks.add(candidate.lowerMassPeakPair.peak2);
                peaksToChecks.add(candidate.higherMassPeakPair.peak2);
            } else {
                AssertionUtil.fail("Unrecognized class: " + c.getClass().getSimpleName());
            }

            // Looking for m2 + 86 = m+ peaks
            for (PeakDescription peak : peaksToChecks) {

                OTHERS:
                for (PeakDescription otherPeak : peak.getParent().getPeakDescriptions()) {

                    // Is the other peak in the mass tolerance range of this candidate peak?
                    if (otherPeak.getCentroid() < (peak.getCentroid() + massTolerance + getHighMassDifference()) && otherPeak.getCentroid() > (peak.getCentroid() - massTolerance + getHighMassDifference())) {
                        if (otherPeak.getIntensity() < peak.getIntensity()) {
                            count++;

                            // Since this peak is a candidate, check others
                            break OTHERS;
                        }
                    }
                }
            }
        }

        return count;
    }

    /**
     * 
     * @param spot
     * @return
     */
    private List<CandidateDeadEnd> getCandidateDeadEndsForSpot(CandidateSpot spot) {
        List<CandidateDeadEnd> deadEnds = new LinkedList();

        // REMEMBER:
        //
        //   A` + 112 = A
        //   A` + 198 = A1
        //   A + 86 = A1

        // If these are changed, make sure they are in increasing order
        final float[] deltasAPrime = {
            getMassDifference(), getMassDifference() + getHighMassDifference()
        };

        final float[] deltasA = {
            getHighMassDifference()};

        // Essentially, need to check deltas for A` and A, since A` might not be present
        final float[][] scenarios = {
            deltasAPrime, deltasA
        };

        SCENARIO:
        for (int scenarioIndex = 0; scenarioIndex < scenarios.length; scenarioIndex++) {

            final float[] deltas = scenarios[scenarioIndex];

            OUTER:
            for (PeakDescription peak1 : spot.peakList.getPeakDescriptions()) {

                //final PeakDescription[] deadEnd = {peak1, null, null};

                // Start at the scenarioIndex, which happens to also be the first peak
                // of the three possible peaks we (might) see
                final PeakDescription[] deadEnd = new PeakDescription[3];
                deadEnd[scenarioIndex] = peak1;

                INNER:
                for (PeakDescription peak2 : spot.peakList.getPeakDescriptions()) {
                    // if not peak2 > peak1, skip peak2
                    if (peak2.getCentroid() <= peak1.getCentroid()) {
                        continue INNER;
                    }

                    // if mass(peak1) + tolerance + max mass difference < mass(peak2), finished with peak1
                    if (peak1.getCentroid() + this.getMassTolerance() + deltas[deltas.length - 1] < peak2.getCentroid()) {
                        continue OUTER;
                    }

                    // If find a peak at specified offset, add to evidence array
                    DELTA_CHECK:
                    for (int i = 0; i < deltas.length; i++) {
                        final float delta = deltas[i];
                        final boolean isFoundDifference = PeakPairUtil.arePeaksSpecifiedDifference(peak1, peak2, delta, massTolerance);

                        if (isFoundDifference) {
                            // The index is a little funky, but includes:
                            //   * The scenario index (since might be starting at different initial offset)
                            //   * i since there might be more than one delta
                            //   And add 1 since this is the next peak!
                            final int peakIndex = scenarioIndex + i + 1;
                            deadEnd[peakIndex] = peak2;
                            break DELTA_CHECK;
                        }
                    }
                }

                // If we found evidence (more than one, since there is always one),
                // count it as a deadend!
                int nonNullCount = 0;
                for (int i = 0; i < deadEnd.length; i++) {
                    if (deadEnd[i] != null) {
                        nonNullCount++;
                    }
                }
                if (nonNullCount > 1) {
                    deadEnds.add(new CandidateDeadEnd(spot, deadEnd[0], deadEnd[1], deadEnd[2]));
                }
            }

        }

        return deadEnds;
    }

    /**
     *
     * @param spot
     * @return
     */
    private List<PeakPair> getPairsForSpot(CandidateSpot spot) {

        List<PeakPair> peaks = new LinkedList();

        OUTER:
        for (PeakDescription peak1 : spot.peakList.getPeakDescriptions()) {

            INNER:
            for (PeakDescription peak2 : spot.peakList.getPeakDescriptions()) {
                // if not peak2 > peak1, skip peak2
                if (peak2.getCentroid() <= peak1.getCentroid()) {
                    continue INNER;
                }

                // if peak1 + tolerance + mass difference < peak2, finished with peak1
                if (peak1.getCentroid() + this.getMassTolerance() + this.getMassDifference() < peak2.getCentroid()) {
                    continue OUTER;
                }

                final boolean isFoundCandidatePair = PeakPairUtil.arePeaksSpecifiedDifference(peak1, peak2, this.getMassDifference(), massTolerance);

                if (isFoundCandidatePair) {
                    PeakPair pair = new PeakPair(peak1, peak2, this, this.getMassDifference(), massTolerance);
                    peaks.add(pair);
                }
            }
        }

        return peaks;
    }

    /**
     * <p>Wraps run method in thread (minimum priority, daemon) and starts it. Useful for calling from JavaFX GUI.</p>
     */
    public void runOnSeparateThread() {
        Thread t = new Thread() {

            @Override()
            public void run() {
                InclusionListGenerator.this.run();
            }
        };

        t.setDaemon(true);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    /**
     *
     */
    public void fireFinished() {
        for (InclusionListGeneratorListener l : this.listeners) {
            l.noteFinished();
        }
    }

    /**
     *
     */
    public void fireFailed(String msg) {
        for (InclusionListGeneratorListener l : this.listeners) {
            l.noteFailed(msg);
        }
    }

    /**
     *
     * @param step
     * @param totalSteps
     * @param msg
     */
    public void fireProgressUpdate(int step, int totalSteps, String msg) {
        for (InclusionListGeneratorListener l : this.listeners) {
            l.noteProgressUpdate(step, totalSteps, msg);
        }
    }

    /**
     *
     * @param msg
     */
    public void fireNoteMessage(String msg) {
        for (InclusionListGeneratorListener l : this.listeners) {
            l.noteMessage(msg);
        }
    }

    /**
     * @return the spotSetDescription
     */
    public SpotSetDescription getSpotSetDescription() {
        return spotSetDescription;
    }

    /**
     * @param spotSetDescription the spotSetDescription to set
     */
    public void setSpotSetDescription(SpotSetDescription spotSetDescription) {
        this.spotSetDescription = spotSetDescription;
    }

    /**
     *
     * @param spotSetId
     */
    public void setSpotSetDescriptionById(String spotSetId) {
        for (SpotSetDescription nextSSD : this.instrument.getSpotDescriptions()) {
            if (nextSSD.getId().equals(spotSetId)) {
                setSpotSetDescription(nextSSD);
                return;
            }
        }
        throw new RuntimeException("Could not find spot set with id: " + spotSetId);
    }

    /**
     * @return the jobId
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * @param jobId the jobId to set
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * @return the requiredAbsoluteIntensity
     */
    public float getRequiredAbsoluteIntensity() {
        return requiredAbsoluteIntensity;
    }

    /**
     * @param requiredAbsoluteIntensity the requiredAbsoluteIntensity to set
     */
    public void setRequiredAbsoluteIntensity(float requiredAbsoluteIntensity) {
        this.requiredAbsoluteIntensity = requiredAbsoluteIntensity;
    }

    /**
     * @return the printComments
     */
    public boolean isPrintComments() {
        return printComments;
    }

    /**
     * @param printComments the printComments to set
     */
    public void setPrintComments(boolean printComments) {
        this.printComments = printComments;
    }

    /**
     * @return the spotOrder
     */
    public String getSpotOrder() {
        return spotOrder;
    }

    /**
     * @param spotOrder the spotOrder to set
     */
    public void setSpotOrder(String spotOrder) {
        this.spotOrder = spotOrder;
    }

    /**
     * @return the highMassOffet
     */
    public float getHighMassDifference() {
        return highMassDifference;
    }

    /**
     * @param highMassOffet the highMassOffet to set
     */
    public void setHighMassDifference(float highMassOffet) {
        this.highMassDifference = highMassOffet;
    }
}
