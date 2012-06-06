/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate;

import java.util.HashSet;
import java.util.Set;
import org.proteomecommons.xlinkcandidate.utils.TextUtil;

/**
 *
 * @author bryan
 */
public class FindParentsUsingHardCodedOffsetsResult {

    private boolean found;
    private final Set<CandidatePeakPairsWithParent> candidatesWithParent;

    /**
     *
     */
    public FindParentsUsingHardCodedOffsetsResult() {
        found = false;
        candidatesWithParent = new HashSet();
    }

    /**
     * @return the found
     */
    public boolean isFound() {
        return found;
    }

    /**
     * @param found the found to set
     */
    public void setFound(boolean found) {
        this.found = found;
    }

    /**
     * @return the candidatesWithParent
     */
    public Set<CandidatePeakPairsWithParent> getCandidatesWithParent() {
        return candidatesWithParent;
    }

    /**
     * @param candidatesWithParent the candidatesWithParent to set
     */
    public void addCandidatesWithParent(CandidatePeakPairsWithParent candidateWithParent) {
        this.candidatesWithParent.add(candidateWithParent);
    }

    @Override()
    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("Found: " + this.isFound());

        for (CandidatePeakPairsWithParent c : this.getCandidatesWithParent()) {
            str.append(TextUtil.getNewLine() + "    * " + c.getDescription());
        }

        return str.toString();
    }
}
