package org.thorax.grafzahlenplugin;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.thorax.grafzahlenplugin.Term.TermGenerator;
import org.thorax.grafzahlenplugin.Term.TermOperator;
import org.thorax.grafzahlenplugin.Term.TermSegment;

public class TermTest {

    @Test
    public void testCalcSubSeg() {

        //Build sub Seg 1
        TermSegment seg1_1 = new TermSegment(TermOperator.ADD, 3);
        TermSegment seg1_2 = new TermSegment(TermOperator.MULT, 3);
        TermSegment seg1_3 = new TermSegment(TermOperator.NONE, 3);
        TermSegment[] termSeg1_1 = {
                seg1_1,
                seg1_2,
                seg1_3
        };

        TermSegment termSegment1 = new TermSegment(TermOperator.NONE, termSeg1_1);

        Assertions.assertEquals(seg1_1.calcTermSeg(seg1_2).getValue(), 6d, "seg1_1 und seg1_2 ergeben nicht 6");
        Assertions.assertEquals(seg1_2.calcTermSeg(seg1_3).getValue(), 9d, "seg1_2 und seg1_3 ergeben nicht 9");
        TermSegment newSeg1_2 = seg1_2.calcTermSeg(seg1_3);
        Assertions.assertEquals(seg1_1.calcTermSeg(newSeg1_2).getValue(), 12d, "3+3*3 ist nicht 12");
        Assertions.assertEquals(termSegment1.calcSubSeg(), 12d, "seg1 calcsubSeg ist nicht 12");
        Assertions.assertEquals("3 + 3 * 3", termSegment1.toStringValuesRounded(0), "termSegment1 toString precision 0 ist nicht 3 + 3 * 3");
        Assertions.assertTrue(termSegment1.valueEquals(12, 0), "termseg1 wert ist nicht 12 bei prezision 0");

        //Build sub Seg 2
        TermSegment seg2_1 = new TermSegment(TermOperator.MULT, 3d);
        TermSegment seg2_2 = new TermSegment(TermOperator.NONE, 3d);
        TermSegment[] termSeg2 = {
                seg2_1,
                seg2_2
        };
        TermSegment termSegment2 = new TermSegment(TermOperator.NONE, termSeg2);

        Assertions.assertEquals(seg2_1.calcTermSeg(seg2_2).getValue(), 9d, "3*3 ist nicht 9");
        Assertions.assertEquals(termSegment2.calcSubSeg(), 9d, "termseg2 calcsubseg ist nicht 9");
        Assertions.assertEquals("3 * 3", termSegment2.toStringValuesRounded(0), "termSegment2 to string precision 0 is tnicht 3 * 3");
        Assertions.assertTrue(termSegment2.valueEquals(9, 0), "termseg2 wert ist nicht gleich 9 prezision 0");

        //Build sub Seg 3
        TermSegment seg3_1 = new TermSegment(TermOperator.DIV, 3d);
        TermSegment seg3_2 = new TermSegment(TermOperator.NONE, 3d);
        TermSegment[] termSeg3 = {
                seg3_1,
                seg3_2
        };
        TermSegment termSegment3 = new TermSegment(TermOperator.NONE, termSeg3);

        Assertions.assertEquals(seg3_1.calcTermSeg(seg3_2).getValue(), 1d, "3 / 3 ist nicht 1");
        Assertions.assertEquals(termSegment3.calcSubSeg(), 1d, "termSeg3 calcSubSeg ist nicht 1");
        Assertions.assertEquals("3 / 3", termSegment3.toStringValuesRounded(0), "termSegment3 to String precision 0 ist nicht 3 / 3");
        Assertions.assertTrue(termSegment3.valueEquals(1, 0), "termseg3 wert nicht 1 bei präzision 0");

        //Build sub Seg 4
        TermSegment seg4_1 = new TermSegment(TermOperator.MULT, 3);
        TermSegment seg4_2 = new TermSegment(TermOperator.NONE,
                new TermSegment[]{
                        new TermSegment(TermOperator.ADD, 3),
                        new TermSegment(TermOperator.NONE, 3)
                });
        TermSegment[] termSeg4 = {seg4_1, seg4_2};
        TermSegment termSegment4 = new TermSegment(TermOperator.NONE, termSeg4);

        Assertions.assertEquals(6, seg4_2.calcSubSeg(), "3 + 3 ist nicht 6");
        Assertions.assertEquals(18, seg4_1.calcTermSeg(seg4_2).getValue(), "3 + 6 sind nicht 18");
        Assertions.assertEquals(18, termSegment4.calcSubSeg(), "3 * (3 + 3) ist nicht 18");
        Assertions.assertTrue(termSegment4.valueEquals(18, 0), "termseg4 wert nicht 6 bei präzision 0");

        TermSegment[] termseg5 = {
                new TermSegment(TermOperator.MULT, 5),
                new TermSegment(TermOperator.NONE, 5),
        };
        TermSegment[] termseg6 = {
                new TermSegment(TermOperator.MULT, 5),
                new TermSegment(TermOperator.NONE, 5)
        };

        TermSegment termseg7 = new TermSegment(TermOperator.MULT, termseg5);
        TermSegment termseg8 = new TermSegment(TermOperator.NONE, termseg6);

        TermSegment termSegment5 = new TermSegment(TermOperator.NONE, new TermSegment[]{termseg7, termseg8});
        System.out.println(termSegment5.toStringValuesRounded(0));
        System.out.println(termSegment5.toString());

        double valueTermSeg5 = termSegment5.getValue();
        Assertions.assertTrue(termSegment5.valueEquals(valueTermSeg5, 0), "not equal");
    }

