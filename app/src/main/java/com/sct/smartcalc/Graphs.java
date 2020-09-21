/*
 *                 J                A                V                        V                A
 *                 J               A A                V                      V                A A
 *                 J              A   A                V                    V                A   A
 *                 J             A     A                V                  V                A     A
 *                 J            A       A                V                V                A       A
 *                 J           A         A                V              V                A         A
 *                 J          A           A                V            V                A           A
 *                 J         AAAAAAAAAAAAAAA                V          V                AAAAAAAAAAAAAAA
 *                 J        A               A                V        V                A               A
 *                 J       A                 A                V      V                A                 A
 * J             J        A                   A                V    V                A                   A
 *   J          J        A                     A                V  V                A                     A
 *     JJJJJJJ          A                       A                V                 A                       A
 *
 */

package com.sct.smartcalc;

import com.sct.smartcalc.graphs.expression.*;
import com.sct.smartcalc.util.SymbolList;

import java.util.ArrayList;
import java.util.List;

import static com.sct.smartcalc.util.SymbolList.OperationType.MUL;
import static com.sct.smartcalc.util.SymbolList.SymbolType.FUNCTION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.LBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.NUMBER;
import static com.sct.smartcalc.util.SymbolList.SymbolType.OPERATION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.RBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.VARIABLE;

public class Graphs {

    protected static SymbolList formula = new SymbolList();
    protected static boolean inDegrees = false;
    protected static Var x = new Var();

    public static SymbolList getSymbolListFormula () {
        return new SymbolList(formula);
    }

    public static boolean isCurrentEnd() {
        if(formula.currentPos() == formula.size() - 1)
            return true;
        else return false;
    }

    public static boolean isCurrentBegin() {
        if(formula.currentPos() == 0)
            return true;
        else return false;
    }

