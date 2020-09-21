/******************************************************************************
 *  Тип данных для комплексных чисел.
 *
 *  Тип данных является «неизменяемым», поэтому после создания 
 *  и инициализации объекта com.example.com.sct.smartcalc.util.ComplexNumber вы не сможете его изменить.
 *  Ключевое слово "final" при объявлении re и im применяет это правило, 
 *  делая ошибкой во время компиляции изменение переменных экземпляра 
 *  .re или .im после их инициализации.
 *
 ******************************************************************************/

package com.sct.smartcalc.util;

import com.sct.smartcalc.Complex;

public class ComplexNumber {
	private final double re;   // действительная часть
	private final double im;   // мнимая часть

	// создаёт новый объект с заданными действительной и мнимой частями
	public ComplexNumber(double re, double im) {
		this.re = re;
		this.im = im;
	}

	// строковое представление объекта com.example.com.sct.smartcalc.util.ComplexNumber
	public String toString() {
		if (im == 0) return re + "";
		if (re == 0) return im + "i";
		if (im < 0) return re + " - " + (-im) + "i";
		return re + " + " + im + "i";
	}

	// модуль
	public static double abs(ComplexNumber z) {
		double res = Math.hypot(z.re, z.im);
		return res;
	}

	// аргумент
	public static double arg(ComplexNumber z) {
		return Math.atan2(z.im, z.re);
	}

	// сумма
	public static ComplexNumber add(ComplexNumber z, ComplexNumber w) {
		double re = z.re + w.re;
		double im = z.im + w.im;
		return new ComplexNumber(re, im);
	}

	// разность
	public static ComplexNumber subtract(ComplexNumber z, ComplexNumber w) {
		double re = z.re - w.re;
		double im = z.im - w.im;
		return new ComplexNumber(re, im);
	}

	// умножение
	public static ComplexNumber multiply(ComplexNumber z, ComplexNumber w) {
		double re = z.re * w.re - z.im * w.im;
		double im = z.re * w.im + z.im * w.re;
		return new ComplexNumber(re, im);
	}

	// деление z на w
	public static ComplexNumber divide(ComplexNumber z, ComplexNumber w) {
		return multiply(z, reciprocal(w));
	}

	// сопряжение
	public static ComplexNumber conjugate(ComplexNumber z) {
		return new ComplexNumber(z.re, -z.im);
	}

	// обратная величина
	public static ComplexNumber reciprocal(ComplexNumber z) {
		double scale = z.re * z.re + z.im * z.im;
		return new ComplexNumber(z.re / scale, -z.im / scale);
	}

	// действительная часть
	public double re() {
		return re;
	}

	// мнимая часть
	public double im() {
		return im;
	}

	// комплексная экспонентая
	public static ComplexNumber exp(ComplexNumber z) {
		ComplexNumber res = new ComplexNumber(Math.exp(z.re) * Math.cos(z.im), Math.exp(z.re) * Math.sin(z.im));
		return res;
	}

	// синус
	public static ComplexNumber sin(ComplexNumber z) {
		return new ComplexNumber(Math.sin(z.re) * Math.cosh(z.im), Math.cos(z.re) * Math.sinh(z.im));
	}

	// косинус
	public static ComplexNumber cos(ComplexNumber z) {
		return new ComplexNumber(Math.cos(z.re) * Math.cosh(z.im), -Math.sin(z.re) * Math.sinh(z.im));
	}

	// тангенс
	public static ComplexNumber tan(ComplexNumber z) {
		return divide(sin(z), cos(z));
	}

	// логарифм
	public static ComplexNumber log(ComplexNumber z, int k) {
		ComplexNumber res = new ComplexNumber(Math.log(abs(z)), arg(z) + 2 * Math.PI * k);
		return res;
	}

	// возведение z в комплексную степень w
	public static ComplexNumber pow(ComplexNumber z, ComplexNumber w, int k) {
		ComplexNumber res = exp(multiply(w, log(z, k)));
		return res;
	}

	// проверка на равенство
	public boolean equals(Object x) {
		if (x == null) return false;
		if (this.getClass() != x.getClass()) return false;
		ComplexNumber that = (ComplexNumber) x;
		return (this.re == that.re) && (this.im == that.im);
	}

	// TODO sqrt

}
