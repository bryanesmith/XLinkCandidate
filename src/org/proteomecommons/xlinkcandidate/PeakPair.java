/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate;

import org.proteomecommons.t2util.PeakDescription;
import org.proteomecommons.xlinkcandidate.utils.PeakPairUtil;

/**
 *
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public class PeakPair implements Comparable {

    public final PeakDescription peak1, peak2;
    private final InclusionListGenerator tool;
    public final float massDifference, massTolerance;

    public PeakPair(final PeakDescription peak1, final PeakDescription peak2, final InclusionListGenerator tool, final float massDifference, final float massTolerance) {
        
        if (peak1.getCentroid() >= peak2.getCentroid()) {
            throw new RuntimeException("peak1 {"+peak1.getCentroid()+"} should be less than peak2 {"+peak2.getCentroid()+"}, but isn't.");
        }
        
        if (!PeakPairUtil.arePeaksSpecifiedDifference(peak1, peak2, massDifference, massTolerance)) {
            throw new RuntimeException("Assertion failed: peak1 {"+peak1.getCentroid()+"} and peak2 {"+peak2.getCentroid()+"} are not within specified distance of "+massDifference+" +- "+massTolerance);
        }

        this.peak1 = peak1;
        this.peak2 = peak2;
        this.tool = tool;
        this.massDifference = massDifference;
        this.massTolerance = massTolerance;
    }

    /**
     * <p>Are pairs the same even though different spots?</p>
     * @param otherPair
     * @return
     */
    public boolean isEquivalentPair(PeakPair otherPair) {
        final double tolerance = this.tool.getMassTolerance();
        return PeakPairUtil.arePeaksWithinTolerance(this.peak1, otherPair.peak1, tolerance) && PeakPairUtil.arePeaksWithinTolerance(this.peak2, otherPair.peak2, tolerance);
    }

    /**
     *
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        if (o instanceof PeakPair) {
            PeakPair other = (PeakPair) o;

            // Sort by higher peak since multipe mass differences might be used.
            // E.g, A - 112 = A', A - 196 = A", etc.
            final float thisCent = this.peak2.getCentroid();
            final float otherCent = other.peak2.getCentroid();
            if (thisCent < otherCent) {
                return -1;
            } else if (thisCent > otherCent) {
                return 1;
            } else {
                return 0;
            }
        }
        throw new RuntimeException("Parameter must be of type PeakPair");
    }
    
    /**
     * <p>This pair could be any two of {A`, A, A1}. This method returns the centroid for A, whether or not it actually exists.</p>
     * 
     */
    public float getCentroidForInterpolatedFragment() {
        int intDiff = (int)massDifference;
        
        switch (intDiff) {
            case 86:
                // A and A1
                return peak1.getCentroid();
            case 112:
                // A` and A
                return peak2.getCentroid();
            case 198:
                // A` and A1
                return peak1.getCentroid() + 112.0f;
            default:
                throw new RuntimeException("Unrecognized mass different: "+intDiff+" (have the requirements changed?)");
        }
    }

}
