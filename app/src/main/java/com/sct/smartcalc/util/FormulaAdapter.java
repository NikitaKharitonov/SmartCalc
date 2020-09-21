package com.sct.smartcalc.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartcalc.R;

import java.util.ArrayList;

import ru.noties.jlatexmath.JLatexMathView;

public class FormulaAdapter extends RecyclerView.Adapter<FormulaAdapter.FormulaViewHolder> {

    private FormulaList formulaList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public static class FormulaViewHolder extends RecyclerView.ViewHolder{
        public JLatexMathView formula;

        public FormulaViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            formula = view.findViewById(R.id.latexViewHistory);
            formula.textSize(50);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }

    public FormulaAdapter(FormulaList list) {
        this.formulaList = list;
    }

    @Override
    public FormulaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.formula_item, parent, false);
        FormulaViewHolder evh = new FormulaViewHolder(v, onItemClickListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(FormulaViewHolder holder, int position) {
        if(formulaList != null) {
            SymbolList currentItem = formulaList.getFormula(position);
            String latexFormula = currentItem.toLaTeX();
            if (formulaList.isSelected(position))
                holder.formula.setLatex("\\textcolor{#00B040}{" + latexFormula + "}");
            else
                holder.formula.setLatex(latexFormula);
        }

    }

    @Override
    public int getItemCount() {
        return formulaList.getSize();
    }
}
