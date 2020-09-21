package com.sct.smartcalc;

import com.sct.smartcalc.graphs.expression.AbsoluteValue;
import com.sct.smartcalc.graphs.expression.Ceiling;
import com.sct.smartcalc.graphs.expression.Cosine;
import com.sct.smartcalc.graphs.expression.Cotangent;
import com.sct.smartcalc.graphs.expression.Difference;
import com.sct.smartcalc.graphs.expression.Floor;
import com.sct.smartcalc.graphs.expression.Function;
import com.sct.smartcalc.graphs.expression.Ln;
import com.sct.smartcalc.graphs.expression.Power;
import com.sct.smartcalc.graphs.expression.Product;
import com.sct.smartcalc.graphs.expression.Quantity;
import com.sct.smartcalc.graphs.expression.Quotient;
import com.sct.smartcalc.graphs.expression.Sine;
import com.sct.smartcalc.graphs.expression.SquareRoot;
import com.sct.smartcalc.graphs.expression.Sum;
import com.sct.smartcalc.graphs.expression.Tangent;
import com.sct.smartcalc.graphs.expression.Var;
import com.sct.smartcalc.graphs.expression.RealNumber;
import com.sct.smartcalc.util.SymbolList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import static com.sct.smartcalc.util.SymbolList.OperationType.MUL;
import static com.sct.smartcalc.util.SymbolList.SymbolType.FUNCTION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.LBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.NUMBER;
import static com.sct.smartcalc.util.SymbolList.SymbolType.OPERATION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.VARIABLE;

public class Integral {


    private static SymbolList functionSymbolList = new SymbolList();
    private static SymbolList lowerLimit = new SymbolList();
    private static SymbolList upperLimit = new SymbolList();

    private static boolean inDegrees = false;
    private static Var x = new Var();

    private enum State {lowerLimit, upperLimit, function};
    private static State state = State.lowerLimit;


    /*
    Приводит в рабочее состояние перед использованием.
     */
    public static void setup() {
        lowerLimit.setActive(true);
        upperLimit.setActive(false);
        functionSymbolList.setActive(false);
        clear();
    }

    public static void pushAction(SymbolList.SymbolType symbolType, Object value) {
        if(state == State.lowerLimit) {
            if(symbolType != VARIABLE)
                pushActionTo(lowerLimit, symbolType, value);
        } else if (state == State.upperLimit) {
            if(symbolType != VARIABLE)
                pushActionTo(upperLimit, symbolType, value);
        } else if (state == State.function) {
            pushActionTo(functionSymbolList, symbolType, value);
        }
    }

