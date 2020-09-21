package com.sct.smartcalc;

import android.support.annotation.NonNull;

import com.sct.smartcalc.util.ComplexNumber;
import com.sct.smartcalc.util.SymbolList;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
import static com.sct.smartcalc.util.SymbolList.SymbolType.VARIABLE;

public class Complex {
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
                && ((String) formula.getVal()).equals("0") && (symbolType == NUMBER
                || symbolType == LBR || symbolType == FUNCTION || symbolType == VARIABLE)) {

            formula.popSymbol(true);
            formula.pushSymbol(symbolType, value);
            if(symbolType == FUNCTION)
                formula.pushSymbol(LBR, "(");
            return;
        }
        else switch (formula.getType()) {
            case NUMBER:
                switch (symbolType) {
                    case NUMBER:
                        if(value.equals("e") || value.equals("π") || formula.getVal().equals("e") || formula.getVal().equals("π")) {
                            formula.pushSymbol(OPERATION, MUL);
                            formula.pushSymbol(symbolType, value);
                        } else
                            formula.addNumeral((String) value);

                        break;
                    case COMMA:
                        if(!formula.getVal().equals("e") && !formula.getVal().equals("π"))
                            formula.addNumeral(".");
                        break;
                    case OPERATION:
                    case RBR:
                        if (formula.lastCharIsDot())
                            formula.pushSymbol(NUMBER, "0");
                        formula.pushSymbol(symbolType, value);
                        break;
                    case LBR:
                    case FUNCTION:
                        if (formula.lastCharIsDot())
                            formula.pushSymbol(NUMBER, "0");
                        formula.pushSymbol(OPERATION, MUL);
                        formula.pushSymbol(symbolType, value);
                        formula.pushSymbol(LBR, "(");
                        break;
                    case VARIABLE:
                        if (formula.lastCharIsDot())
                            formula.pushSymbol(NUMBER, "0");
                        formula.pushSymbol(OPERATION, MUL);
                        formula.pushSymbol(symbolType, value);
                        break;
                }
                break;
            case OPERATION:
            case LBR:
                switch (symbolType) {
                    case NUMBER:
                    case LBR:
                    case VARIABLE:
                        formula.pushSymbol(symbolType, value);
                        break;
                    case FUNCTION:
                        formula.pushSymbol(symbolType, value);
                        formula.pushSymbol(LBR, "(");
                }
                break;
            case RBR:
                switch (symbolType) {
                    case NUMBER:
                    case LBR:
                    case VARIABLE:
                        formula.pushSymbol(OPERATION, MUL);
                        formula.pushSymbol(symbolType, value);
                        break;
                    case OPERATION:
                    case RBR:
                        formula.pushSymbol(symbolType, value);
                        break;
                    case FUNCTION:
                        formula.pushSymbol(OPERATION, MUL);
                        formula.pushSymbol(symbolType, value);
                        formula.pushSymbol(LBR, "(");

                }
                break;
            case FUNCTION:
                switch (symbolType) {
                    case VARIABLE:
                    case NUMBER:
                        formula.pushSymbol(LBR, "(");
                        formula.pushSymbol(symbolType, value);
                        break;
                    case LBR:
                        formula.pushSymbol(symbolType, value);
                        break;
                    case FUNCTION:
                        formula.pushSymbol(OPERATION, MUL);
                        formula.pushSymbol(symbolType, value);
                        formula.pushSymbol(LBR, "(");
                }
                break;
            case VARIABLE: //nh
                switch (symbolType) {
                    case NUMBER:
                        formula.pushSymbol(OPERATION, MUL);
                        formula.pushSymbol(symbolType, value);
                        break;
                    case OPERATION:
                    case RBR:
                        if (formula.lastCharIsDot())
                            formula.pushSymbol(NUMBER, "0");
                        formula.pushSymbol(symbolType, value);
                        break;
                    case LBR:
                    case FUNCTION:
                        formula.pushSymbol(LBR, "(");
                        formula.pushSymbol(symbolType, value);
                        formula.pushSymbol(OPERATION, MUL);
                        break;
                    case VARIABLE:
                        formula.pushSymbol(OPERATION, MUL);
                        formula.pushSymbol(symbolType, value);
                        break;
                }
                break;
        }
    }

    /*
    Удаляет последний введённый символ.
     */
    public static void popAction() {

        SymbolList.SymbolType lastType = formula.getType();
        if (lastType == NUMBER && !formula.getVal().equals("e") && !formula.getVal().equals("π") && !formula.getVal().equals("i"))
            formula.popNumeral();
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
    public static String calculate() {

        String resultString = "";
        SymbolList symbolList = new SymbolList(formula);
        // Выделение последнего элемента.
        while (symbolList.currentPos() + 1 != formula.size())
            symbolList.moveCurrent(true);

        // Подсчёт скобок и добавление закрывающих при нужде.
        int leftBrCount = symbolList.countType(LBR);
        int rightBrCount = symbolList.countType(RBR);
        while (rightBrCount < leftBrCount) {
            pushAction(RBR, ")");
            symbolList.pushSymbol(RBR, ")");
            rightBrCount++;
        }
        symbolList.pushSymbol(LBR, null, 0);
        symbolList.pushSymbol(RBR, null);

        // Создание рабочей группы (работа с выражениями в самых глубоких скобках).
        while (symbolList.size() > 1) {
			/*
            Нахождение самой глубокой пары скобок.
            При эквивалентных уровнях глубины выбирается самая правая пара.
             */
            int lB = 0, rB = 0;
            int depth = 0, maxDepth = 0;

            for (int i = 0; i < symbolList.size(); i++) {
                if (symbolList.getType(i) == LBR) {
                    depth++;
                    if (depth >= maxDepth) {
                        maxDepth = depth;
                        lB = i;
                    }
                }
                if (symbolList.getType(i) == RBR) {
                    if (depth == maxDepth)
                        rB = i;
                    depth--;
                }
            }

            // Вырезка выражения в скобках из формулы.
            SymbolList currentSymbols = symbolList.cutSublist(lB + 1, rB - 1);
            resultString = calculate_numeric(currentSymbols);
            symbolList.replaceSublist(lB, lB + 1, currentSymbols);
        }
        return resultString;
    }

    private static String calculate_numeric(@NonNull SymbolList formula) {
        ComplexNumber result;
        int numId = 0;
        for (int i = 0; i < formula.size(); i++) {
            if (formula.getType(i) == NUMBER) {
                numId = i;
                break;
            }
        }
        String val = (String)formula.getVal(numId);
        if(val.equals("e"))
            result = new ComplexNumber(Math.E, 0);
        else if (val.equals("i"))
            result = new ComplexNumber(0, 1);
        else if (val.equals("π"))
            result = new ComplexNumber(Math.PI, 0);
        else
            result = new ComplexNumber(Double.parseDouble(val), 0.0);

        // Рабочий ход.
        for (int i = 0; i < formula.size(); ) {
            if (formula.getType(i) == FUNCTION) {

                // Создаётся список для хранения результата.
                SymbolList tmp = new SymbolList();
                //Берутся соседние числа.
                ComplexNumber rightNum;
                String rightVal = (String)formula.getVal(i + 1);
                if(rightVal.equals("e"))
                    rightNum = new ComplexNumber(Math.E, 0);
                else if (rightVal.equals("i"))
                    rightNum = new ComplexNumber(0, 1);
                else if (rightVal.equals("π"))
                    rightNum = new ComplexNumber(Math.PI, 0);
                else
                    rightNum = new ComplexNumber(Double.parseDouble(rightVal), 0.0);

                // Обработка операций умножения.
                if (formula.getVal(i) == SymbolList.FunctionType.SIN) {
                    // TODO radians
                    result = ComplexNumber.sin(rightNum);
                }
                if (formula.getVal(i) == SymbolList.FunctionType.COS) {
                    result = ComplexNumber.cos(rightNum);
                }
                if (formula.getVal(i) == SymbolList.FunctionType.TAN) {
                    result = ComplexNumber.tan(rightNum);
                }
                if (formula.getVal(i) == SymbolList.FunctionType.SQRT) {
                    // TODO
                    result = ComplexNumber.pow(rightNum, new ComplexNumber(0.5, 0), 0);
                }
                if (formula.getVal(i) == SymbolList.FunctionType.CONJ) {
                    result = ComplexNumber.conjugate(rightNum);
                }
                if (formula.getVal(i) == SymbolList.FunctionType.ARG) {
                    result = new ComplexNumber(ComplexNumber.arg(rightNum), 0);
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
                ComplexNumber leftNum;
                String leftVal = (String)formula.getVal(i - 1);
                if(leftVal.equals("e"))
                    leftNum = new ComplexNumber(Math.E, 0);
                else if (leftVal.equals("i"))
                    leftNum = new ComplexNumber(0, 1);
                else if (leftVal.equals("π"))
                    leftNum = new ComplexNumber(Math.PI, 0);
                else
                    leftNum = new ComplexNumber(Double.parseDouble(leftVal), 0.0);

                ComplexNumber rightNum;
                String rightVal = (String)formula.getVal(i + 1);
                if(rightVal.equals("e"))
                    rightNum = new ComplexNumber(Math.E, 0);
                else if (rightVal.equals("i"))
                    rightNum = new ComplexNumber(0, 1);
                else if (rightVal.equals("π"))
                    rightNum = new ComplexNumber(Math.PI, 0);
                else
                    rightNum = new ComplexNumber(Double.parseDouble(rightVal), 0.0);

                // Обработка операций умножения.
                if (formula.getVal(i) == POW)
                    result = ComplexNumber.pow(leftNum, rightNum, 0);

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
                ComplexNumber leftNum;
                String leftVal = (String)formula.getVal(i - 1);
                if(leftVal.equals("e"))
                    leftNum = new ComplexNumber(Math.E, 0);
                else if (leftVal.equals("i"))
                    leftNum = new ComplexNumber(0, 1);
                else if (leftVal.equals("π"))
                    leftNum = new ComplexNumber(Math.PI, 0);
                else
                    leftNum = new ComplexNumber(Double.parseDouble(leftVal), 0.0);

                ComplexNumber rightNum;
                String rightVal = (String)formula.getVal(i + 1);
                if(rightVal.equals("e"))
                    rightNum = new ComplexNumber(Math.E, 0);
                else if (rightVal.equals("i"))
                    rightNum = new ComplexNumber(0, 1);
                else if (rightVal.equals("π"))
                    rightNum = new ComplexNumber(Math.PI, 0);
                else
                    rightNum = new ComplexNumber(Double.parseDouble(rightVal), 0.0);

                // Обработка операций умножения.
                if (formula.getVal(i) == MUL)
                    result = ComplexNumber.multiply(leftNum, rightNum);
                // Обработка операций деления.
                if (formula.getVal(i) == DIV)
                    result = ComplexNumber.divide(leftNum, rightNum);

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
                ComplexNumber leftNum;
                String leftVal = (String)formula.getVal(i - 1);
                if(leftVal.equals("e"))
                    leftNum = new ComplexNumber(Math.E, 0);
                else if (leftVal.equals("i"))
                    leftNum = new ComplexNumber(0, 1);
                else if (leftVal.equals("π"))
                    leftNum = new ComplexNumber(Math.PI, 0);
                else
                    leftNum = new ComplexNumber(Double.parseDouble(leftVal), 0.0);

                ComplexNumber rightNum;
                String rightVal = (String)formula.getVal(i + 1);
                if(rightVal.equals("e"))
                    rightNum = new ComplexNumber(Math.E, 0);
                else if (rightVal.equals("i"))
                    rightNum = new ComplexNumber(0, 1);
                else if (rightVal.equals("π"))
                    rightNum = new ComplexNumber(Math.PI, 0);
                else
                    rightNum = new ComplexNumber(Double.parseDouble(rightVal), 0.0);

                // Обработка операций сложения.
                if (formula.getVal(i) == ADD)
                    result = ComplexNumber.multiply(leftNum, rightNum);

                // Обработка операций отрицания.
                if (formula.getVal(i) == SUB)
                    result = ComplexNumber.subtract(leftNum, rightNum);

                // Превращение числа в строку.
                String resultStr = String.valueOf(result);
                // Помещение числа в единичный список и замена операции на её результат.
                tmp.pushSymbol(NUMBER, resultStr);
                formula.replaceSublist(i - 1, i + 1, tmp);
            } else i++;
        }
   //      Округление рузультата и превращение его в строку.
        return round(result, 6).toString();
    }

    private static ComplexNumber round(ComplexNumber value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal reBig = new BigDecimal(value.re());
        BigDecimal imBig = new BigDecimal(value.im());
        reBig = reBig.setScale(places, RoundingMode.HALF_UP);
        imBig = imBig.setScale(places, RoundingMode.HALF_UP);
        double re = reBig.doubleValue();
        double im = imBig.doubleValue();
        return new ComplexNumber(re, im);
    }
}
