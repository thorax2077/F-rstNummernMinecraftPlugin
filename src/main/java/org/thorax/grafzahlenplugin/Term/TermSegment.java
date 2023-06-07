package org.thorax.grafzahlenplugin.Term;


import com.google.common.math.BigDecimalMath;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

public class TermSegment {

    private TermSegment[] subSegment;

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
        this.setSubSegment(subSeg);
        this.value = this.calcSubSeg();
    }

    public TermOperator getOperator() {
        return operator;
    }

    private void setSubSegment(TermSegment[] subSegment) {
        if (subSegment[subSegment.length - 1].operator != TermOperator.NONE) {
            double value = subSegment[subSegment.length - 1].getValue();
            subSegment[subSegment.length - 1] = new TermSegment(TermOperator.NONE, value);
        }
        for (TermSegment term :
                subSegment) {
            term.setIsTopLevel(false);
        }
        this.subSegment = subSegment;
    }

    public TermSegment[] getSubSegment() {
        return subSegment;
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
        if (this.subSegment.length == 0) {
            return this.value;
        }
        TermSegment[] subSegCopy = Arrays.copyOf(this.subSegment, this.subSegment.length);
        for (TermSegment subSeg :
                subSegCopy) {
            if (subSeg == null) {
                return this.value;
            }
            if (subSeg.subSegment != null) {
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
        if (termSegment != null && termSegment.getSubSegment() != null) {
            toReturn = 1;
            for (int i = 0; i < termSegment.getSubSegment().length -1; i++) {
                depth = Math.max(termSegment.getSubSegment()[i].getDepth(), termSegment.getSubSegment()[i + 1].getDepth());
            }
            toReturn += depth;
        }
        return toReturn;
    }

    public boolean valueEquals(double value, int precision) {
        BigDecimal nativeValue = BigDecimal.valueOf(this.value).round(new MathContext(precision, RoundingMode.HALF_UP));
        BigDecimal incomingValue = BigDecimal.valueOf(value).round(new MathContext(precision, RoundingMode.HALF_UP));
        return nativeValue.equals(incomingValue);
    }

    public String toStringValuesRounded(int precision) {
        String toReturn = "";
        if(this.subSegment != null){
            toReturn += this.isTopLevel ? "" : " ( ";
            for (TermSegment seg :
                    this.subSegment) {
                toReturn += seg.toStringValuesRounded(precision);
            }
            toReturn += this.isTopLevel ? "" : " ) ";
        } else {
            if (precision < 0) {
                toReturn += Double.toString(value);
            } else if (precision == 0) {
                toReturn += Integer.toString((int) value);
            } else {
                toReturn += BigDecimal.valueOf(value).round(new MathContext(precision, RoundingMode.HALF_UP));
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
