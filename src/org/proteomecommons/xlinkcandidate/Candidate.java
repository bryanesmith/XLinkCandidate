/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.proteomecommons.xlinkcandidate;

/**
 *
 * @author Bryan Smith - bryanesmith@gmail.com
 */
public abstract class Candidate {
    private String description = "No description was provided.";
    private final CandidateSpot candidateSpot;

    protected Candidate(CandidateSpot candidateSpot) {
        this.candidateSpot = candidateSpot;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the candidateSpot
     */
    public CandidateSpot getCandidateSpot() {
        return candidateSpot;
    }


}
