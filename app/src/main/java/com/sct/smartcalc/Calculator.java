package com.sct.smartcalc;

import android.support.annotation.NonNull;

import com.sct.smartcalc.util.SymbolList;
import com.sct.smartcalc.util.SymbolList.FunctionType;

import static com.sct.smartcalc.util.SymbolList.OperationType.ADD;
import static com.sct.smartcalc.util.SymbolList.OperationType.DIV;
import static com.sct.smartcalc.util.SymbolList.OperationType.MUL;
import static com.sct.smartcalc.util.SymbolList.OperationType.POW;
import static com.sct.smartcalc.util.SymbolList.OperationType.SUB;
import static com.sct.smartcalc.util.SymbolList.SymbolType.FUNCTION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.LBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.NUMBER;
import static com.sct.smartcalc.util.SymbolList.SymbolType.OPERATION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.RBR;
import static java.lang.StrictMath.cos;
import static java.lang.StrictMath.sin;
import static java.lang.StrictMath.sqrt;
import static java.lang.StrictMath.tan;
import static java.lang.StrictMath.toRadians;

public final class Calculator {
	private static SymbolList formula = new SymbolList();
	private static boolean inDegrees = false;

	/*
	Приводит калькулятор в рабочее состояние перед использованием.
	 */
	public static void setup() {
		clear();
	}

	/*
	Добавляет символ в формулу.
	 */
	public static void pushAction(SymbolList.SymbolType symbolType, Object value) {
		if (formula.size() == 1 && formula.getType() == NUMBER
				&& ((String)formula.getVal()).equals("0") && (symbolType == NUMBER
				|| symbolType == LBR || symbolType == FUNCTION /*|| symbolType == VARIABLE*/)) {
			formula.popSymbol(true);
			formula.pushSymbol(symbolType, value);
			return;
		}
		switch (formula.getType()) {
			case NUMBER:
				switch (symbolType) {
					case NUMBER:
						formula.addNumeral((String)value);
						break;
					case COMMA:
						formula.addNumeral(".");
						break;
					case OPERATION:
					case RBR:
						if (formula.lastCharIsDot()) formula.pushSymbol(NUMBER, "0");
						formula.pushSymbol(symbolType, value);
						break;
					case LBR:
					case FUNCTION:
						if (formula.lastCharIsDot()) formula.pushSymbol(NUMBER, "0");
						formula.pushSymbol(OPERATION, MUL);
						formula.pushSymbol(symbolType, value);
				}
				break;
			case OPERATION:
			case LBR:
				switch (symbolType) {
					case NUMBER:
					case LBR:
					case FUNCTION:
						formula.pushSymbol(symbolType, value);
						break;
				}
				break;
			case RBR:
			case DERIVATIVE:
				switch (symbolType) {
					case NUMBER:
					case LBR:
					case FUNCTION:
						formula.pushSymbol(OPERATION, MUL);
						formula.pushSymbol(symbolType, value);
						break;
					case OPERATION:
					case RBR:
						formula.pushSymbol(symbolType, value);
				}
				break;
			case FUNCTION:
				switch (symbolType) {
					case NUMBER:
					case LBR:
						formula.pushSymbol(symbolType, value);
						break;
					case FUNCTION:
						formula.pushSymbol(OPERATION, MUL);
						formula.pushSymbol(symbolType, value);
				}
		}
	}

	/*
	Удаляет последний введённый символ.
	 */
	public static void popAction() {
		SymbolList.SymbolType lastType = formula.getType();

		if (lastType == NUMBER)	formula.popNumeral();
		else formula.popSymbol();
	}

	public static void switchNumberSign() {
		formula.switchNumberSign();
	}

	public static void moveCursor(boolean forward) {
		formula.moveCurrent(forward);
	}

	/*
	Смена вычислений тригонометрических функций с радиан на градусы и обратно.
	 */
	public static void switchDegreesOrRadian() {
		inDegrees = !inDegrees;
	}

