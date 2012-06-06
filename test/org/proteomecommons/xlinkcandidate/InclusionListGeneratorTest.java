/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.proteomecommons.xlinkcandidate;

import junit.framework.TestCase;

/**
 *
 * @author bryan
 */
public class InclusionListGeneratorTest extends TestCase {

//    /**
//     * <p></p>
//     * @throws Exception
//     */
//    public void testFindParentsUsingHardCodedOffsetsPositive1() throws Exception {
//
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[0], 86.0f);
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[1], -198.0f);
//
//        final float massDifference = 112.0f;
//        final float massTolerance = 1.0f;
//
//        List<PeakDescription> peakDescriptions = new LinkedList();
//
//        // Create two peak pairs with a parent (no evidence), and four decoys, and one supporting peak
//        final float intensity = 1.0f;
//        PeakDescription peakAPrime = new PeakDescription(100.0f, intensity, null);
//        peakDescriptions.add(peakAPrime);
//        PeakDescription peakA = new PeakDescription(100.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakA);
//
//        PeakDescription peakBPrime = new PeakDescription(400.0f, intensity, null);
//        peakDescriptions.add(peakBPrime);
//        PeakDescription peakB = new PeakDescription(400.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakB);
//
//        PeakDescription parentPeak = new PeakDescription(peakAPrime.getCentroid() + peakB.getCentroid(), intensity, null);
//        peakDescriptions.add(parentPeak);
//
//        PeakDescription decoy1Peak = new PeakDescription(25.0f, intensity, null);
//        peakDescriptions.add(decoy1Peak);
//        PeakDescription decoy2Peak = new PeakDescription(50.0f, intensity, null);
//        peakDescriptions.add(decoy2Peak);
//        PeakDescription decoy3Peak = new PeakDescription(75.0f, intensity, null);
//        peakDescriptions.add(decoy3Peak);
//        PeakDescription decoy4Peak = new PeakDescription(125.0f, intensity, null);
//        peakDescriptions.add(decoy4Peak);
//
//        PeakDescription supportingPeak = new PeakDescription(parentPeak.getCentroid() + InclusionListGenerator.PARENT_MASS_OFFSETS[0], intensity, null);
//        peakDescriptions.add(supportingPeak);
//
//        assertEquals("Expecting certain number of peaks.", 10, peakDescriptions.size());
//
//        PeakPair lowerPeakPair = new PeakPair(peakAPrime, peakA, null, massDifference, massTolerance);
//        PeakPair higherPeakPair = new PeakPair(peakBPrime, peakB, null, massDifference, massTolerance);
//
//        FindParentsUsingHardCodedOffsetsResult result = InclusionListGenerator.findParentsUsingHardCodedOffsets(peakDescriptions, lowerPeakPair, higherPeakPair, "testing", null, massDifference, massTolerance);
//        assertNotNull("Shouldn't be null.", result);
//
//        System.out.println("-----------------------------------------------------------------------");
//        System.out.println(" Result: " + result);
//        System.out.println("-----------------------------------------------------------------------");
//
//        assertTrue("Should have found pairs with parent anything.", result.isFound());
//        assertEquals("Expecting one pair of peak pairs with parent.", 1, result.getCandidatesWithParent().size());
//
//        CandidatePeakPairsWithParent c = result.getCandidatesWithParent().toArray(new CandidatePeakPairsWithParent[0])[0];
////        assertEquals("Expecting certain type of result.", CandidatePeakPairsWithParentType.ParentPeakWithAlternativePeaks, c.type);
//    }
//
//    /**
//     * <p></p>
//     * @throws Exception
//     */
//    public void testFindParentsUsingHardCodedOffsetsPositive1NoParent() throws Exception {
//
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[0], 86.0f);
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[1], -198.0f);
//
//        final float massDifference = 112.0f;
//        final float massTolerance = 1.0f;
//
//        List<PeakDescription> peakDescriptions = new LinkedList();
//
//        // Create two peak pairs with a parent (no evidence), and four decoys, and one supporting peak
//        final float intensity = 1.0f;
//        PeakDescription peakAPrime = new PeakDescription(100.0f, intensity, null);
//        peakDescriptions.add(peakAPrime);
//        PeakDescription peakA = new PeakDescription(100.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakA);
//
//        PeakDescription peakBPrime = new PeakDescription(400.0f, intensity, null);
//        peakDescriptions.add(peakBPrime);
//        PeakDescription peakB = new PeakDescription(400.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakB);
//
//        // Create but don't add!
//        PeakDescription parentPeak = new PeakDescription(peakAPrime.getCentroid() + peakB.getCentroid(), intensity, null);
////        peakDescriptions.add(parentPeak);
//
//        PeakDescription decoy1Peak = new PeakDescription(25.0f, intensity, null);
//        peakDescriptions.add(decoy1Peak);
//        PeakDescription decoy2Peak = new PeakDescription(50.0f, intensity, null);
//        peakDescriptions.add(decoy2Peak);
//        PeakDescription decoy3Peak = new PeakDescription(75.0f, intensity, null);
//        peakDescriptions.add(decoy3Peak);
//        PeakDescription decoy4Peak = new PeakDescription(125.0f, intensity, null);
//        peakDescriptions.add(decoy4Peak);
//
//        PeakDescription supportingPeak = new PeakDescription(parentPeak.getCentroid() + InclusionListGenerator.PARENT_MASS_OFFSETS[0], intensity, null);
//        peakDescriptions.add(supportingPeak);
//
//        assertEquals("Expecting certain number of peaks.", 9, peakDescriptions.size());
//
//        PeakPair lowerPeakPair = new PeakPair(peakAPrime, peakA, null, massDifference, massTolerance);
//        PeakPair higherPeakPair = new PeakPair(peakBPrime, peakB, null, massDifference, massTolerance);
//
//        FindParentsUsingHardCodedOffsetsResult result = InclusionListGenerator.findParentsUsingHardCodedOffsets(peakDescriptions, lowerPeakPair, higherPeakPair, "testing", null, massDifference, massTolerance);
//        assertNotNull("Shouldn't be null.", result);
//
//        System.out.println("-----------------------------------------------------------------------");
//        System.out.println(" Result: " + result);
//        System.out.println("-----------------------------------------------------------------------");
//
//        assertTrue("Should have found pairs with parent anything.", result.isFound());
//        assertEquals("Expecting one pair of peak pairs with parent.", 1, result.getCandidatesWithParent().size());
//
//        CandidatePeakPairsWithParent c = result.getCandidatesWithParent().toArray(new CandidatePeakPairsWithParent[0])[0];
////        assertEquals("Expecting certain type of result.", CandidatePeakPairsWithParentType.NoParentPeakButAlternativePeaks, c.type);
//    }
//
//    /**
//     * <p></p>
//     * @throws Exception
//     */
//    public void testFindParentsUsingHardCodedOffsetsPositive2() throws Exception {
//
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[0], 86.0f);
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[1], -198.0f);
//
//        final float massDifference = 112.0f;
//        final float massTolerance = 1.0f;
//
//        List<PeakDescription> peakDescriptions = new LinkedList();
//
//        // Create two peak pairs with a parent (no evidence), and three decoys, and two supporting peak
//        final float intensity = 1.0f;
//        PeakDescription peakAPrime = new PeakDescription(100.0f, intensity, null);
//        peakDescriptions.add(peakAPrime);
//        PeakDescription peakA = new PeakDescription(100.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakA);
//
//        PeakDescription peakBPrime = new PeakDescription(400.0f, intensity, null);
//        peakDescriptions.add(peakBPrime);
//        PeakDescription peakB = new PeakDescription(400.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakB);
//
//        PeakDescription parentPeak = new PeakDescription(peakAPrime.getCentroid() + peakB.getCentroid(), intensity, null);
//        peakDescriptions.add(parentPeak);
//
//        PeakDescription decoy1Peak = new PeakDescription(25.0f, intensity, null);
//        peakDescriptions.add(decoy1Peak);
//        PeakDescription decoy2Peak = new PeakDescription(50.0f, intensity, null);
//        peakDescriptions.add(decoy2Peak);
//        PeakDescription decoy3Peak = new PeakDescription(75.0f, intensity, null);
//        peakDescriptions.add(decoy3Peak);
//
//        PeakDescription supportingPeak1 = new PeakDescription(parentPeak.getCentroid() + InclusionListGenerator.PARENT_MASS_OFFSETS[0], intensity, null);
//        peakDescriptions.add(supportingPeak1);
//
//        PeakDescription supportingPeak2 = new PeakDescription(parentPeak.getCentroid() + InclusionListGenerator.PARENT_MASS_OFFSETS[1], intensity, null);
//        peakDescriptions.add(supportingPeak2);
//
//        assertEquals("Expecting certain number of peaks.", 10, peakDescriptions.size());
//
//        PeakPair lowerPeakPair = new PeakPair(peakAPrime, peakA, null, massDifference, massTolerance);
//        PeakPair higherPeakPair = new PeakPair(peakBPrime, peakB, null, massDifference, massTolerance);
//
//        FindParentsUsingHardCodedOffsetsResult result = InclusionListGenerator.findParentsUsingHardCodedOffsets(peakDescriptions, lowerPeakPair, higherPeakPair, "testing", null, massDifference, massTolerance);
//        assertNotNull("Shouldn't be null.", result);
//
//        System.out.println("-----------------------------------------------------------------------");
//        System.out.println(" Result: " + result);
//        System.out.println("-----------------------------------------------------------------------");
//
//        assertTrue("Should have found pairs with parent anything.", result.isFound());
//        assertEquals("Expecting one pair of peak pairs with parent.", 1, result.getCandidatesWithParent().size());
//
//        CandidatePeakPairsWithParent c = result.getCandidatesWithParent().toArray(new CandidatePeakPairsWithParent[0])[0];
////        assertEquals("Expecting certain type of result.", CandidatePeakPairsWithParentType.ParentPeakWithAlternativePeaks, c.type);
//    }
//
//    /**
//     * <p></p>
//     * @throws Exception
//     */
//    public void testFindParentsUsingHardCodedOffsetsPositive2NoParent() throws Exception {
//
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[0], 86.0f);
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[1], -198.0f);
//
//        final float massDifference = 112.0f;
//        final float massTolerance = 1.0f;
//
//        List<PeakDescription> peakDescriptions = new LinkedList();
//
//        // Create two peak pairs with a parent (no evidence), and three decoys, and two supporting peak
//        final float intensity = 1.0f;
//        PeakDescription peakAPrime = new PeakDescription(100.0f, intensity, null);
//        peakDescriptions.add(peakAPrime);
//        PeakDescription peakA = new PeakDescription(100.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakA);
//
//        PeakDescription peakBPrime = new PeakDescription(400.0f, intensity, null);
//        peakDescriptions.add(peakBPrime);
//        PeakDescription peakB = new PeakDescription(400.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakB);
//
//        // Create, but don't add
//        PeakDescription parentPeak = new PeakDescription(peakAPrime.getCentroid() + peakB.getCentroid(), intensity, null);
////        peakDescriptions.add(parentPeak);
//
//        PeakDescription decoy1Peak = new PeakDescription(25.0f, intensity, null);
//        peakDescriptions.add(decoy1Peak);
//        PeakDescription decoy2Peak = new PeakDescription(50.0f, intensity, null);
//        peakDescriptions.add(decoy2Peak);
//        PeakDescription decoy3Peak = new PeakDescription(75.0f, intensity, null);
//        peakDescriptions.add(decoy3Peak);
//
//        PeakDescription supportingPeak1 = new PeakDescription(parentPeak.getCentroid() + InclusionListGenerator.PARENT_MASS_OFFSETS[0], intensity, null);
//        peakDescriptions.add(supportingPeak1);
//
//        PeakDescription supportingPeak2 = new PeakDescription(parentPeak.getCentroid() + InclusionListGenerator.PARENT_MASS_OFFSETS[1], intensity, null);
//        peakDescriptions.add(supportingPeak2);
//
//        assertEquals("Expecting certain number of peaks.", 9, peakDescriptions.size());
//
//        PeakPair lowerPeakPair = new PeakPair(peakAPrime, peakA, null, massDifference, massTolerance);
//        PeakPair higherPeakPair = new PeakPair(peakBPrime, peakB, null, massDifference, massTolerance);
//
//        FindParentsUsingHardCodedOffsetsResult result = InclusionListGenerator.findParentsUsingHardCodedOffsets(peakDescriptions, lowerPeakPair, higherPeakPair, "testing", null, massDifference, massTolerance);
//        assertNotNull("Shouldn't be null.", result);
//
//        System.out.println("-----------------------------------------------------------------------");
//        System.out.println(" Result: " + result);
//        System.out.println("-----------------------------------------------------------------------");
//
//        assertTrue("Should have found pairs with parent anything.", result.isFound());
//        assertEquals("Expecting one pair of peak pairs with parent.", 1, result.getCandidatesWithParent().size());
//
//        CandidatePeakPairsWithParent c = result.getCandidatesWithParent().toArray(new CandidatePeakPairsWithParent[0])[0];
////        assertEquals("Expecting certain type of result.", CandidatePeakPairsWithParentType.NoParentPeakButAlternativePeaks, c.type);
//    }
//
//    /**
//     * <p></p>
//     * @throws Exception
//     */
//    public void testFindParentsUsingHardCodedOffsetsNegative() throws Exception {
//
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[0], 86.0f);
////        assertEquals("Expecting certain constant. If changed, need to update tests.", InclusionListGenerator.PARENT_MASS_OFFSETS[1], -198.0f);
//
//        final float massDifference = 112.0f;
//        final float massTolerance = 1.0f;
//
//        List<PeakDescription> peakDescriptions = new LinkedList();
//
//        // Create two peak pairs without a parent, and six decoys
//        final float intensity = 1.0f;
//        PeakDescription peakAPrime = new PeakDescription(100.0f, intensity, null);
//        peakDescriptions.add(peakAPrime);
//        PeakDescription peakA = new PeakDescription(100.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakA);
//
//        PeakDescription peakBPrime = new PeakDescription(400.0f, intensity, null);
//        peakDescriptions.add(peakBPrime);
//        PeakDescription peakB = new PeakDescription(400.0f + massDifference, intensity, null);
//        peakDescriptions.add(peakB);
//
//        PeakDescription decoy1Peak = new PeakDescription(25.0f, intensity, null);
//        peakDescriptions.add(decoy1Peak);
//        PeakDescription decoy2Peak = new PeakDescription(50.0f, intensity, null);
//        peakDescriptions.add(decoy2Peak);
//        PeakDescription decoy3Peak = new PeakDescription(75.0f, intensity, null);
//        peakDescriptions.add(decoy3Peak);
//        PeakDescription decoy4Peak = new PeakDescription(125.0f, intensity, null);
//        peakDescriptions.add(decoy4Peak);
//        PeakDescription decoy5Peak = new PeakDescription(150.0f, intensity, null);
//        peakDescriptions.add(decoy5Peak);
//        PeakDescription decoy6Peak = new PeakDescription(175.0f, intensity, null);
//        peakDescriptions.add(decoy6Peak);
//
//        assertEquals("Expecting certain number of peaks.", 10, peakDescriptions.size());
//
//        PeakPair lowerPeakPair = new PeakPair(peakAPrime, peakA, null, massDifference, massTolerance);
//        PeakPair higherPeakPair = new PeakPair(peakBPrime, peakB, null, massDifference, massTolerance);
//
//        FindParentsUsingHardCodedOffsetsResult result = InclusionListGenerator.findParentsUsingHardCodedOffsets(peakDescriptions, lowerPeakPair, higherPeakPair, "testing", null, massDifference, massTolerance);
//        assertNotNull("Shouldn't be null.", result);
//
//        System.out.println("-----------------------------------------------------------------------");
//        System.out.println(" Result: " + result);
//        System.out.println("-----------------------------------------------------------------------");
//
//        assertFalse("Shouldn't have found anything.", result.isFound());
//        assertEquals("Expecting no pairs with parents.", 0, result.getCandidatesWithParent().size());
//    }
}
