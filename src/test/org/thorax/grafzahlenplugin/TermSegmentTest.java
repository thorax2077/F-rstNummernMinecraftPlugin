package org.thorax.grafzahlenplugin;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TermSegmentTest {

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
    }
}