	/*
	Очищает введённую формулу.
	 */
	public static void clear() {
		formula.clear();
		formula.pushSymbol(NUMBER, "0");
	}

	/*
	Выводит сохранённую формулу в виде строки.
	 */
	public static String getFormula() {
		return formula.toLaTeX();
	}

	/*
	Превращает сохранённую формулу в другую, получая в результате единственное число.
	 */
	public static void calculate() {
		// Выделение последнего элемента.
		while (formula.currentPos() + 1 != formula.size())
			formula.moveCurrent(true);

		// Подсчёт скобок и добавление закрывающих при нужде.
		int leftBrCount = formula.countType(LBR);
		int rightBrCount = formula.countType(RBR);
		while (rightBrCount < leftBrCount) {
			pushAction(RBR, ")");
			rightBrCount++;
		}
		formula.pushSymbol(LBR, null, 0);
		formula.pushSymbol(RBR, null);

		// Создание рабочей группы (работа с выражениями в самых глубоких скобках).
		while (formula.size() > 1) {
			/*
            Нахождение самой глубокой пары скобок.
            При эквивалентных уровнях глубины выбирается самая правая пара.
             */
			int lB = 0, rB = 0;
			int depth = 0, maxDepth = 0;

			for (int i = 0; i < formula.size(); i++) {
				if (formula.getType(i) == LBR) {
					depth++;
					if (depth >= maxDepth) {
						maxDepth = depth;
						lB = i;
					}
				}
				if (formula.getType(i) == RBR) {
					if (depth == maxDepth)
						rB = i;
					depth--;
				}
			}

			// Вырезка выражения в скобках из формулы.
			SymbolList currentSymbols = formula.cutSublist(lB + 1, rB - 1);
			String resultStr = calculate_numeric(currentSymbols);
			double result = Double.parseDouble(resultStr);

			// Преобразование вывода из double в int, если приемлемо.
			boolean parseExponent = false;
			String exponent = "";
			if (result == (int) result) {
				resultStr = String.valueOf((int) result);
			} else {
				// Проверка на возможность избавления от научной нотации.
				if (formula.size() <= 2 && resultStr.contains("E")) {
					int pivot = resultStr.lastIndexOf('E');
					exponent = resultStr.substring(pivot + 1);
					result = Double.parseDouble(resultStr.substring(0, pivot - 1));
					parseExponent = true;
				}
				// Округление до 6 знаков после запятой.
				resultStr = String.format("%.6f", result).replaceAll("(\\.\\d+?)0*$", "$1");
			}
			currentSymbols.clear();
			currentSymbols.pushSymbol(NUMBER, resultStr);
			if (parseExponent) {
				currentSymbols.pushSymbol(OPERATION, MUL);
				currentSymbols.pushSymbol(NUMBER, "10");
				currentSymbols.pushSymbol(OPERATION, POW);
				currentSymbols.pushSymbol(NUMBER, exponent);
				formula.replaceSublist(lB, lB + 1, currentSymbols);
				break;
			}
			else formula.replaceSublist(lB, lB + 1, currentSymbols);
		}
	}

