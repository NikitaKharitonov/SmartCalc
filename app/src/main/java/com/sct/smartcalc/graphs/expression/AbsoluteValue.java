package com.sct.smartcalc.graphs.expression;

public class AbsoluteValue extends Unary {
	
	public AbsoluteValue(Quantity q) {
		super(q);
	}

	@Override
	public double getValue() {
		double val = realValue(q);
		return Math.abs(val);
	}
}