    @Test
    public void testTermGen() {

        TermSegment term3 = TermGenerator.newTerm().maxDepth(3).build();
        Assertions.assertEquals(3, term3.getDepth(), "term3 ist nicht 3 tief");

        TermSegment term4 = TermGenerator.newTerm().termCount(5).build();
        Assertions.assertEquals(5, term4.getSubSegment().length, "term4 hat nicht 5 SubSegmente");

        TermSegment term5 = TermGenerator.newTerm().termCount(10).valueRange(-15, 15).build();
        for (TermSegment term :
                term4.getSubSegment()) {
            Assertions.assertTrue(-15 < term.getValue(), "term4 nicht  alle werte sind größer als -15");
            Assertions.assertTrue(15 > term.getValue(), "term4 nicht alle werte sind kleiner als 15");
        };

        TermSegment term1 = TermGenerator.newTerm().maxDepth(2).valueRange(-10, 10).termCount(3).build();
        Assertions.assertEquals(3, term1.getSubSegment().length, "term1 ist nicht 3 Subterme lang");
        /*
        for (TermSegment term :
                term1.getSubSegment()) {
            Assertions.assertTrue(-10 < term.getValue(), "term1 manche werte sind nicht größer als -10");
            Assertions.assertTrue(10 > term.getValue(), "term1 manche werte sind nicht kleiner als 10");
        }
         */
        Assertions.assertEquals(2, term1.getDepth(), "term1 ist nicht 2 tief");

        TermSegment term2 = TermGenerator.newTerm().build();
        Assertions.assertEquals(2, term2.getSubSegment().length, "term2 ist nicht 2 Subterme lang");
        for (TermSegment term :
                term2.getSubSegment()) {
            Assertions.assertTrue(-5 < term.getValue(), "term2 manche werte sind nicht größer als -5");
            Assertions.assertTrue(5 > term.getValue(), "term2 manche werte sind nicht kleiner als 5");
        }
        Assertions.assertEquals(1, term2.getDepth(), "term2 ist nicht 1 tief");
    }

    @Test
    public void testTermGenTerms() {
        TermSegment termSegment1 = TermGenerator.newTerm().termCount(2).maxDepth(2).valueRange(-20, 20).build();
        System.out.println(termSegment1.toStringValuesRounded(0));
        double value = termSegment1.calcSubSeg();
        Assertions.assertTrue(termSegment1.valueEquals(value, 0), "nicht gleich");
    }


}
