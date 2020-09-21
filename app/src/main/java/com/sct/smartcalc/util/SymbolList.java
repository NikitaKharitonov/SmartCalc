package com.sct.smartcalc.util;

import android.app.UiAutomation;
import android.support.annotation.NonNull;

import com.sct.smartcalc.graphs.expression.AbsoluteValue;
import com.sct.smartcalc.graphs.expression.Ceiling;
import com.sct.smartcalc.graphs.expression.Cosine;
import com.sct.smartcalc.graphs.expression.Difference;
import com.sct.smartcalc.graphs.expression.Floor;
import com.sct.smartcalc.graphs.expression.Function;
import com.sct.smartcalc.graphs.expression.Ln;
import com.sct.smartcalc.graphs.expression.Power;
import com.sct.smartcalc.graphs.expression.Product;
import com.sct.smartcalc.graphs.expression.Quantity;
import com.sct.smartcalc.graphs.expression.Quotient;
import com.sct.smartcalc.graphs.expression.RealNumber;
import com.sct.smartcalc.graphs.expression.Sine;
import com.sct.smartcalc.graphs.expression.Sum;
import com.sct.smartcalc.graphs.expression.Tangent;
import com.sct.smartcalc.graphs.expression.Var;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.sct.smartcalc.util.SymbolList.SymbolType.LBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.RBR;

public class SymbolList implements Cloneable, Serializable {

	public enum SymbolType {
		NUMBER,
		COMMA,
		FUNCTION,
		LBR,
		RBR,
		OPERATION,
		VARIABLE,
		DERIVATIVE,
		INTEGRAL,
		CONSTANTA
	}

	public enum FunctionType {
		SIN,
		COS,
		TAN,
		SQRT,
		CUSTOM,

		// дополнительные для графиков (^_^)
        LN,
        ABS,
        COTAN,
        FLOOR,
        CEILING,

		// complex
		CONJ,
		ARG
	}

	public enum OperationType {
		ADD,
		SUB,
		MUL,
		DIV,
		POW
	}

	private LinkedList<SymbolType> types;
	private LinkedList<Object> values;
	private int size;
	private int current;
	private boolean isActive = true;
	private Var x;

	public SymbolList() {
		this.types = new LinkedList<>();
		this.values = new LinkedList<>();
		this.size = 0;
		this.current = -1;
		this.x = new Var();
	}

	private SymbolList(LinkedList<SymbolType> inT, LinkedList<Object> inV) {
		this.types = inT;
		this.values = inV;
		this.size = inT.size();
		this.current = this.size - 1;
	}

	public SymbolList(SymbolList symbolList) {
		this.types = (LinkedList)symbolList.types.clone();
		this.values = (LinkedList)symbolList.values.clone();
		this.size = symbolList.size;
		this.current = symbolList.current;
		this.isActive = symbolList.isActive;
	}

//	@Override
//	public String toString() {
//		StringBuilder str = new StringBuilder();
//		for (int i = 0; i < this.size; i++) {
//			switch (this.getType(i)) {
//				case NUMBER:
//				case VARIABLE:
//					str.append((String)this.getVal(i));
//					break;
//				case FUNCTION:
//					switch ((FunctionType)this.getVal(i)) {
//						case SIN:
//							str.append("sin");
//							break;
//						case COS:
//							str.append("cos");
//							break;
//						case TAN:
//							str.append("tan");
//							break;
//						case SQRT:
//							str.append("√");
//							break;
//						case CEILING:
//							str.append("ceiling");
//							break;
//						case FLOOR:
//							str.append("floor");
//							break;
//						case COTAN:
//							str.append("cotan");
//							break;
//						case LN:
//							str.append("ln");
//							break;
//						case ABS:
//							str.append("abs");
//							break;
//						case CUSTOM:
//							/*
//							Забирает из места хранения кастомных функций выбранную.
//							 */
//					}
//					break;
//				case LBR:
//					str.append("(");
//					break;
//				case RBR:
//					str.append(")");
//					break;
//				case OPERATION:
//					switch ((OperationType)this.getVal(i)) {
//						case ADD:
//							str.append("+");
//							break;
//						case SUB:
//							str.append("-");
//							break;
//						case MUL:
//							str.append("×");
//							break;
//						case DIV:
//							str.append("÷");
//							break;
//						case POW:
//							str.append("^");
//					}
//					break;
//			}
//		}
//		return str.toString();
//	}

