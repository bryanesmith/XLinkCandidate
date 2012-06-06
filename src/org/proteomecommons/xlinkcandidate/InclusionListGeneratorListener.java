/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.proteomecommons.xlinkcandidate;

/**
 *
 * @author besmit
 */
public interface InclusionListGeneratorListener {
    /**
     *
     */
    public void noteFinished();

    /**
     *
     */
    public void noteFailed(String msg);

    /**
     * 
     * @param step
     * @param totalSteps
     * @param msg
     */
    public void noteProgressUpdate(int step, int totalSteps, String msg);

    /**
     * <p>A place for standard output and error, if tool decides to print them.</p>
     * @param msg
     */
    public void noteMessage(String msg);
}
