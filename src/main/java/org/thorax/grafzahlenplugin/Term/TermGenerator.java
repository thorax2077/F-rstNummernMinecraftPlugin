package org.thorax.grafzahlenplugin.Term;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Random;

public class TermGenerator implements Cloneable{

    private int maxDepth = 1;

    private int termCount = 2;

    private double minValue = 5d;

    private double maxValue = 5d;

    private TermSegment[] termSegmentsToUse = new TermSegment[0];


    public static TermGenerator newTerm() {
        return new TermGenerator();
    }

    public TermGenerator makeCopy() {
        try {
            return (TermGenerator) this.clone();
        } catch (CloneNotSupportedException notSupportedException) {
            notSupportedException.printStackTrace();
        }
    }

    public TermGenerator setMaxDepth(int depth) {
        this.maxDepth = depth;
        return this;
    }

    public TermGenerator setTermCount(int count) {
        this.termCount = count;
        return this;
    }

    public TermGenerator setValueRange(double min, double max) {
        this.minValue = min;
        this.maxValue = max;
        return this;
    }

    public TermGenerator use(TermSegment termSegment) {
        ArrayUtils.add(this.termSegmentsToUse, termSegment);
        this.maxDepth = Math.max(this.maxDepth, termSegment.getDepth());
        return this;
    }

    public TermSegment build() {
        TermSegment toReturn = null;
        Random rand = new Random();
        TermSegment[] topLevel = new TermSegment[this.termCount];
        double difference = Math.abs(this.minValue) + Math.abs(this.maxValue);
        double termValue;
        for (int i = 1; i < this.termCount; i++) {
            if (rand.nextBoolean() && this.maxDepth > 1) {
                topLevel[i] = this.makeCopy().setMaxDepth(this.maxDepth - 1).build();
            } else {
                termValue = this.minValue + rand.nextDouble() % difference;
                topLevel[i] = new TermSegment(TermOperator.getTermOperatorByIndex(rand.nextInt(4)), termValue);
            }
        }
        toReturn = new TermSegment(TermOperator.NONE, topLevel);
        return toReturn;
    }

}