    /*
    Добавляет символ в формулу.
     */
    private static void pushActionTo(SymbolList symbolList, SymbolList.SymbolType symbolType, Object value) {

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

    public static void nextState() {
        state = State.values()[state.ordinal() + 1];
    }

    public static void prevState() {
        state = State.values()[state.ordinal() - 1];
    }

    public static void popAction() {
        if(state == State.lowerLimit) {
            popActionFrom(lowerLimit);
        } else if (state == State.upperLimit) {
            popActionFrom(upperLimit);
        } else if (state == State.function) {
            popActionFrom(functionSymbolList);
        }
    }

    /*
    Удаляет последний введённый символ.
     */
    public static void popActionFrom(SymbolList symbolList) {

        SymbolList.SymbolType lastType = symbolList.getType();
        if (lastType == NUMBER && !symbolList.getVal().equals("e") && !symbolList.getVal().equals("π"))
            symbolList.popNumeral();
        else symbolList.popSymbol();

    }

    public static void switchNumberSign() {
        functionSymbolList.switchNumberSign();
    }

    public static void moveCursor(boolean forward) {
        if(state == State.lowerLimit) {
            if(lowerLimit.currentPos() == lowerLimit.size() - 1 && forward) {
                nextState();
                lowerLimit.setActive(false);
                upperLimit.setActive(true);
            }
            else lowerLimit.moveCurrent(forward);
        } else if (state == State.upperLimit) {
            if(upperLimit.currentPos() == upperLimit.size() - 1 && forward) {
                nextState();
                upperLimit.setActive(false);
                functionSymbolList.setActive(true);
            }
            else if (upperLimit.currentPos() == 0 && !forward) {
                prevState();
                upperLimit.setActive(false);
                lowerLimit.setActive(true);
            }
            else upperLimit.moveCurrent(forward);
        } else {
            if (functionSymbolList.currentPos() == 0 && !forward) {
                prevState();
                functionSymbolList.setActive(false);
                upperLimit.setActive(true);
            }
            else
                functionSymbolList.moveCurrent(forward);
        }
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
        lowerLimit.clear();
        lowerLimit.pushSymbol(NUMBER, "0");
        upperLimit.clear();
        upperLimit.pushSymbol(NUMBER, "0");
        functionSymbolList.clear();
        functionSymbolList.pushSymbol(NUMBER, "0");
    }

    /*
    Возвращает сохранённую формулу в виде строки в формате латеха.
     */
    public static String getFormula() {
        String str = "$ \\int\\limits_{" + lowerLimit.toLaTeX() + "}^" + "{" + upperLimit.toLaTeX() + "} " + functionSymbolList.toLaTeX() + "dx";
        str += "$";
        return str;
    }

    /*
    Возвращает формулу в виде обычной строки.
    * */
    public static String getString() { return functionSymbolList.toString();}

    /*
    Взовращает функцию.
     */
    public static Function getFunction() {

        return parse(functionSymbolList);
    }

    public static Function parse(SymbolList symbolList) {
        if (symbolList != null) {
            checkParentheses(symbolList);
            substituteUnaryMinus(symbolList);
            Quantity root = doOrderOfOperations(symbolList);
            if (root == null) {
                return null;
            }
            return new Function(root, x);
        }
        return null;
    }

    private static Quantity doOrderOfOperations(SymbolList symbolList) {
        /*
         * Order of operations in reverse:
         * Addition, Subtraction, Division, Multiplication, Modulo, Exponentiation, Function, Parentheses, Variables, Numbers
         * All from right to left
         */
        int location = 0;	// Location of some operator
        Quantity ret = new RealNumber(0.0);

        location = scanFromRight(symbolList, SymbolList.OperationType.ADD);
        if (location != -1) {
            SymbolList left = symbolList.getSubList(0, location);
            SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
            ret = new Sum(doOrderOfOperations(left), doOrderOfOperations(right));
        } else {
            location = scanFromRight(symbolList, SymbolList.OperationType.SUB);
            if (location != -1) {
                SymbolList left = symbolList.getSubList(0, location);
                SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
                ret = new Difference(doOrderOfOperations(left), doOrderOfOperations(right));
            } else {
                location = scanFromRight(symbolList, SymbolList.OperationType.DIV);
                if (location != -1) {
                    SymbolList left = symbolList.getSubList(0, location);
                    SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
                    ret = new Quotient(doOrderOfOperations(left), doOrderOfOperations(right));
                } else {
                    location = scanFromRight(symbolList, SymbolList.OperationType.MUL);
                    if (location != -1) {
                        SymbolList left = symbolList.getSubList(0, location);
                        SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
                        ret = new Product(doOrderOfOperations(left), doOrderOfOperations(right));
                    } else {
                        location = scanFromRight(symbolList, SymbolList.OperationType.POW);
                        if (location != -1) {
                            SymbolList left = symbolList.getSubList(0, location);
                            SymbolList right = symbolList.getSubList(location + 1, symbolList.size());
                            ret = new Power(doOrderOfOperations(left), doOrderOfOperations(right));
                        } else {
                            location = scanFromRight(symbolList, SymbolList.SymbolType.FUNCTION);
                            if (location != -1) {
                                int endParams = getFunctionParamsEnd(symbolList, location + 2);
                                if (endParams != -1) {
                                    SymbolList paramString = symbolList.getSubList(location + 2, endParams);
                                    ret = parseFunctionParams(paramString, (SymbolList.FunctionType) symbolList.getVal(location));
                                }
                            } else if (symbolList.size() >= 2 && symbolList.getType(symbolList.size() - 1) == SymbolList.SymbolType.RBR
                                    && symbolList.getType(0) == SymbolList.SymbolType.LBR) {
                                SymbolList inParentheses = symbolList.getSubList(1, symbolList.size() - 1);
                                ret = doOrderOfOperations(inParentheses);
                            } else {
                                location = scanFromRight(symbolList, SymbolList.SymbolType.VARIABLE);
                                if (location != -1) {
                                    ret = x;
                                } else {
                                    location = scanFromRight(symbolList, SymbolList.SymbolType.NUMBER);
                                    if (location != -1) {
                                        String number = (String)symbolList.getVal(location);
                                        if(number.compareTo("e") == 0)
                                            ret = new RealNumber(Math.E);
                                        else if(number.compareTo("π") == 0)
                                            ret = new RealNumber(Math.PI);
                                        else
                                            ret = new RealNumber(Double.parseDouble((String)symbolList.getVal(location)));
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return ret;
    }

    private static Quantity parseFunctionParams(SymbolList paramString, SymbolList.FunctionType type) {
        List<SymbolList> params = new ArrayList<>();
        int start = 0;
        for (int i = 0; i < paramString.size(); i++) {
            SymbolList.SymbolType t = paramString.getType(i);
            if (t == SymbolList.SymbolType.COMMA) {
                params.add(paramString.getSubList(start, i));
                start = i + 1;
            }
        }
        params.add(paramString.getSubList(start, paramString.size()));

        if (params.size() == 0) return null;

        if (params.size() == 1) {
            Quantity param1 = doOrderOfOperations(params.get(0));
            switch (type) {
                case ABS:
                    return new AbsoluteValue(param1);
                case CEILING:
                    return new Ceiling(param1);
                case FLOOR:
                    return new Floor(param1);
                case SIN:
                    return new Sine(param1);
                case COS:
                    return new Cosine(param1);
                case TAN:
                    return new Tangent(param1);
                case COTAN:
                    return new Cotangent(param1);
                case SQRT:
                    return new SquareRoot(param1);
                case LN:
                    return new Ln(param1);
                default:
                    return null;
            }
        }
        else if (params.size() == 2) {
            // todo
        }
        return null;
    }

    private static int getFunctionParamsEnd(SymbolList symbolList, int location) {
        int openParentheses = 0;
        for (int i = location; i < symbolList.size(); i++) {
            SymbolList.SymbolType t = symbolList.getType(i);
            if (t == SymbolList.SymbolType.LBR) {
                openParentheses++;
            } else if (t == SymbolList.SymbolType.RBR) {
                if (openParentheses == 0) {
                    return i;
                }
                openParentheses--;
            }
        }
        return -1;
    }

    private static int scanFromRight(SymbolList symbolList, SymbolList.OperationType type) {
        int openParentheses = 0;
        for (int i = symbolList.size() - 1; i >= 0; i--) {
            SymbolList.SymbolType t = symbolList.getType(i);
            if (t == SymbolList.SymbolType.RBR) {
                openParentheses++;
            } else if (t == SymbolList.SymbolType.LBR) {
                openParentheses--;
            } else if (t == SymbolList.SymbolType.OPERATION) {
                if ((SymbolList.OperationType)symbolList.getVal(i) == type && openParentheses == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
    private static int scanFromRight(SymbolList symbolList, SymbolList.SymbolType type) {
        int openParentheses = 0;
        for (int i = symbolList.size() - 1; i >= 0; i--) {
            SymbolList.SymbolType t = symbolList.getType(i);
            if (t == SymbolList.SymbolType.RBR) {
                openParentheses++;
            } else if (t == SymbolList.SymbolType.LBR) {
                openParentheses--;
            } else if (t == type) {
                return i;
            }
            // TODO проверить равнство количеств открывающих и закрывающих скобок
        }
        return -1;
    }

    private static void substituteUnaryMinus(SymbolList symbolList) {
        SymbolList.SymbolType prev = null;
        for (int i = 0; i < symbolList.size(); i++) {
            SymbolList.SymbolType t = symbolList.getType(i);
            if(t == SymbolList.SymbolType.OPERATION) {
                if ((SymbolList.OperationType)symbolList.getVal(i) == SymbolList.OperationType.SUB) {
                    if (prev == null || !(prev == SymbolList.SymbolType.NUMBER || prev == SymbolList.SymbolType.VARIABLE || prev == SymbolList.SymbolType.RBR)) {
                        // Ex: -x becomes (0-1)*x
                        symbolList.removeSymbols(i, i);
                        symbolList.pushSymbol(SymbolList.SymbolType.OPERATION, SymbolList.OperationType.MUL, i);
                        symbolList.pushSymbol(SymbolList.SymbolType.RBR, null, i);
                        symbolList.pushSymbol(SymbolList.SymbolType.NUMBER, 1, i);
                        symbolList.pushSymbol(SymbolList.SymbolType.OPERATION, SymbolList.OperationType.SUB, i);
                        symbolList.pushSymbol(SymbolList.SymbolType.NUMBER, 1, i);
                        symbolList.pushSymbol(SymbolList.SymbolType.LBR, null, i);
                        i += 6;
                    }
                }
            }
            prev = t;
        }
    }

    private static void checkParentheses(SymbolList symbolList) {
        // Test for correct number of parentheses
        int openParentheses = 0;
        for (int i = 0; i < symbolList.size(); i++) {
            SymbolList.SymbolType symbolType = symbolList.getType(i);
            if (symbolType == SymbolList.SymbolType.LBR) {
                openParentheses++;
            } else if (symbolType == SymbolList.SymbolType.RBR) {
                openParentheses--;
            }
            if (openParentheses < 0) {
                // TODO реализовать обработку ошибки
            }
        }
        if (openParentheses > 0) {
            // TODO реализовать обработку ошибки
        }
    }

    /*
    Считает определённый интеграл.
    * */
    public static double integrate() {
        Function function = parse(functionSymbolList);
        double x1 = parse(lowerLimit).evaluateAt(0);
        double x2 = parse(upperLimit).evaluateAt(0);
        double dx = 0.00001;
        int step = (int) Math.ceil((x2 - x1) / dx);
        double total = 0;
        double x = x1;

        for(; x < x2; x += dx) {
            double val = function.evaluateAt(x);
            total += val;
        }
        total *= dx;

//        for (int i = 0; i < step - 1; ++i, x += dx) total += dx * (function.evaluateAt(x) + function.evaluateAt(x + dx)) / 2;
//        double m = (function.evaluateAt(x) + function.evaluateAt(x2)) / 2;
//        total += dx * m;
        return total;
    }

    final static int N = 5;

    static double[] lroots = new double[N];
    static double[] weight = new double[N];
    static double[][] lcoef = new double[N + 1][N + 1];

    static void legeCoef() {
        lcoef[0][0] = lcoef[1][1] = 1;

        for (int n = 2; n <= N; n++) {

            lcoef[n][0] = -(n - 1) * lcoef[n - 2][0] / n;

            for (int i = 1; i <= n; i++) {
                lcoef[n][i] = ((2 * n - 1) * lcoef[n - 1][i - 1]
                        - (n - 1) * lcoef[n - 2][i]) / n;
            }
        }
    }

    static double legeEval(int n, double x) {
        double s = lcoef[n][n];
        for (int i = n; i > 0; i--)
            s = s * x + lcoef[n][i - 1];
        return s;
    }

    static double legeDiff(int n, double x) {
        return n * (x * legeEval(n, x) - legeEval(n - 1, x)) / (x * x - 1);
    }

    static void legeRoots() {
        double x, x1;
        for (int i = 1; i <= N; i++) {
            x = Math.cos(Math.PI * (i - 0.25) / (N + 0.5));
            do {
                x1 = x;
                x -= legeEval(N, x) / legeDiff(N, x);
            } while (x != x1);

            lroots[i - 1] = x;

            x1 = legeDiff(N, x);
            weight[i - 1] = 2 / ((1 - x * x) * x1 * x1);
        }
    }

    public static double legeInte() {
        Function f = parse(functionSymbolList);
        double a = parse(lowerLimit).evaluateAt(0);
        double b = parse(upperLimit).evaluateAt(0);

        legeCoef();
        legeRoots();

        double c1 = (b - a) / 2, c2 = (b + a) / 2, sum = 0;
        for (int i = 0; i < N; i++)
            sum += weight[i] * f.evaluateAt(c1 * lroots[i] + c2);
        return round(c1 * sum, 4);
    }

    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
