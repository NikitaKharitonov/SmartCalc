package com.sct.smartcalc.graphs.expression;

public class Ln extends Unary {
	public Ln(Quantity q) {
		super(q);
	}

	@Override
	public double getValue() {
		double val1 = realValue(q);
		return Math.log(val1);
	}
}
