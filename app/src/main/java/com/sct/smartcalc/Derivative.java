package com.sct.smartcalc;

import com.sct.smartcalc.graphs.expression.Function;
import com.sct.smartcalc.util.SymbolList;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.sct.smartcalc.util.SymbolList.OperationType.MUL;
import static com.sct.smartcalc.util.SymbolList.SymbolType.FUNCTION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.LBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.NUMBER;
import static com.sct.smartcalc.util.SymbolList.SymbolType.OPERATION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.VARIABLE;

public final class Derivative extends Graphs {

	public static void setup () {
		formula.setActive(true);
		clear();
		point.clear();
		point.setActive(false);
		point.pushSymbol(NUMBER, "0");

	}

	public static void setPointActive(boolean bool) {
		point.setActive(bool);
	}

	public static void setFormulaActive(boolean bool) {
		formula.setActive(bool);
	}

	//private static double tochka;

	private static SymbolList point = new SymbolList();

	public static String getPoint() {
		return point.toLaTeX();
	}

	/*
	Удаляет последний введённый символ.
	 */
	public static void popActionPoint() {
		SymbolList.SymbolType lastType = point.getType();
		if (lastType == NUMBER && !point.getVal().equals("e") && !point.getVal().equals("π"))
			point.popNumeral();
		else point.popSymbol();
	}

	public static void switchNumberSignPoint() {
		point.switchNumberSign();
	}

	public static void pushToPoint(SymbolList.SymbolType symbolType, Object value) {
		if (point.size() == 1 && point.getType() == NUMBER
				&& ((String) point.getVal()).equals("0") && (symbolType == NUMBER
				|| symbolType == LBR || symbolType == FUNCTION)) {

			point.popSymbol(true);
			point.pushSymbol(symbolType, value);
			if(symbolType == FUNCTION)
				point.pushSymbol(LBR, "(");
			return;
		}
		else switch (point.getType()) {
			case NUMBER:
				switch (symbolType) {
					case NUMBER:
						if(value.equals("e") || value.equals("π") || point.getVal().equals("e") || point.getVal().equals("π")) {
							point.pushSymbol(OPERATION, MUL);
							point.pushSymbol(symbolType, value);
						} else
							point.addNumeral((String) value);

						break;
					case COMMA:
						if(!point.getVal().equals("e") && !point.getVal().equals("π"))
							point.addNumeral(".");
						break;
					case OPERATION:
					case RBR:
						if (point.lastCharIsDot())
							point.pushSymbol(NUMBER, "0");
						point.pushSymbol(symbolType, value);
						break;
					case LBR:
					case FUNCTION:
						if (point.lastCharIsDot())
							point.pushSymbol(NUMBER, "0");
						point.pushSymbol(OPERATION, MUL);
						point.pushSymbol(symbolType, value);
						point.pushSymbol(LBR, "(");
						break;
				}
				break;
			case OPERATION:
			case LBR:
				switch (symbolType) {
					case NUMBER:
					case LBR:
					case FUNCTION:
						point.pushSymbol(symbolType, value);
						point.pushSymbol(LBR, "(");
				}
				break;
			case RBR:
				switch (symbolType) {
					case NUMBER:
					case LBR:
					case OPERATION:
					case RBR:
						point.pushSymbol(symbolType, value);
						break;
					case FUNCTION:
						point.pushSymbol(OPERATION, MUL);
						point.pushSymbol(symbolType, value);
						point.pushSymbol(LBR, "(");

				}
				break;
			case FUNCTION:
				switch (symbolType) {
					case NUMBER:
						point.pushSymbol(LBR, "(");
						point.pushSymbol(symbolType, value);
						break;
					case LBR:
						point.pushSymbol(symbolType, value);
						break;
					case FUNCTION:
						point.pushSymbol(OPERATION, MUL);
						point.pushSymbol(symbolType, value);
						point.pushSymbol(LBR, "(");
				}
				break;
		}
	}

//	//public static void setX(double x) {
//		tochka = x;
//	}

	public static double prodifferentsirovat() {

		double tochka = point.parse().evaluateAt(0);
		Function function = getFunction();
		double dx = 0.0000000001;
		double proizvodnaya = (function.evaluateAt(tochka + dx) - function.evaluateAt(tochka)) / dx;
		return round(proizvodnaya, 4);
	}

	private static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}