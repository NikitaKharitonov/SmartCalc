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
import android.widget.TextView;

import com.example.smartcalc.R;
import com.sct.smartcalc.Complex;

import ru.noties.jlatexmath.JLatexMathView;

import static com.sct.smartcalc.util.SymbolList.FunctionType.ABS;
import static com.sct.smartcalc.util.SymbolList.FunctionType.ARG;
import static com.sct.smartcalc.util.SymbolList.FunctionType.CEILING;
import static com.sct.smartcalc.util.SymbolList.FunctionType.CONJ;
import static com.sct.smartcalc.util.SymbolList.FunctionType.COS;
import static com.sct.smartcalc.util.SymbolList.FunctionType.FLOOR;
import static com.sct.smartcalc.util.SymbolList.FunctionType.SIN;
import static com.sct.smartcalc.util.SymbolList.FunctionType.SQRT;
import static com.sct.smartcalc.util.SymbolList.FunctionType.TAN;
import static com.sct.smartcalc.util.SymbolList.OperationType.ADD;
import static com.sct.smartcalc.util.SymbolList.OperationType.DIV;
import static com.sct.smartcalc.util.SymbolList.OperationType.MUL;
import static com.sct.smartcalc.util.SymbolList.OperationType.POW;
import static com.sct.smartcalc.util.SymbolList.OperationType.SUB;
import static com.sct.smartcalc.util.SymbolList.SymbolType.COMMA;
import static com.sct.smartcalc.util.SymbolList.SymbolType.FUNCTION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.LBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.NUMBER;
import static com.sct.smartcalc.util.SymbolList.SymbolType.OPERATION;
import static com.sct.smartcalc.util.SymbolList.SymbolType.RBR;
import static com.sct.smartcalc.util.SymbolList.SymbolType.VARIABLE;

public class ComplexActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    JLatexMathView mathView;
    Button btnSDR;
    TextView textView;
    double result;
    String resultString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complex);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mathView = findViewById(R.id.formula);
        btnSDR = findViewById(R.id.btnSDR);
        btnSDR.setText("RAD");

        textView = new TextView(this);

        Complex.setup();
        mathView.setLatex(Complex.getFormula());
    }

    int step = 0;
    public void buttonClick(View view) {
        resultString = "";
        switch (view.getId()) {
            case R.id.btn_left:
                Complex.moveCursor(false);
                break;
            case R.id.btn_right:
                Complex.moveCursor(true);
                break;
            case R.id.btnPNS:
                Complex.switchNumberSign();
                break;
            case R.id.btnSDR:
                if (btnSDR.getText().equals("DEG"))
                    btnSDR.setText("RAD");
                else
                    btnSDR.setText("DEG");
                Complex.switchDegreesOrRadian();
                break;
            case R.id.btnX:
                break;
            case R.id.btnI:
                Complex.pushAction(NUMBER, ((Button) view).getText().toString());
                break;
            //Обработка кнопок операторов.
            case R.id.btnAdd:
                Complex.pushAction(OPERATION, ADD);
                break;
            case R.id.btnDivide:
                Complex.pushAction(OPERATION, SUB);
                break;
            case R.id.btnSubtract:
                Complex.pushAction(OPERATION, DIV);
                break;
            case R.id.btnMultiply:
                Complex.pushAction(OPERATION, MUL);
                break;
            case R.id.btnPow:
                Complex.pushAction(OPERATION, POW);
                break;
            case R.id.btnSin:
                Complex.pushAction(FUNCTION, SIN);
                break;
            case R.id.btnCos:
                Complex.pushAction(FUNCTION, COS);
                break;
            case R.id.btnTan:
                Complex.pushAction(FUNCTION, TAN);
                break;
            case R.id.btnRoot:
                Complex.pushAction(FUNCTION, SQRT);
                break;
            case R.id.btnCeiling:
                Complex.pushAction(FUNCTION, CEILING);
                break;
            case R.id.btnFloor:
                Complex.pushAction(FUNCTION, FLOOR);
                break;
            case R.id.btnAbs:
                Complex.pushAction(FUNCTION, ABS);
                break;
            case R.id.btnLn:
                //	Graphs.pushAction(FUNCTION, LOG); TODO
                break;
            case R.id.btnConj:
                Complex.pushAction(FUNCTION, CONJ);
                break;
            case R.id.btnArg:
                Complex.pushAction(FUNCTION, ARG);
                break;
            case R.id.btn_left_bracket:
                Complex.pushAction(LBR, null);
                break;
            //Обработка закрывающей скобки
            case R.id.btn_right_bracket:
                Complex.pushAction(RBR, null);
                break;
            //Обработка кнопки очищения введённой формулы.
            case R.id.btnClear:
                Complex.clear();
                break;
            //Обработка знака запятой.
            case R.id.btnComma:
                Complex.pushAction(COMMA, ((Button) view).getText().toString());
                break;
            //Обработка удаления последнего символа.
            case R.id.btnDelete:
                Complex.popAction();
                break;
            case R.id.btnResult:
                resultString = " = " + Complex.calculate();
                break;
            //Обработка цифр.
            default:
                Complex.pushAction(NUMBER, ((Button) view).getText().toString());
        }
        mathView.setLatex(Complex.getFormula() + resultString);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, CalculatorActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.graphs, menu);
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


}
