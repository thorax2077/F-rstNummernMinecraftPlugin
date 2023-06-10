package org.thorax.grafzahlenplugin.Term;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class TermGenerator implements Cloneable{



    private final Random rand = new Random();

    private int maxDepth = 1;

    private int termCount = 2;

    private double minValue = -5d;

    private double maxValue = 5d;

    private boolean useTermSegments = true;

    private boolean TermToGenerateIsToplevel = true;


    private TermSegment[] termSegmentsToUse = new TermSegment[0];


    public static TermGenerator newTerm() {
        return new TermGenerator();
    }


    public TermGenerator setTermToGenerateIsToplevel(boolean termToGenerateIsToplevel) {
        TermToGenerateIsToplevel = termToGenerateIsToplevel;
        return this;
    }

    public TermGenerator makeCopy() {
        return (TermGenerator) this.clone();
    }

    public TermGenerator maxDepth(int depth) {
        this.maxDepth = depth;
        return this;
    }

    public TermGenerator termCount(int count) {
        this.termCount = count;
        return this;
    }

    public TermGenerator valueRange(double min, double max) {
        this.minValue = min;
        this.maxValue = max;
        return this;
    }

    public TermGenerator use(TermSegment termSegment) {
        ArrayUtils.add(this.termSegmentsToUse, termSegment);
        this.maxDepth = Math.max(this.maxDepth, termSegment.getDepth());
        return this;
    }

    public TermGenerator useSuppliedTermSegments(boolean use) {
        this.useTermSegments = use;
        return this;
    }

    public TermSegment build() {
        TermSegment toReturn = null;
        TermSegment[] topLevel = new TermSegment[this.termCount];
        double difference = Math.abs(this.minValue) + Math.abs(this.maxValue);
        double termValue;
        for (int i = 0; i < this.termCount; i++) {
            if (this.useTermSegments && this.termSegmentsToUse.length > i && this.termSegmentsToUse[i] != null) {
                topLevel[i] = this.termSegmentsToUse[i];
            } else if (this.maxDepth > 1 && this.randomOrTrueIfNoSubsegmentAsLastPresent(topLevel,  i, this.termCount)) {
                topLevel[i] = this.makeCopy().maxDepth(this.maxDepth - 1).useSuppliedTermSegments(false).setTermToGenerateIsToplevel(false).build();
            } else {
                termValue = this.minValue + rand.nextDouble() * difference;
                topLevel[i] = new TermSegment(TermOperator.getTermOperatorByIndex(rand.nextInt(4)), termValue);
            }
        }
        toReturn = supplyLastTermSegmentForSubSegment(topLevel);
        return toReturn;
    }


    private TermSegment supplyLastTermSegmentForSubSegment(TermSegment[] subTerms) {
        if (this.TermToGenerateIsToplevel) {
            return new TermSegment(TermOperator.NONE, subTerms);
        } else {
            TermOperator termOperator = TermOperator.getTermOperatorByIndex(rand.nextInt(4));
            TermSegment termseg = new TermSegment(termOperator, subTerms);
            termseg.setIsTopLevel(false);
            return termseg;
        }
    }

    private boolean randomOrTrueIfNoSubsegmentAsLastPresent(TermSegment[] termArr, int i, int length) {
        boolean subSegmentNotPresent = true;
        for (TermSegment term :
                termArr) {
            if (term == null) continue;
            if (term.getSubSegment() != null) {
                subSegmentNotPresent = false;
                break;
            }
        }
        if (subSegmentNotPresent && i == length - 1) {
            return true;
        } else {
            return rand.nextBoolean();
        }
    }

    @Override
    public Object clone() {
        TermGenerator toReturn = TermGenerator.newTerm()
                .setTermToGenerateIsToplevel(this.TermToGenerateIsToplevel)
                .maxDepth(this.maxDepth)
                .termCount(this.termCount)
                .valueRange(this.minValue, this.maxValue)
                .useSuppliedTermSegments(this.useTermSegments);
        for (TermSegment term :
                this.termSegmentsToUse) {
            toReturn.use(term);
        }
        return toReturn;
    }

}
