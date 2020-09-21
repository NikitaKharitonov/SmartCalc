package com.sct.smartcalc.graphs.expression;

public abstract class Quantity {
	public abstract double getValue();

	public Quantity() {

	}
	
	public static double realValue(Quantity q) {
		return q != null ? q.getValue() : Double.NaN;
	}
}