	public String toLaTeX() {
		StringBuilder str = new StringBuilder();
		str.append("$ \\sf ");

		boolean noFrac = false;
		int depth = 0;
		int signDepth = 0;
		int signSetRBR = 0;
		int signSet = 0;

		// для скобок функции потолок
		int ceilDepth = 0;
		int ceilSetRBR = 0;
		int ceilSet = 0;

		// для скобок функции пол
		int floorDepth = 0;
		int floorSetRBR = 0;
		int floorSet = 0;

		// для прямых скобок
		int absDepth = 0;
		int absSetRBR = 0;
		int absSet = 0;

		for (int i = 0; i < this.size; i++) {
			// Окрашивание текущего выбранного числа.
			if (i == this.current && isActive)
				str.append("\\textcolor{#00B040}{");
			// Добавление символа в общую строку.
			switch (this.getType(i)) {
				case NUMBER:
					// Число.
					str.append((String)this.getVal(i));
					// Если перед числом была операция нуждающаяся в `}`, оно ставится.
					if (signSet != 0 && i != this.current) {
						str.append("}");
						signSet--;
					}
					break;
				case VARIABLE:
					str.append("x");
					// Если перед числом была операция нуждающаяся в `}`, оно ставится.
					if (signSet != 0 && i != this.current) {
						str.append("}");
						signSet--;
					}
					break;
				case FUNCTION:
					switch ((FunctionType)this.getVal(i)) {
						case SIN:
							str.append("\\sin");
							break;
						case COS:
							str.append("\\cos");
							break;
						case TAN:
							str.append("\\tan");
							break;
						case SQRT:
							str.append("\\sqrt{");
							signSet++;
							break;
                        case ABS:
                        	str.append("|");
                        	++absSet;
                            break;
                        case LN:
                        	str.append("\\ln");
                            break;
                        case COTAN:
                        	str.append("\\cot");
                            break;
                        case FLOOR:
                        	str.append("\\lfloor");
                        	++floorSet;
                            break;
                        case CEILING:
                        	str.append("\\lceil");
                        	++ceilSet;
                            break;
						case CONJ:
							str.append("conj");
							break;
						case ARG:
							str.append("arg");
							break;
						case CUSTOM:
							/*
							Забирает из места хранения кастомных функций выбранную.

							str.append("\\operatorname{");
							str.append(>>FUNCTION NAME FROM CACHE<<);
							str.append("}");
							 */
					}
					break;
				case LBR:
					// `}` ставится после первой `)`, что неприемлемо в ситуации `{(x+(3*5)}-6)`
					// В данной ситуации `}` стоит не у той скобки.
					if (signSetRBR != 0)
						signDepth++;
					// Если после операции, нуждающейся в `}`, стоит скобка,
					// то содержимое этой скобки пойдёт в операцию.
					if (signSet != 0) {
						signSet--;
						signSetRBR++;
					}

					// аналогично для прямых скобок
					if (absSetRBR != 0)
						absDepth++;
					if (absSet != 0) {
						absSet--;
						absSetRBR++;
					}

					// аналогично для скобок функции потолок
					if (ceilSetRBR != 0)
						ceilDepth++;
					if (ceilSet != 0) {
						ceilSet--;
						ceilSetRBR++;
					}

					// аналогично для скобок функции пол
					if (floorSetRBR != 0)
						floorDepth++;
					if (floorSet != 0) {
						floorSet--;
						floorSetRBR++;
					}
					str.append("(");
					depth++;
					break;
				case RBR:
					str.append(")");
					depth--;
					// Ставим `}` после нужной закрывающей скобки.
					if (signSetRBR != 0 && signDepth == 0) {
						str.append("}");
						signSetRBR--;
					}
					if (signDepth != 0) signDepth--;

					// аналогично для прямых скобок
					if (absSetRBR != 0 && absDepth == 0) {
						str.append("|");
						absSetRBR--;
					}
					if (absDepth != 0) absDepth--;

					// аналогично для скобок функции потолок
					if (ceilSetRBR != 0 && ceilDepth == 0) {
						str.append("\\rceil");
						ceilSetRBR--;
					}
					if (ceilDepth != 0) ceilDepth--;

					// аналогично для скобок функции пол
					if (floorSetRBR != 0 && floorDepth == 0) {
						str.append("\\rfloor");
						floorSetRBR--;
					}
					if (floorDepth != 0) floorDepth--;

					break;
				case OPERATION:
					switch ((OperationType)this.getVal(i)) {
						case ADD:
							str.append("+");
							break;
						case SUB:
							str.append("-");
							break;
						case MUL:
							//`Число * НЕ число` - в этом случае знак умножения опускается.
							// Во всех остальных случаях знак умножения появляется.
							if (i != 0 && i != this.size - 1) {
								if (this.getType(i - 1) != SymbolType.NUMBER
										|| this.getType(i + 1) == SymbolType.NUMBER
										|| i == this.current)
									str.append("\\cdot ");
							}
							else str.append("\\cdot ");
							break;
						case DIV:
							// Деление очень особенное.
							if (i == this.size - 1 || i == this.current)
								str.append("/");
							else {
								int index = 0;
								if (this.getType(i - 1) == SymbolType.RBR) {
									// Поместить frac{ LBR ... RBR } в уже существующую строку.
									int RBDepth = 1;
									for (int j = str.length() - 1; j > 0 || RBDepth == 0; j--) {
										if (str.charAt(j) == '(') {
											RBDepth--;
											if (RBDepth == 0) index = j;
										}
										if (str.charAt(j) == ')') RBDepth++;
									}
								}
								else {
									// Поместить frac{ NUMBER } в уже существущую строку.
									for (int j = str.length() - 1; j > 0 && index == 0; j--) {
										if (!Character.isDigit(str.charAt(j)) && str.charAt(j) != '}')
											index = j + 1;
									}
								}
								str.insert(index, " \\frac{");
								str.append("}{");
								signSet++;
							}
							break;
						case POW:
							// Возведение в степень не рендерится, если нет аргумента.
							str.append("^{");
							signSet++;
					}
					break;
				case DERIVATIVE:
					/*
					Не реализовано
					 */
					break;
				case INTEGRAL:
					/*
					Не реализовано
					 */
			}
			if (i == this.current && isActive) str.append("}");
		}
		// Поскольку у нас не всегда законченные формулы, закрываем скобки для рендера.
		if (lastCharIsDot(this.size - 1)) str.append("\\textcolor{#AAAAAA}{0}");
		for (int i = depth; i > 0; i--) str.append("\\textcolor{#AAAAAA}{)}");
		for (; signSet != 0; signSet--) str.append("}");
		for (; signSetRBR != 0; signSetRBR--) str.append("}");
		str.append(" $");
		return str.toString();
	}

