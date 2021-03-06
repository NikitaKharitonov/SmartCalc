package com.sct.smartcalc.graphs.expression;

public class Ceiling extends Unary {
	
	public Ceiling(Quantity q) {
		super(q);
	}

	@Override
	public double getValue() {
		double val = realValue(q);
		return Math.ceil(val);
	}
}
