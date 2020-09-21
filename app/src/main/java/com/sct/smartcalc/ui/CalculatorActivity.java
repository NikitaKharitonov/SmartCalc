package com.sct.smartcalc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.smartcalc.R;
import com.sct.smartcalc.Calculator;

import ru.noties.jlatexmath.JLatexMathView;

import static com.sct.smartcalc.util.SymbolList.OperationType.*;
import static com.sct.smartcalc.util.SymbolList.SymbolType.*;
import static com.sct.smartcalc.util.SymbolList.FunctionType.*;

public class CalculatorActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	JLatexMathView mathView;
	Button btnSDR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		mathView = findViewById(R.id.formula);
		btnSDR = findViewById(R.id.btnSDR);
		btnSDR.setText("RAD");

		Calculator.setup();
		mathView.setLatex(Calculator.getFormula());
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_focus_derivative) {
			Intent intent = new Intent(this, DerivativeActivity.class);
			startActivity(intent);
		} else if (id == R.id.nav_focus_integral) {
			Intent intent = new Intent(this, IntegralActivity.class);
			startActivity(intent);
		} else if (id == R.id.nav_focus_graphs) {
			Intent intent = new Intent(this, GraphsActivity.class);
			startActivity(intent);
		} else if (id == R.id.nav_focus_complex) {
			Intent intent = new Intent(this, ComplexActivity.class);
			startActivity(intent);
		} else if (id == R.id.nav_focus_calculator) {
			Intent intent = new Intent(this, CalculatorActivity.class);
			startActivity(intent);
		}

		DrawerLayout drawer = findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

    /*
    private void showToastMessage(int messageId) {
        Toast toastMessage = Toast.makeText(this, messageId, Toast.LENGTH_LONG);
        toastMessage.setGravity(Gravity.TOP, 0, 100);
        toastMessage.show();
    }
     */

	/*
	Данный метод отвечает за две вещи: вывод текста и создание списка для введённой формулы.
	Этот список хранится и обрабатывается в com.example.com.sct.smartcalc.Calculator.
	 */
	public void buttonClick(View view) {
		switch (view.getId()) {
			case R.id.btn_left:
				Calculator.moveCursor(false);
				break;
			case R.id.btn_right:
				Calculator.moveCursor(true);
				break;
			case R.id.btnPNS:
				Calculator.switchNumberSign();
				break;
			case R.id.btnSDR:
				if (btnSDR.getText().equals("DEG")) btnSDR.setText("RAD");
				else btnSDR.setText("DEG");
				Calculator.switchDegreesOrRadian();
				break;
			//Обработка кнопок операторов.
			case R.id.btnAdd:
				Calculator.pushAction(OPERATION, ADD);
				break;
			case R.id.btnDivide:
				Calculator.pushAction(OPERATION, DIV);
				break;
			case R.id.btnSubtract:
				Calculator.pushAction(OPERATION, SUB);
				break;
			case R.id.btnMultiply:
				Calculator.pushAction(OPERATION, MUL);
				break;
			case R.id.btnPow:
				Calculator.pushAction(OPERATION, POW);
				break;
			case R.id.btnSin:
				Calculator.pushAction(FUNCTION, SIN);
				break;
			case R.id.btnCos:
				Calculator.pushAction(FUNCTION, COS);
				break;
			case R.id.btnTan:
				Calculator.pushAction(FUNCTION, TAN);
				break;
			case R.id.btnRoot:
				Calculator.pushAction(FUNCTION, SQRT);
				break;
			case R.id.btn_left_bracket:
				Calculator.pushAction(LBR, null);
				break;
			//Обработка закрывающей скобки
			case R.id.btn_right_bracket:
				Calculator.pushAction(RBR, null);
				break;
			//Обработка кнопки очищения введённой формулы.
			case R.id.btnClear:
				Calculator.clear();
				break;
			//Обработка знака равно.
			case R.id.btnResult:
				Calculator.calculate();
				break;
			//Обработка знака запятой.
			case R.id.btnComma:
				Calculator.pushAction(COMMA, ((Button) view).getText().toString());
				break;
			//Обработка удаления последнего символа.
			case R.id.btnDelete:
				Calculator.popAction();
				break;
			//Обработка цифр.
			default:
				Calculator.pushAction(NUMBER, ((Button) view).getText().toString());
		}
		mathView.setLatex(Calculator.getFormula());
	}
}