	@Override
	public SymbolList clone() {
		try {
			return (SymbolList) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	Проверка объекта на соответствие типу. Для LBR и RBR нет символов.
	 */
	private boolean typeMatch(SymbolType t, Object val) {
		if (t == SymbolType.NUMBER && !(val instanceof String)) return false;
		if (t == SymbolType.FUNCTION && !(val instanceof FunctionType)) return false;
		if (t == SymbolType.OPERATION && !(val instanceof OperationType)) return false;
		if (t == SymbolType.VARIABLE && !(val instanceof String)) return false;
		if (t == SymbolType.DERIVATIVE && !(val instanceof Integer)) return false;
		if (t == SymbolType.INTEGRAL && !(val instanceof Integer[])) return false;
		return true;
	}

	public boolean lastCharIsDot() {
		if (!isDouble()) return false;
		else {
			String number = (String)this.values.get(this.current);
			return !number.substring(0, number.length() - 1).contains(".");
		}
	}

	public boolean lastCharIsDot(int i) {
		if (!isDouble(i)) return false;
		else {
			String number = (String)this.values.get(i);
			return !number.substring(0, number.length() - 1).contains(".");
		}
	}

	public boolean isDouble() {
		if (this.types.get(this.current) != SymbolType.NUMBER) return false;
		else return ((String)this.values.get(this.current)).contains(".");
	}

	public boolean isDouble(int i) {
		if (this.types.get(i) != SymbolType.NUMBER) return false;
		else return ((String)this.values.get(i)).contains(".");
	}

	/*
	Помещает новый символ в список в место после выделенного элемента и выделяет этот символ.
	 */
	public void pushSymbol(SymbolType type, Object value) {
		if (!typeMatch(type, value)) return;
		if (current + 1 == size) {
			this.types.add(type);
			this.values.add(value);
		} else {
			this.types.add(current + 1, type);
			this.values.add(current + 1, value);
		}
		this.size++;
		this.current++;
	}

	public void pushSymbol(SymbolType type, Object value, int i) {
		if (!typeMatch(type, value)) return;
		if (i < 0 || i > this.size) return;
		if (i + 1 == this.size) {
			this.types.add(type);
			this.values.add(value);
		} else {
			this.types.add(i, type);
			this.values.add(i, value);
		}
		this.size++;
		this.current++;
	}

	/*
	Удаляет выделенный символ и помещает курсор на предшествующий ему.
	При удалении посленего символа помещает ноль.
	 */
	public void popSymbol() {
		this.types.remove(current);
		this.values.remove(current);
		this.size--;
		this.current--;
		if (this.size == 0)
			this.pushSymbol(SymbolType.NUMBER, "0");
	}

	public void popSymbol(boolean controlledPop) {
		this.types.remove(current);
		this.values.remove(current);
		this.size--;
		this.current--;
	}

	/*
	Добавляет цифру к концу числа.
	 */
	public void addNumeral(String num) {
		if (getType() != SymbolType.NUMBER) return;
		StringBuilder strB = new StringBuilder();
		strB.append(getVal());
		strB.append(num);
		this.values.set(this.current, strB.toString());
	}

	/*
	Удаляет последнюю цифру из числа.
	Удаляет число вовсе, если
	 */
	public void popNumeral() {
		if (getType() != SymbolType.NUMBER) return;
		String str = (String) getVal();
		if (Double.isInfinite(Double.parseDouble(str)) || Double.isNaN(Double.parseDouble(str)))
			this.popSymbol();
		else if (str.length() != 1)
			this.values.set(this.current, str.substring(0, str.length() - 1));
		else this.popSymbol();
	}

	public void switchNumberSign() {
		if (getType() != SymbolType.NUMBER) return;
		String str = (String) getVal();
		if (str.charAt(0) == '-')
			this.values.set(this.current, str.substring(1));
		else
			this.values.set(this.current, "-" + str);
	}

	public void clear() {
		this.types.clear();
		this.values.clear();
		this.size = 0;
		this.current = -1;
	}

	public SymbolType getType() {
		return this.types.get(current);
	}

	public SymbolType getType(int i) {
		if (i < this.size)
			return this.types.get(i);
		else return null;
	}

	public Object getVal() {
		return this.values.get(current);
	}

	public Object getVal(int i) {
		if (i < this.size) {
			Object object = this.values.get(i);
			if(object.getClass() == String.class) {
				if(object.equals("SIN"))
					return FunctionType.SIN;
				else if(object.equals("COS"))
					return FunctionType.COS;
				else if(object.equals("TAN"))
					return FunctionType.TAN;
				else if(object.equals("SQRT"))
					return FunctionType.SQRT;
				else if(object.equals("LN"))
					return FunctionType.LN;
				else if(object.equals("ABS"))
					return FunctionType.ABS;
				else if(object.equals("FLOOR"))
					return FunctionType.FLOOR;
				else if(object.equals("CEILING"))
					return FunctionType.CEILING;
				else if(object.equals("ADD"))
					return OperationType.ADD;
				else if(object.equals("SUB"))
					return OperationType.SUB;
				else if(object.equals("MUL"))
					return OperationType.MUL;
				else if(object.equals("DIV"))
					return OperationType.DIV;
				else if(object.equals("POW"))
					return OperationType.POW;
				else return object;
			} else
				return object;
		}
		else
			return null;
	}

	public int size() {
		return this.size;
	}

	public int currentPos() {
		return this.current;
	}

	public void moveCurrent (boolean forward) {
		if (forward && this.current + 1 != this.size)
			this.current++;
		if (!forward && this.current != 0)
			this.current--;
	}

	public int countType(SymbolType type) {
		int count = 0;
		for (int i = 0; i < this.size; i++)
			if (this.types.get(i) == type)
				count++;
		return count;
	}

	public SymbolList cutSublist(int start, int end) {
		LinkedList<SymbolType> subT = new LinkedList<>(this.types.subList(start, end + 1));
		LinkedList<Object> subV = new LinkedList<>(this.values.subList(start, end + 1));
		removeSymbols(start, end);
		return new SymbolList(subT, subV);
	}

	public void replaceSublist(int start, int end, SymbolList list) {
		removeSymbols(start, end);
		for (int i = 0, j = start; i < list.size(); i++, j++) {
			this.types.add(j, list.getType(i));
			this.values.add(j, list.getVal(i));
			this.size++;
			this.current++;
		}
	}

	public void removeSymbols(int start, int end) {
		for (int i = 0; i <= end - start; i++) {
			this.types.remove(start);
			this.values.remove(start);
			this.size--;
			this.current--;
		}
	}

	/*
	Возвращает подсписок из списка символов
	@param start индекс левого элемента включительно
	@param end индекс правого элемента исключительно
	@return подсписок символов
	* */
	public SymbolList getSubList(int start, int end) {
		LinkedList<SymbolType> symbolTypeLinkedList = new LinkedList<>(this.types.subList(start, end));
		LinkedList<Object> objectLinkedList = new LinkedList<>(this.values.subList(start, end));
		return new SymbolList(symbolTypeLinkedList, objectLinkedList);
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public Function parse() {

		if (this != null) {
			// Подсчёт скобок и добавление закрывающих при нужде.
			int leftBrCount = countType(LBR);
			int rightBrCount = countType(RBR);
			while (rightBrCount < leftBrCount) {
				pushSymbol(RBR, ")");
				rightBrCount++;
			}
			setActive(false);
			checkParentheses(this);
			substituteUnaryMinus(this);
			Quantity root = doOrderOfOperations(this);
			if (root == null) {
				return null;
			}
			return new Function(root, x);
		}
		return null;
	}

	private Quantity doOrderOfOperations(SymbolList symbolList) {
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
									Object object = symbolList.getVal(location);
									ret = parseFunctionParams(paramString, (SymbolList.FunctionType) object);
								}
							} else if (symbolList.size() >= 2 && symbolList.getType(symbolList.size() - 1) == SymbolList.SymbolType.RBR
									&& symbolList.getType(0) == SymbolList.SymbolType.LBR) {
								SymbolList inParentheses = symbolList.getSubList(1, symbolList.size() - 1);
								ret = doOrderOfOperations(inParentheses);
							} else {
								location = scanFromRight(symbolList, SymbolList.SymbolType.VARIABLE);
								if (location != -1) {
									//    ret = x;
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

	private Quantity parseFunctionParams(SymbolList paramString, SymbolList.FunctionType type) {
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
//                case COTAN:
//                    return new Cotangent(param1);
//                case SQRT:
//                    return new SquareRoot(param1);
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

	private int getFunctionParamsEnd(SymbolList symbolList, int location) {
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

	private int scanFromRight(SymbolList symbolList, SymbolList.OperationType type) {
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
	private int scanFromRight(SymbolList symbolList, SymbolList.SymbolType type) {
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

	private void substituteUnaryMinus(SymbolList symbolList) {
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

	private void checkParentheses(SymbolList symbolList) {
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

}
