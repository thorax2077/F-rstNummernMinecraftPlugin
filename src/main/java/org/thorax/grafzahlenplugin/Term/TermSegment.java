package org.thorax.grafzahlenplugin.Term;


import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class TermSegment {

    private TermSegment[] subSegments;

    private double value;

    private final TermOperator operator;

    private boolean isTopLevel = true;

    public TermSegment(@NotNull TermOperator termOp, double val) {
        this.operator = termOp;
        this.value = val;

    }
    public TermSegment(@NotNull TermOperator termOp, @NotNull TermSegment[] subSeg) {
        if (subSeg == null) {
            throw new IllegalArgumentException("termOp sollte nicht null sein");
        }
        this.operator = termOp;
        this.setSubSegments(subSeg);
        this.value = this.calcSubSeg();
    }

    private TermSegment (TermOperator termOp, TermSegment[] subSeg, double val) {
        if (subSeg != null) {
            this.setSubSegments(subSeg);
        }
        this.operator = termOp;
        this.value = val;
    }

    public TermSegment (TermOperator termOp, TermSegment[] subSeg, double val, boolean isTopLevel) {
        if (subSeg != null) {
            this.setSubSegments(subSeg);
        }
        this.operator = termOp;
        this.value = val;
        this.isTopLevel = isTopLevel;
    }

    public TermOperator getOperator() {
        return operator;
    }

    private void setSubSegments(TermSegment[] subSegments) {
        if (subSegments[subSegments.length - 1].operator != TermOperator.NONE) {
            double value = subSegments[subSegments.length - 1].getValue();
            TermSegment[] termSegments = subSegments[subSegments.length - 1].getSubSegments();
            subSegments[subSegments.length - 1] = new TermSegment(TermOperator.NONE, termSegments, value);
        }
        for (TermSegment term :
                subSegments) {
            term.setIsTopLevel(false);
        }
        this.subSegments = subSegments;
    }

    public TermSegment[] getSubSegments() {
        return subSegments;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setIsTopLevel(boolean value) {
        this.isTopLevel = value;
    }

    public boolean getIsTopLevel() { return this.isTopLevel; }

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
        if (this.subSegments.length == 0) {
            return this.value;
        }
        TermSegment[] subSegCopy = Arrays.copyOf(this.subSegments, this.subSegments.length);
        for (TermSegment subSeg :
                subSegCopy) {
            if (subSeg == null) {
                return this.value;
            }
            if (subSeg.subSegments != null) {
                subSeg.value = subSeg.calcSubSeg();
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
                    subSegCopy = ArrayUtils.addAll(Arrays.copyOfRange(subSegCopy, 0, i+1), Arrays.copyOfRange(subSegCopy, i+2, subSegCopy.length));
                } else {
                    subSegCopy = Arrays.copyOfRange(subSegCopy, 0, i+1);
                }

            }
        }
        return subSegCopy[0].getValue();
    }

    public int getDepth() {
        return getDepth(this);
    }

    public static int getDepth(TermSegment termSegment) {
        int toReturn = 0;
        int depth = 0;
        if (termSegment != null && termSegment.getSubSegments() != null) {
            toReturn = 1;
            for (int i = 0; i < termSegment.getSubSegments().length -1; i++) {
                depth = Math.max(termSegment.getSubSegments()[i].getDepth(), termSegment.getSubSegments()[i + 1].getDepth());
            }
            toReturn += depth;
        }
        return toReturn;
    }

    public boolean valueEquals(double value, int precision) {
        BigDecimal nativeValue = BigDecimal.valueOf(this.value).setScale(precision, RoundingMode.HALF_UP);
        BigDecimal incomingValue = BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_UP);
        return nativeValue.equals(incomingValue);
    }

    public String toStringValuesRounded(int precision) {
        String toReturn = "";
        if(this.subSegments != null){
            toReturn += this.isTopLevel ? "" : " ( ";
            for (TermSegment seg :
                    this.subSegments) {
                toReturn += seg.toStringValuesRounded(precision);
            }
            toReturn += this.isTopLevel ? "" : " ) ";
        } else {
            if (precision < 0) {
                toReturn += Double.toString(value);
            } else if (precision == 0) {
                toReturn += Integer.toString((int) value);
            } else {
                toReturn += BigDecimal.valueOf(value).setScale(precision, RoundingMode.HALF_UP);
            }
        }

        switch (operator) {
            case ADD:
                toReturn += " + ";
                break;
            case SUB:
                toReturn += " - ";
                break;
            case MULT:
                toReturn += " * ";
                break;
            case DIV:
                toReturn += " / ";
                break;
            case NONE:
                break;
        }
        return toReturn;
    }

    public String toString() {
        return this.toStringValuesRounded(-1);
    }
}
