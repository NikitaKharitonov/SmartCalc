package com.sct.smartcalc.graphs.expression;

import com.google.gson.annotations.SerializedName;

public class Function {
	@SerializedName("root")
	private Quantity root;
	@SerializedName("x")
	private Var x;

	public Function(Quantity root, Var x) {
		this.root = root;
		this.x = x;
	}

	public double evaluateAt(double x) {
		this.x.set(x);
		return root.getValue();
	}
}
