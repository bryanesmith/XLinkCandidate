/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.proteomecommons.xlinkcandidate;

import org.proteomecommons.t2util.PeakDescription;

/**
 *
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public class CandidatePeakPairsWithParent extends Candidate {
    public final PeakPair lowerMassPeakPair, higherMassPeakPair;
    public final PeakDescription parentMassPeak;

    protected CandidatePeakPairsWithParent(CandidateSpot spot, PeakPair lowerMassPeakPair, PeakPair higherMassPeakPair, PeakDescription parentMassPeak) {
        super(spot);
        this.lowerMassPeakPair = lowerMassPeakPair;
        this.higherMassPeakPair = higherMassPeakPair;
        this.parentMassPeak = parentMassPeak;
    }
}
