package com.sct.smartcalc.util;

import com.sct.smartcalc.graphs.expression.Function;

import java.util.ArrayList;

public class FormulaList {
    private ArrayList<SymbolList> formulas;
    private ArrayList<Boolean> selected;
    private int size;

    public FormulaList () {
        formulas = new ArrayList<SymbolList>();
        selected = new ArrayList<Boolean>();
        size = 0;
    }

    public void setFormula(SymbolList symbolList) {
        formulas.add(symbolList);
        selected.add(false);
        ++size;
    }

    public SymbolList getFormula(int i) {
        return formulas.get(i);
    }

    public void setSelected(int i, boolean bool) {
        selected.set(i, bool);
    }

    public boolean isSelected(int i) {
        return selected.get(i);
    }

    public int getSize() {
        return size;
    }

    public void clear() {
        formulas.clear();
        selected.clear();
        size = 0;
    }
}
