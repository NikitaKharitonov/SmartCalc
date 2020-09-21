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
import com.sct.smartcalc.Derivative;
import com.sct.smartcalc.graphs.expression.Function;

import ru.noties.jlatexmath.JLatexMathView;

import static com.sct.smartcalc.util.SymbolList.OperationType.*;
import static com.sct.smartcalc.util.SymbolList.FunctionType.*;
import static com.sct.smartcalc.util.SymbolList.SymbolType.*;

public class DerivativeActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	JLatexMathView mathView;
	Button btnSDR;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_derivative);
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

		Derivative.setup();
		mathView.setLatex("(" + Derivative.getFormula() + ")\'(" + Derivative.getPoint() + ")" + result);
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, CalculatorActivity.class));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		int id = item.getItemId();
		return super.onOptionsItemSelected(item);

	}

	@SuppressWarnings("StatementWithEmptyBody")
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

	boolean functionIsInput = false;

	String result = "";

	public void buttonClick(View view) {
		if(functionIsInput) {
			switch (view.getId()) {
				case R.id.btn_0:
				case R.id.btn_1:
				case R.id.btn_2:
				case R.id.btn_3:
				case R.id.btn_4:
				case R.id.btn_5:
				case R.id.btn_6:
				case R.id.btn_7:
				case R.id.btn_8:
				case R.id.btn_9:
				case R.id.btnComma:
				    Derivative.pushToPoint(NUMBER, ((Button) view).getText().toString());
				case R.id.btnResult:
					result = " = " + String.valueOf(Derivative.prodifferentsirovat());
					break;
				case R.id.btnDelete:
					Derivative.popActionPoint();
					break;
                case R.id.btn_left:
                    if(Derivative.isCurrentBegin()) {
						functionIsInput = false;
						Derivative.setFormulaActive(true);
						Derivative.setPointActive(false);
					}
			}
		}
		else switch (view.getId()) {
			case R.id.btn_left:
				Derivative.moveCursor(false);
				break;
			case R.id.btn_right:
				if(Derivative.isCurrentEnd()) {
					functionIsInput = true;
					Derivative.setFormulaActive(false);
					Derivative.setPointActive(true);
				}
				else
					Derivative.moveCursor(true);
				break;
			case R.id.btnPNS:
				Derivative.switchNumberSign();
				break;
			case R.id.btnSDR:
				if (btnSDR.getText().equals("DEG")) btnSDR.setText("RAD");
				else btnSDR.setText("DEG");
				Derivative.switchDegreesOrRadian();
				break;
			case R.id.btnX:
				Derivative.pushAction(VARIABLE, ((Button) view).getText().toString());
				break;
			case R.id.btnDraw:
				Derivative.pushAction(DERIVATIVE, ((Button) view).getText().toString());
				break;
			//Обработка кнопок операторов.
			case R.id.btnAdd:
				Derivative.pushAction(OPERATION, ADD);
				break;
			case R.id.btnDivide:
				Derivative.pushAction(OPERATION, SUB);
				break;
			case R.id.btnSubtract:
				Derivative.pushAction(OPERATION, DIV);
				break;
			case R.id.btnMultiply:
				Derivative.pushAction(OPERATION, MUL);
				break;
			case R.id.btnPow:
				Derivative.pushAction(OPERATION, POW);
				break;
			case R.id.btnSin:
				Derivative.pushAction(FUNCTION, SIN);
				break;
			case R.id.btnCos:
				Derivative.pushAction(FUNCTION, COS);
				break;
			case R.id.btnTan:
				Derivative.pushAction(FUNCTION, TAN);
				break;
			case R.id.btnRoot:
				Derivative.pushAction(FUNCTION, SQRT);
				break;
			case R.id.btn_left_bracket:
				Derivative.pushAction(LBR, null);
				break;
			//Обработка закрывающей скобки
			case R.id.btn_right_bracket:
				Derivative.pushAction(RBR, null);
				break;
			//Обработка кнопки очищения введённой формулы.
			case R.id.btnClear:
				Derivative.clear();
				break;
			//Обработка знака равно.
//			case R.id.btnResult:
//				Derivative.calculate();
//				break;
			//Обработка знака запятой.
			case R.id.btnComma:
				Derivative.pushAction(COMMA, ((Button) view).getText().toString());
				break;
			//Обработка удаления последнего символа.
			case R.id.btnDelete:
				Derivative.popAction();
				break;
			//Обработка цифр.
			case R.id.btnResult:
				Derivative.prodifferentsirovat();
				Derivative.setPointActive(true);
			default:
				Derivative.pushAction(NUMBER, ((Button) view).getText().toString());
		}
		mathView.setLatex("(" + Derivative.getFormula() + ")\'(" + Derivative.getPoint() + ")" + result);
	}
}