    /*
    Приводит в рабочее состояние перед использованием.
     */
    public static void setup() {
        formula.setActive(true);
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

    public static void pushActionTo(SymbolList symbolList, SymbolList.SymbolType symbolType, Object value) {

        if (symbolList.size() == 1 && symbolList.getType() == NUMBER
                && ((String) symbolList.getVal()).equals("0") && (symbolType == NUMBER
                || symbolType == LBR || symbolType == FUNCTION || symbolType == VARIABLE)) {

            symbolList.popSymbol(true);
            symbolList.pushSymbol(symbolType, value);
            if(symbolType == FUNCTION)
                symbolList.pushSymbol(LBR, "(");
            return;
        }
        else switch (symbolList.getType()) {
            case NUMBER:
                switch (symbolType) {
                    case NUMBER:
                        if(value.equals("e") || value.equals("π") || symbolList.getVal().equals("e") || symbolList.getVal().equals("π")) {
                            symbolList.pushSymbol(OPERATION, MUL);
                            symbolList.pushSymbol(symbolType, value);
                        } else
                            symbolList.addNumeral((String) value);

                        break;
                    case COMMA:
                        if(!symbolList.getVal().equals("e") && !symbolList.getVal().equals("π"))
                            symbolList.addNumeral(".");
                        break;
                    case OPERATION:
                    case RBR:
                        if (symbolList.lastCharIsDot())
                            symbolList.pushSymbol(NUMBER, "0");
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case LBR:
                    case FUNCTION:
                        if (symbolList.lastCharIsDot())
                            symbolList.pushSymbol(NUMBER, "0");
                        symbolList.pushSymbol(OPERATION, MUL);
                        symbolList.pushSymbol(symbolType, value);
                        symbolList.pushSymbol(LBR, "(");
                        break;
                    case VARIABLE:
                        if (symbolList.lastCharIsDot())
                            symbolList.pushSymbol(NUMBER, "0");
                        symbolList.pushSymbol(OPERATION, MUL);
                        symbolList.pushSymbol(symbolType, value);
                        break;
                }
                break;
            case OPERATION:
            case LBR:
                switch (symbolType) {
                    case NUMBER:
                    case LBR:
                    case VARIABLE:
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case FUNCTION:
                        symbolList.pushSymbol(symbolType, value);
                        symbolList.pushSymbol(LBR, "(");
                }
                break;
            case RBR:
                switch (symbolType) {
                    case NUMBER:
                    case LBR:
                    case VARIABLE:
                        symbolList.pushSymbol(OPERATION, MUL);
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case OPERATION:
                    case RBR:
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case FUNCTION:
                        symbolList.pushSymbol(OPERATION, MUL);
                        symbolList.pushSymbol(symbolType, value);
                        symbolList.pushSymbol(LBR, "(");

                }
                break;
            case FUNCTION:
                switch (symbolType) {
                    case VARIABLE:
                    case NUMBER:
                        symbolList.pushSymbol(LBR, "(");
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case LBR:
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case FUNCTION:
                        symbolList.pushSymbol(OPERATION, MUL);
                        symbolList.pushSymbol(symbolType, value);
                        symbolList.pushSymbol(LBR, "(");
                }
                break;
            case VARIABLE: //nh
                switch (symbolType) {
                    case NUMBER:
                        symbolList.pushSymbol(OPERATION, MUL);
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case OPERATION:
                    case RBR:
                        if (symbolList.lastCharIsDot())
                            symbolList.pushSymbol(NUMBER, "0");
                        symbolList.pushSymbol(symbolType, value);
                        break;
                    case LBR:
                    case FUNCTION:
                        symbolList.pushSymbol(LBR, "(");
                        symbolList.pushSymbol(symbolType, value);
                        symbolList.pushSymbol(OPERATION, MUL);
                        break;
                    case VARIABLE:
                        symbolList.pushSymbol(OPERATION, MUL);
                        symbolList.pushSymbol(symbolType, value);
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
        if (lastType == NUMBER && !formula.getVal().equals("e") && !formula.getVal().equals("π"))
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
        formula.setActive(true);
    }

    /*
    Возвращает сохранённую формулу в виде строки в формате латеха.
     */
    public static String getFormula() {
        return formula.toLaTeX();
    }

    /*
    Возвращает формулу в виде обычной строки.
     */
    public static String getString() { return formula.toString();}

    /*
    Взовращает функцию.
     */
    public static Function getFunction() {
        // Подсчёт скобок и добавление закрывающих при нужде.
        int leftBrCount = formula.countType(LBR);
        int rightBrCount = formula.countType(RBR);
        while (rightBrCount < leftBrCount) {
            pushAction(RBR, ")");
            rightBrCount++;
        }
        formula.setActive(false);
        return formula.parse();
    }

//    public static Function parse(SymbolList symbolList) {
//        if (symbolList != null) {
//            checkParentheses(symbolList);
//            substituteUnaryMinus(symbolList);
//            Quantity root = doOrderOfOperations(symbolList);
//            if (root == null) {
//                return null;
//            }
//            return new Function(root, x);
//        }
//        return null;
//    }
//
//    private static Quantity doOrderOfOperations(SymbolList symbolList) {
//        /*
//         * Order of operations in reverse:
//         * Addition, Subtraction, Division, Multiplication, Modulo, Exponentiation, Function, Parentheses, Variables, Numbers
//         * All from right to left
//         */
//        int location = 0;	// Location of some operator
//        Quantity ret = new RealNumber(0.0);
//
//        location = scanFromRight(symbolList, SymbolList.OperationType.ADD);
//        if (location != -1) {
//            SymbolList left = symbolList.getSubList(0, location);
//            SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
//            ret = new Sum(doOrderOfOperations(left), doOrderOfOperations(right));
//        } else {
//            location = scanFromRight(symbolList, SymbolList.OperationType.SUB);
//            if (location != -1) {
//                SymbolList left = symbolList.getSubList(0, location);
//                SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
//                ret = new Difference(doOrderOfOperations(left), doOrderOfOperations(right));
//            } else {
//                location = scanFromRight(symbolList, SymbolList.OperationType.DIV);
//                if (location != -1) {
//                    SymbolList left = symbolList.getSubList(0, location);
//                    SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
//                    ret = new Quotient(doOrderOfOperations(left), doOrderOfOperations(right));
//                } else {
//                    location = scanFromRight(symbolList, SymbolList.OperationType.MUL);
//                    if (location != -1) {
//                        SymbolList left = symbolList.getSubList(0, location);
//                        SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
//                        ret = new Product(doOrderOfOperations(left), doOrderOfOperations(right));
//                    } else {
//                        location = scanFromRight(symbolList, SymbolList.OperationType.POW);
//                        if (location != -1) {
//                            SymbolList left = symbolList.getSubList(0, location);
//                            SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
//                            ret = new Power(doOrderOfOperations(left), doOrderOfOperations(right));
//                        } else {
//                            location = scanFromRight(symbolList, SymbolList.SymbolType.FUNCTION);
//                            if (location != -1) {
//                                int endParams = getFunctionParamsEnd(symbolList, location + 2);
//                                if (endParams != -1) {
//                                    SymbolList paramString = symbolList.getSubList(location + 2, endParams);
//                                    ret = parseFunctionParams(paramString, (SymbolList.FunctionType) symbolList.getVal(location));
//                                }
//                            } else if (symbolList.size() >= 2 && symbolList.getType(symbolList.size() - 1) == SymbolList.SymbolType.RBR
//                                    && symbolList.getType(0) == SymbolList.SymbolType.LBR) {
//                                SymbolList inParentheses = symbolList.getSubList(1, symbolList.size() - 1);
//                                ret = doOrderOfOperations(inParentheses);
//                            } else {
//                                location = scanFromRight(symbolList, SymbolList.SymbolType.VARIABLE);
//                                if (location != -1) {
//                                //    ret = x;
//                                    ret = x;
//                                } else {
//                                    location = scanFromRight(symbolList, SymbolList.SymbolType.NUMBER);
//                                    if (location != -1) {
//                                        String number = (String)symbolList.getVal(location);
//                                        if(number.compareTo("e") == 0)
//                                            ret = new RealNumber(Math.E);
//                                        else if(number.compareTo("π") == 0)
//                                            ret = new RealNumber(Math.PI);
//                                        else
//                                            ret = new RealNumber(Double.parseDouble((String)symbolList.getVal(location)));
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//        return ret;
//    }
//
//    private static Quantity parseFunctionParams(SymbolList paramString, SymbolList.FunctionType type) {
//        List<SymbolList> params = new ArrayList<>();
//        int start = 0;
//        for (int i = 0; i < paramString.size(); i++) {
//            SymbolList.SymbolType t = paramString.getType(i);
//            if (t == SymbolList.SymbolType.COMMA) {
//                params.add(paramString.getSubList(start, i));
//                start = i + 1;
//            }
//        }
//        params.add(paramString.getSubList(start, paramString.size()));
//
//        if (params.size() == 0) return null;
//
//        if (params.size() == 1) {
//            Quantity param1 = doOrderOfOperations(params.get(0));
//            switch (type) {
//                case ABS:
//                    return new AbsoluteValue(param1);
//                case CEILING:
//                    return new Ceiling(param1);
//                case FLOOR:
//                    return new Floor(param1);
//                case SIN:
//                    return new Sine(param1);
//                case COS:
//                    return new Cosine(param1);
//                case TAN:
//                    return new Tangent(param1);
////                case COTAN:
////                    return new Cotangent(param1);
////                case SQRT:
////                    return new SquareRoot(param1);
//                case LN:
//                    return new Ln(param1);
//                default:
//                    return null;
//            }
//        }
//        else if (params.size() == 2) {
//           // todo
//        }
//        return null;
//    }
//
//    private static int getFunctionParamsEnd(SymbolList symbolList, int location) {
//        int openParentheses = 0;
//        for (int i = location; i < symbolList.size(); i++) {
//            SymbolList.SymbolType t = symbolList.getType(i);
//            if (t == SymbolList.SymbolType.LBR) {
//                openParentheses++;
//            } else if (t == SymbolList.SymbolType.RBR) {
//                if (openParentheses == 0) {
//                    return i;
//                }
//                openParentheses--;
//            }
//        }
//        return -1;
//    }
//
//    private static int scanFromRight(SymbolList symbolList, SymbolList.OperationType type) {
//        int openParentheses = 0;
//        for (int i = symbolList.size() - 1; i >= 0; i--) {
//            SymbolList.SymbolType t = symbolList.getType(i);
//            if (t == SymbolList.SymbolType.RBR) {
//                openParentheses++;
//            } else if (t == SymbolList.SymbolType.LBR) {
//                openParentheses--;
//            } else if (t == SymbolList.SymbolType.OPERATION) {
//                if ((SymbolList.OperationType)symbolList.getVal(i) == type && openParentheses == 0) {
//                    return i;
//                }
//            }
//        }
//        return -1;
//    }
//    private static int scanFromRight(SymbolList symbolList, SymbolList.SymbolType type) {
//        int openParentheses = 0;
//        for (int i = symbolList.size() - 1; i >= 0; i--) {
//            SymbolList.SymbolType t = symbolList.getType(i);
//            if (t == SymbolList.SymbolType.RBR) {
//                openParentheses++;
//            } else if (t == SymbolList.SymbolType.LBR) {
//                openParentheses--;
//            } else if (t == type) {
//                return i;
//            }
//            // TODO проверить равнство количеств открывающих и закрывающих скобок
//        }
//        return -1;
//    }
//
//    private static void substituteUnaryMinus(SymbolList symbolList) {
//        SymbolList.SymbolType prev = null;
//        for (int i = 0; i < symbolList.size(); i++) {
//            SymbolList.SymbolType t = symbolList.getType(i);
//            if(t == SymbolList.SymbolType.OPERATION) {
//                if ((SymbolList.OperationType)symbolList.getVal(i) == SymbolList.OperationType.SUB) {
//                    if (prev == null || !(prev == SymbolList.SymbolType.NUMBER || prev == SymbolList.SymbolType.VARIABLE || prev == SymbolList.SymbolType.RBR)) {
//                        // Ex: -x becomes (0-1)*x
//                        symbolList.removeSymbols(i, i);
//                        symbolList.pushSymbol(SymbolList.SymbolType.OPERATION, SymbolList.OperationType.MUL, i);
//                        symbolList.pushSymbol(SymbolList.SymbolType.RBR, null, i);
//                        symbolList.pushSymbol(SymbolList.SymbolType.NUMBER, 1, i);
//                        symbolList.pushSymbol(SymbolList.SymbolType.OPERATION, SymbolList.OperationType.SUB, i);
//                        symbolList.pushSymbol(SymbolList.SymbolType.NUMBER, 1, i);
//                        symbolList.pushSymbol(SymbolList.SymbolType.LBR, null, i);
//                        i += 6;
//                    }
//                }
//            }
//            prev = t;
//        }
//    }
//
//    private static void checkParentheses(SymbolList symbolList) {
//        // Test for correct number of parentheses
//        int openParentheses = 0;
//        for (int i = 0; i < symbolList.size(); i++) {
//            SymbolList.SymbolType symbolType = symbolList.getType(i);
//            if (symbolType == SymbolList.SymbolType.LBR) {
//                openParentheses++;
//            } else if (symbolType == SymbolList.SymbolType.RBR) {
//                openParentheses--;
//            }
//            if (openParentheses < 0) {
//                // TODO реализовать обработку ошибки
//            }
//        }
//        if (openParentheses > 0) {
//            // TODO реализовать обработку ошибки
//        }
//    }
//
//    /*
//    Считает определённый интеграл.
//    * */
//    public static double integrate(Function function, double x1, double x2) {
//        double dx = 0.0000001;
//        int step = (int) Math.ceil((x2 - x1) / dx);
//        double total = 0;
//        double x = x1;
//        for (int i = 0; i < step - 1; ++i, x += dx) {
//   //         double m = (function.evaluateAt(x) + function.evaluateAt(x + dx)) / 2;
//            double m = (function.evaluateAt(x) + function.evaluateAt(x + dx)) / 2;
//            total += dx * m;
//        }
//  //      double m = (function.evaluateAt(x) + function.evaluateAt(x2)) / 2;
//        double m = (function.evaluateAt(x) + function.evaluateAt(x2)) / 2;
//        total += dx * m;
//        return total;
//    }
}
