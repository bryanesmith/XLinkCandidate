/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.proteomecommons.xlinkcandidate;

import java.util.LinkedList;
import java.util.List;
import org.proteomecommons.t2util.PeakListDescription;
import org.proteomecommons.t2util.SpotDescription;

/**
 *
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public class CandidateSpot {
    protected final SpotDescription spot;
    protected final PeakListDescription peakList;
    protected final List<PeakPair> pairs;
    protected final List<CandidateDeadEnd> deadEnds;

    private String explanation = null;

    public CandidateSpot(final SpotDescription spot, final PeakListDescription peakList) {
        this.spot = spot;
        this.peakList = peakList;
        this.deadEnds = new LinkedList();
        this.pairs = new LinkedList();
    }

    /**
     * @return the explanation
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * @param explanation the explanation to set
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
