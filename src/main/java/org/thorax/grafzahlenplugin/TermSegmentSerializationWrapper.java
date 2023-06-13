package org.thorax.grafzahlenplugin;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;
import org.thorax.grafzahlenplugin.Term.TermOperator;
import org.thorax.grafzahlenplugin.Term.TermSegment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs("TermSegmentSerializer")
public class TermSegmentSerializationWrapper implements Serializable, ConfigurationSerializable {

    private List<TermSegmentSerializationWrapper> subSegments = new ArrayList<>();

    private int operator;

    private TermSegment termSegment;

    private double value;

    private boolean isTopLevel;

    TermSegmentSerializationWrapper(TermSegment termSegment) {
        this.termSegment = termSegment;
        this.assignSubSegments(termSegment);
        this.operator = TermOperator.getIndexOfOperator(termSegment.getOperator());
        this.isTopLevel = termSegment.getIsTopLevel();
        this.value = termSegment.getValue();
    }

    TermSegmentSerializationWrapper(List<TermSegmentSerializationWrapper> subSegments, int operator, double value, boolean isTopLevel) {
        this.subSegments = subSegments;
        this.operator = operator;
        this.value = value;
        this.isTopLevel = isTopLevel;
    }

    public List<TermSegmentSerializationWrapper> getSubSegments() {
        return subSegments;
    }

    public void setSubSegments(List<TermSegmentSerializationWrapper> subSegments) {
        this.subSegments = subSegments;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public TermSegment getTermSegment() {
        return termSegment;
    }

    public void setTermSegment(TermSegment termSegment) {
        this.termSegment = termSegment;
    }

    public boolean isTopLevel() {
        return isTopLevel;
    }

    public void setTopLevel(boolean topLevel) {
        isTopLevel = topLevel;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("subSegments", this.subSegments);
        toReturn.put("operation", operator);
        toReturn.put("value", value);
        toReturn.put("isTopLevel", isTopLevel);
        return toReturn;
    }

    public TermSegment toTermSegment() {
        TermOperator termOp = TermOperator.getTermOperatorByIndex(this.operator);
        TermSegment[] subSegments;
        if (this.subSegments != null) {
            subSegments = new TermSegment[this.subSegments.size()];
            for (int i = 0; i < this.subSegments.size(); i++) {
                subSegments[i] = this.subSegments.get(i).toTermSegment();
            }
        } else {
            subSegments = null;
        }
        double value = this.value;
        boolean isTopLevel = this.isTopLevel;
        return new TermSegment(termOp, subSegments, value, isTopLevel);
    }

    public static TermSegmentSerializationWrapper deserialize(Map<String, Object> args) {
        int termOperator = (int) args.get("operation");
        ArrayList<TermSegmentSerializationWrapper> subTerms =
                (ArrayList<TermSegmentSerializationWrapper>) args.get("subSegments");
        boolean isTopLevel = (boolean) args.get("isTopLevel");
        double value = (double) args.get("value");
        return new TermSegmentSerializationWrapper(subTerms, termOperator, value, isTopLevel);
    }



    private void assignSubSegments(TermSegment termSegment) {
        if (termSegment.getSubSegments() == null) {
            return;
        }
        for (TermSegment ts :
                termSegment.getSubSegments()) {
            this.subSegments.add(new TermSegmentSerializationWrapper(ts));
        }
    }
}
