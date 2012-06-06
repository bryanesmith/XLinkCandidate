/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate.utils;

import junit.framework.TestCase;
import org.proteomecommons.t2util.PeakDescription;
import org.proteomecommons.xlinkcandidate.PeakPair;

/**
 *
 * @author besmit
 */
public class PeakPairUtilTest extends TestCase {

    public PeakPairUtilTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of arePeaksWithinTolerance method, of class PeakPairUtil.
     */
    public void testArePeaksWithinTolerance() {

        assertTrue(PeakPairUtil.arePeaksWithinTolerance(182.0, 182.0, 1.0));
        assertTrue(PeakPairUtil.arePeaksWithinTolerance(182.05, 182.0, 1.0));
        assertTrue(PeakPairUtil.arePeaksWithinTolerance(181.95, 182.0, 1.0));

        assertTrue(PeakPairUtil.arePeaksWithinTolerance(182.0, 183.0, 1.0));
        assertTrue(PeakPairUtil.arePeaksWithinTolerance(184.0, 183.0, 1.0));

        assertFalse(PeakPairUtil.arePeaksWithinTolerance(182.0, 183.01, 1.0));
        assertFalse(PeakPairUtil.arePeaksWithinTolerance(184.01, 183.0, 1.0));
    }

    /**
     * Test of arePeaksSpecifiedDifference method, of class PeakPairUtil.
     */
    public void testArePeaksSpecifiedDifference() {
        assertTrue(PeakPairUtil.arePeaksSpecifiedDifference(100.0, 200.0, 100.0, 1.0));

        assertTrue(PeakPairUtil.arePeaksSpecifiedDifference(100.0, 201.0, 100.0, 1.0));
        assertTrue(PeakPairUtil.arePeaksSpecifiedDifference(100.0, 199.0, 100.0, 1.0));
    }

    /**
     * Test of isFoundParentPeakForXLinks method, of class PeakPairUtil.
     */
    public void testIsFoundParentPeakForXLinksSimpleValues() {
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 200.0, 450.0, 150.0, 0, 1.0));
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 201.0, 450.0, 150.0, 0, 1.0));
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 200.0, 449.0, 150.0, 0, 1.0));
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 200.0, 450.0, 150.0, 0, 0.5));

        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 200.0, 450.0 - 1, 150.0, -1, 1.0));
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 201.0, 450.0 - 1, 150.0, -1, 1.0));
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 200.0, 449.0 - 2, 150.0, -2, 1.0));
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 200.0, 450.0 - 2, 150.0, -2, 0.5));

        assertFalse(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 201.0, 450.0, 150.0, 0, 0.5));
        assertFalse(PeakPairUtil.isFoundParentPeakForXLinks(100.0, 200.0, 449.0, 150.0, 0, 0.5));
    }

    /**
     * <p>Simple scenario without alternative chemistries</p>
     */
    public void testIsFoundParentPeakForXLinksPeakPairs1() {
        float massDiff = 112.0f;
        float massTol = 1.0f;
        PeakDescription peakA = new PeakDescription(300.0f, 1.0f, null);
        PeakDescription peakAPrime = new PeakDescription(300.0f - massDiff, 1.0f, null);
        PeakPair pair1 = new PeakPair(peakAPrime, peakA, null, massDiff, massTol);

        PeakDescription peakB = new PeakDescription(600.0f, 1.0f, null);
        PeakDescription peakBPrime = new PeakDescription(600.0f - massDiff, 1.0f, null);
        PeakPair pair2 = new PeakPair(peakBPrime, peakB, null, massDiff, massTol);

        PeakDescription parent = new PeakDescription(peakA.getCentroid() + peakB.getCentroid() - massDiff, 1.0f, null);
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(pair1, pair2, parent, massDiff, massTol));
    }

    /**
     * <p>Alternative chemistries, but positive</p>
     */
    public void testIsFoundParentPeakForXLinksPeakPairs2() {
        float massDiff1 = 112.0f;
        float massTol = 1.0f;
        PeakDescription peakA = new PeakDescription(300.0f, 1.0f, null);
        PeakDescription peakAPrime = new PeakDescription(300.0f - massDiff1, 1.0f, null);
        PeakPair pair1 = new PeakPair(peakAPrime, peakA, null, massDiff1, massTol);

        float massDiff2 = 198.0f;
        PeakDescription peakBPrime = new PeakDescription(600.0f, 1.0f, null);
        PeakDescription peakB1 = new PeakDescription(600.0f + massDiff2, 1.0f, null);
        PeakPair pair2 = new PeakPair(peakBPrime, peakB1, null, massDiff2, massTol);

        float massDiff = massDiff1;
        PeakDescription parent = new PeakDescription(peakA.getCentroid() + pair2.getCentroidForInterpolatedFragment() - massDiff, 1.0f, null);
        assertTrue(PeakPairUtil.isFoundParentPeakForXLinks(pair1, pair2, parent, massDiff, massTol));
    }
}
