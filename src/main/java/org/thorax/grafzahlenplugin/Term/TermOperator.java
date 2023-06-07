package org.thorax.grafzahlenplugin.Term;


public enum TermOperator {
    ADD,
    SUB,
    DIV,
    MULT,
    NONE;

    public static TermOperator getTermOperatorByIndex(int i) {
        switch (i) {
            case 0:
                return ADD;
            case 1:
                return SUB;
            case 2:
                return DIV;
            case 3:
                return MULT;
            default:
                return NONE;
        }
    }

    public static int getIndexOfOperator(TermOperator termOp) {
        switch (termOp) {
            case ADD:
                return 0;
            case SUB:
                return 1;
            case DIV:
                return 2;
            case MULT:
                return 3;
            default:
                return -1;
        }
    }
}
