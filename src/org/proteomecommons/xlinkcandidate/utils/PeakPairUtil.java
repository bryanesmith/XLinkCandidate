/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate.utils;

import org.proteomecommons.t2util.PeakDescription;
import org.proteomecommons.xlinkcandidate.PeakPair;

/**
 *
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public class PeakPairUtil {

    /**
     *
     * @param lowerPeak
     * @param higherPeak
     * @param tolerance
     * @return
     */
    public static boolean arePeaksWithinTolerance(PeakDescription peak1, PeakDescription peak2, double tolerance) {
        return arePeaksWithinTolerance(peak1.getCentroid(), peak2.getCentroid(), tolerance);
    }

    /**
     *
     * @param peak1Centroid
     * @param peak2Centroid
     * @param tolerance
     * @return
     */
    public static boolean arePeaksWithinTolerance(double peak1Centroid, double peak2Centroid, double tolerance) {
        boolean upperSafe = peak1Centroid + tolerance >= peak2Centroid;
        boolean lowerSafe = peak1Centroid - tolerance <= peak2Centroid;
        return upperSafe && lowerSafe;
    }

    /**
     *
     * @param lowerPeak
     * @param higherPeak
     * @param massDifference
     * @param tolerance
     * @return
     */
    public static boolean arePeaksSpecifiedDifference(PeakDescription peak1, PeakDescription peak2, double difference, double tolerance) {
        return arePeaksSpecifiedDifference(peak1.getCentroid(), peak2.getCentroid(), difference, tolerance);
    }

    /**
     * 
     * @param peak1Centroid
     * @param peak2Centroid
     * @param difference
     * @param tolerance
     * @return
     */
    public static boolean arePeaksSpecifiedDifference(double peak1Centroid, double peak2Centroid, double difference, double tolerance) {
        boolean possibility1 = arePeaksWithinTolerance(peak1Centroid + difference, peak2Centroid, tolerance);
        boolean possibility2 = arePeaksWithinTolerance(peak2Centroid + difference, peak1Centroid, tolerance);
        return possibility1 || possibility2;
    }

    /**
     *
     * @param pair1
     * @param pair2
     * @param parentPeak
     * @param massDifference
     * @param tolerance
     * @return
     */
    public static boolean isFoundParentPeakForXLinks(PeakPair pair1, PeakPair pair2, PeakDescription parentPeak, double massDifference, double tolerance) {

        // Make difference negative since using higher peaks from each pair!
        return isFoundParentPeakForXLinks(pair1.getCentroidForInterpolatedFragment(), pair2.getCentroidForInterpolatedFragment(), parentPeak.getCentroid(), -massDifference, 0, tolerance);
    }

    /**
     *
     * @param pair1
     * @param pair2
     * @param parentPeak
     * @param massDifference
     * @param offset
     * @param tolerance
     * @return
     */
    public static boolean isFoundParentPeakForXLinks(PeakPair pair1, PeakPair pair2, PeakDescription parentPeak, double massDifference, double offset, double tolerance) {
        // Make difference negative since using higher peaks from each pair!
        return isFoundParentPeakForXLinks(pair1.getCentroidForInterpolatedFragment(), pair2.getCentroidForInterpolatedFragment(), parentPeak.getCentroid(), -massDifference, offset, tolerance);
    }

//    /**
//     *
//     * @param lowerPeak
//     * @param higherPeak
//     * @param parentPeak
//     * @param massDifference
//     * @param offset Negative or positive value to account for loss of protons, alternative chemistries, etc. Be careful! E.g, P = A + B -2 + 112, then this value should be -2
//     * @param tolerance
//     * @return
//     */
//    public static boolean isFoundParentPeakForXLinks(PeakDescription lowerPeak, PeakDescription higherPeak, PeakDescription parentPeak, double massDifference, double offset, double tolerance) {
//        return isFoundParentPeakForXLinks(lowerPeak.getCentroid(), higherPeak.getCentroid(), parentPeak.getCentroid(), massDifference, offset, tolerance);
//    }

    /**
     * 
     * @param lowerPeakCentroid
     * @param higherPeakCentroid
     * @param parentPeakCentroid
     * @param massDifference
     * @param offsetForProtonationAndAlternativeChemistry Negative or positive value to account for loss of protons, alternative chemistries, etc. Be careful! E.g, P = A + B -2 + 112, then this value should be -2
     * @param tolerance
     * @return
     */
    public static boolean isFoundParentPeakForXLinks(double lowerPeakCentroid, double higherPeakCentroid, double parentPeakCentroid, double massDifference, double offset, double tolerance) {
        double expectedParent = lowerPeakCentroid + higherPeakCentroid + massDifference + offset;
        return arePeaksWithinTolerance(expectedParent, parentPeakCentroid, tolerance);
    }
}
