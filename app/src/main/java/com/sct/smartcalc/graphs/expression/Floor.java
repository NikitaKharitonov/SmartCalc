package com.sct.smartcalc.graphs.expression;

public class Floor extends Unary {
	
	public Floor(Quantity q) {
		super(q);
	}

	@Override
	public double getValue() {
		double val = realValue(q);
		return Math.floor(val);
	}
}
