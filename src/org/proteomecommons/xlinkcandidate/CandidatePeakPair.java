/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate;

/**
 *
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public class CandidatePeakPair extends Candidate {

    public final PeakPair peakPair;

    protected CandidatePeakPair(CandidateSpot spot, PeakPair peakPair) {
        super(spot);
        this.peakPair = peakPair;
    }
}