	private static String calculate_numeric(@NonNull SymbolList formula) {
		double result;
		int numId = 0;
		for (int i = 0; i < formula.size(); i++) {
			if (formula.getType(i) == NUMBER) {
				numId = i;
				break;
			}
		}
		result = Double.parseDouble((String)formula.getVal(numId));

		// Рабочий ход.
		for (int i = 0; i < formula.size(); ) {
			if (formula.getType(i) == FUNCTION) {

				// Создаётся список для хранения результата.
				SymbolList tmp = new SymbolList();
				//Берутся соседние числа.
				double rightNum = Double.parseDouble((String) formula.getVal(i + 1));

				// Обработка операций умножения.
				if (formula.getVal(i) == FunctionType.SIN) {
					if (inDegrees) result = sin(toRadians(rightNum));
					else result = sin(rightNum);
				}
				if (formula.getVal(i) == FunctionType.COS) {
					if (inDegrees) result = cos(toRadians(rightNum));
					else result = cos(rightNum);
				}
				if (formula.getVal(i) == FunctionType.TAN) {
					if (inDegrees) result = tan(toRadians(rightNum));
					else result = tan(rightNum);
				}
				if (formula.getVal(i) == FunctionType.SQRT) {
					result = sqrt(rightNum);
				}

				// Превращение числа в строку.
				String resultStr = String.valueOf(result);
				// Помещение числа в единичный список и замена операции на её результат.
				tmp.pushSymbol(NUMBER, resultStr);
				formula.replaceSublist(i, i + 1, tmp);
			} else i++;
		}
		for (int i = 0; i < formula.size(); ) {
			if (formula.getType(i) == OPERATION	&& formula.getVal(i) == POW) {

				// Создаётся список для хранения результата.
				SymbolList tmp = new SymbolList();
				//Берутся соседние числа.
				double leftNum = Double.parseDouble((String) formula.getVal(i - 1));
				double rightNum = Double.parseDouble((String) formula.getVal(i + 1));

				// Обработка операций умножения.
				if (formula.getVal(i) == POW)
					result = StrictMath.pow(leftNum, rightNum);

				// Превращение числа в строку.
				String resultStr = String.valueOf(result);
				// Помещение числа в единичный список и замена операции на её результат.
				tmp.pushSymbol(NUMBER, resultStr);
				formula.replaceSublist(i - 1, i + 1, tmp);
			} else i++;
		}
		for (int i = 0; i < formula.size(); ) {
			if (formula.getType(i) == OPERATION	&&
					(formula.getVal(i) == MUL || formula.getVal(i) == DIV)) {

				// Создаётся список для хранения результата.
				SymbolList tmp = new SymbolList();
				// Берутся соседние числа.
				double leftNum = Double.parseDouble((String)formula.getVal(i - 1));
				double rightNum = Double.parseDouble((String)formula.getVal(i + 1));

				// Обработка операций умножения.
				if (formula.getVal(i) == MUL)
					result = leftNum * rightNum;
				// Обработка операций деления.
				if (formula.getVal(i) == DIV)
					result = leftNum / rightNum;

				// Превращение числа в строку.
				String resultStr = String.valueOf(result);
				// Помещение числа в единичный список и замена операции на её результат.
				tmp.pushSymbol(NUMBER, resultStr);
				formula.replaceSublist(i - 1, i + 1, tmp);
			} else i++;
		}
		for (int i = 0; i < formula.size(); ) {
			if (formula.getType(i) == OPERATION
					&& (formula.getVal(i) == ADD || formula.getVal(i) == SUB)) {

				// Создаётся список для хранения результата.
				SymbolList tmp = new SymbolList();
				//Берутся соседние числа.
				double leftNum = Double.parseDouble((String)formula.getVal(i - 1));
				double rightNum = Double.parseDouble((String)formula.getVal(i + 1));

				// Обработка операций сложения.
				if (formula.getVal(i) == ADD)
					result = leftNum + rightNum;
				// Обработка операций отрицания.
				if (formula.getVal(i) == SUB)
					result = leftNum - rightNum;

				// Превращение числа в строку.
				String resultStr = String.valueOf(result);
				// Помещение числа в единичный список и замена операции на её результат.
				tmp.pushSymbol(NUMBER, resultStr);
				formula.replaceSublist(i - 1, i + 1, tmp);
			} else i++;
		}
		String resultStr = Double.toString(result);
		if (result == (int) result)
			resultStr = String.valueOf((int) result);
		else
			resultStr = String.format("%.6f", result).replaceAll("(\\.\\d+?)0*$", "$1");
		return resultStr;
	}
}