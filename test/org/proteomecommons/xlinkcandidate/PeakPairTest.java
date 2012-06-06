/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate;

import junit.framework.TestCase;
import org.proteomecommons.t2util.PeakDescription;

/**
 *
 * @author bryan
 */
public class PeakPairTest extends TestCase {

    public void testConstructorFailsWrongOrder() throws Exception {
        PeakDescription peak1 = new PeakDescription(100.0f, 1.0f, null);
        PeakDescription peak2 = new PeakDescription(85.0f, 1.0f, null);
        final float massDifference = 112.0f;
        final float massTolerance = 1.0f;
        try {
            new PeakPair(peak1, peak2, null, massDifference, massTolerance);
            fail("Should have thrown exception.");
        } catch (Exception nope) {
            System.out.println("As expected... " + nope.getClass().getSimpleName() + ": " + nope.getMessage());
        }
    }

    public void testConstructorFailsNotWithinTolerance() throws Exception {
        PeakDescription peak1 = new PeakDescription(100.0f, 1.0f, null);
        PeakDescription peak2 = new PeakDescription(214.0f, 1.0f, null);
        final float massDifference = 112.0f;
        final float massTolerance = 1.0f;
        try {
            new PeakPair(peak1, peak2, null, massDifference, massTolerance);
            fail("Should have thrown exception.");
        } catch (Exception nope) {
            System.out.println("As expected... " + nope.getClass().getSimpleName() + ": " + nope.getMessage());
        }
    }

    public void testConstructorSucceeds() throws Exception {
        PeakDescription peak1 = new PeakDescription(100.0f, 1.0f, null);
        PeakDescription peak2 = new PeakDescription(214.0f, 1.0f, null);
        final float massDifference = 112.0f;
        final float massTolerance = 2.0f;
        new PeakPair(peak1, peak2, null, massDifference, massTolerance);
    }
}
