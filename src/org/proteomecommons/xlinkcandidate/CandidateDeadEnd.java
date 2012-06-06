/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.proteomecommons.xlinkcandidate;

import org.proteomecommons.t2util.PeakDescription;

/**
 *
 * @author bryan
 */
public class CandidateDeadEnd extends Candidate {
    public final PeakDescription peakAPrime, peakA, peakA1;

    /**
     *
     * @param spot
     * @param peakAPrime mass(peakAPrime) = mass(peakA) - 112
     * @param peakA
     * @param peakA1 mass(peakA1) = mass(peakA) + 86
     */
    public CandidateDeadEnd(CandidateSpot spot, PeakDescription peakAPrime, PeakDescription peakA, PeakDescription peakA1) {
        super(spot);
        this.peakAPrime = peakAPrime;
        this.peakA = peakA;
        this.peakA1 = peakA1;
    }
}
