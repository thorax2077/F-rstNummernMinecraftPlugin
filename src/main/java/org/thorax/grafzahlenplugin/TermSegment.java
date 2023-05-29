package org.thorax.grafzahlenplugin;


import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.stream.Stream;

public class TermSegment {

    private TermSegment[] subSegment;
    private double value;
    private final TermOperator operator;

    public TermSegment(TermOperator termOp, double val) {
        this.operator = termOp;
        this.value = val;
    }
    public TermSegment(TermOperator termOp, TermSegment[] subSeg) {
        this.operator = termOp;
        this.subSegment = subSeg;
        this.value = this.calcSubSeg();
    }

    public double getValue() {
        return value;
    }

    public TermSegment calcTermSeg(TermSegment termSeg) {
        switch (operator) {
            case ADD:
                return new TermSegment(termSeg.operator, this.value + termSeg.value);
            case SUB:
                return new TermSegment(termSeg.operator, this.value - termSeg.value);
            case MULT:
                return new TermSegment(termSeg.operator, this.value * termSeg.value);
            case DIV:
                return new TermSegment(termSeg.operator, this.value / termSeg.value);
            default:
                throw new RuntimeException("invalid Operator");
        }
    }

    public double calcSubSeg() {
        TermSegment[] subSegCopy = Arrays.copyOf(this.subSegment, this.subSegment.length);
        for (TermSegment subSeg :
                subSegCopy) {
            if (subSeg.subSegment != null) {
                double calculatedValue = subSeg.calcSubSeg();
                subSeg.value = calculatedValue;
            }
        }
        for (int i = 0; i < subSegCopy.length; i++) {
            if(subSegCopy[i].operator == TermOperator.NONE) break;
            while(subSegCopy[i].operator == TermOperator.MULT || subSegCopy[i].operator == TermOperator.DIV){
                TermSegment termSeg = subSegCopy[i].calcTermSeg(subSegCopy[i+1]);
                subSegCopy[i] = termSeg;
                if (subSegCopy.length >= 3) {
                    TermSegment[] termsegArr1 = Arrays.copyOfRange(subSegCopy, 0, i +1);
                    TermSegment[] termsegArr2 = Arrays.copyOfRange(subSegCopy, i +2 , subSegCopy.length);
                    subSegCopy = ArrayUtils.addAll(termsegArr1, termsegArr2);
                } else {
                    subSegCopy = Arrays.copyOfRange(subSegCopy, 0, i+1);
                }

            }
        }
        for (int i = 0; i < subSegCopy.length; i++) {
            if(subSegCopy[i].operator == TermOperator.NONE) break;
            while(subSegCopy[i].operator == TermOperator.SUB || subSegCopy[i].operator == TermOperator.ADD){
                TermSegment termSeg = subSegCopy[i].calcTermSeg(subSegCopy[i+1]);
                subSegCopy[i] = termSeg;
                if (subSegCopy.length >= 3) {
                    subSegCopy = (TermSegment[]) ArrayUtils.add(Arrays.copyOfRange(subSegCopy, 0, i+1), Arrays.copyOfRange(subSegCopy, i+2, subSegCopy.length));
                } else {
                    subSegCopy = Arrays.copyOfRange(subSegCopy, 0, i+1);
                }

            }
        }
        return subSegCopy[0].getValue();
    }

    public String toString() {
        String toReturn = "";
        if(this.subSegment != null){
            toReturn += "(";
            for (TermSegment seg :
                    this.subSegment) {
                toReturn += seg.toString();
            }
        } else {
            toReturn = Double.toString(value);
        }

        switch (operator) {
            case ADD:
                toReturn += "+";
                break;
            case SUB:
                toReturn += "-";
                break;
            case MULT:
                toReturn += "*";
                break;
            case DIV:
                toReturn += "/";
                break;
            case NONE:
                break;
        }
        return toReturn;
    }
}
