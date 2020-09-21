package com.sct.smartcalc.graphs.expression;

import com.google.gson.annotations.SerializedName;

public class RealNumber extends Quantity {

	protected double num;
	
	public RealNumber(double num) {
		this.num = num;
	}

	public RealNumber() {this.num = 0;}

	@Override
	public double getValue() {
		return num;
	}
}
