package com.sct.smartcalc.graphs.expression;

import com.google.gson.annotations.SerializedName;

public abstract class Unary extends Quantity {

	@SerializedName("q")
	protected Quantity q;
	
	public Unary(Quantity q) {
		this.q = q;
	}

	public Unary() {this.q = new RealNumber();}
	
	@Override
	public abstract double getValue();
}
