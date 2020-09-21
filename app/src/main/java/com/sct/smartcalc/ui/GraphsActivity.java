package com.sct.smartcalc.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import com.example.smartcalc.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sct.smartcalc.Graphs;
import com.sct.smartcalc.graphs.expression.Function;
import com.sct.smartcalc.util.FormulaAdapter;
import com.sct.smartcalc.util.FormulaList;
import com.sct.smartcalc.util.SymbolList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

import ru.noties.jlatexmath.JLatexMathView;

import static com.sct.smartcalc.util.SymbolList.SymbolType.*;
import static com.sct.smartcalc.util.SymbolList.FunctionType.*;
import static com.sct.smartcalc.util.SymbolList.OperationType.*;


public class GraphsActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	JLatexMathView mathView;
	Button btnSDR;
	ArrayList<Function> functionArrayList = new ArrayList<>();
	ArrayList<String> graphsLables = new ArrayList<>();
	ArrayList<SymbolList> symbolListArrayList = new ArrayList<>();
	FormulaList formulaList = new FormulaList();
//	ArrayList<Boolean> selectedFormulasInHistory = new ArrayList<>();
	LineChart lineChart;
	TextView textEdit;
	String string = "";
	RecyclerView recyclerView;
	FormulaAdapter formulaAdapter;
	RecyclerView.LayoutManager recyclerViewLayoutManager;

	float bottomBorder = -500f;
	float topBorder = 500f;
	float leftBorder = -500f;
	float rightBorder = 500f;

	float distanceBetweenPoints = 0.05f;
	Bundle bundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		bundle = savedInstanceState;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graphs);
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

        textEdit = new TextView(this);
        lineChart = new LineChart(this);
        recyclerView = new RecyclerView(this);

		loadData();

	//	importSelectedFormulasFromHistory();

        Graphs.setup();
		showLatex();
	}

	void importSelectedFormulasFromHistory() {
		for(int i = 0; i < formulaList.getSize(); ++i) {
			SymbolList symbolList = formulaList.getFormula(i);
			if(formulaList.isSelected(i))
				symbolListArrayList.add(symbolList);
		}
	}

	void DrawGraphs() {

		ArrayList<ILineDataSet> dataSets = new ArrayList<>();

		ArrayList<Entry> fourPointsList = new ArrayList<>();
		fourPointsList.add(new Entry(leftBorder, bottomBorder));
		fourPointsList.add(new Entry(leftBorder, topBorder));
		fourPointsList.add(new Entry(rightBorder, bottomBorder));
		fourPointsList.add(new Entry(rightBorder, topBorder));
		LineDataSet fourpoints = new LineDataSet(fourPointsList, "");
		fourpoints.setVisible(false);
		dataSets.add(fourpoints);

//		for(int i = 0; i < symbolListArrayList.size(); ++i) {
//			functionArrayList.add(symbolListArrayList.get(i).parse());
//		}
		for(int i = 0; i < formulaList.getSize(); ++i) {
			if(formulaList.isSelected(i)) {
				SymbolList symbolList = formulaList.getFormula(i);
				functionArrayList.add(symbolList.parse());
			}
		}

		Random r = new Random();
		for(int i = 0; i < functionArrayList.size(); ++i) {
			ArrayList<Entry> entries = new ArrayList<>();
			setPoints(entries, functionArrayList.get(i));
			LineDataSet lineDataSet = new LineDataSet(entries, "");
			lineDataSet.setDrawValues(false);
			lineDataSet.setColor(Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
			lineDataSet.setDrawCircles(false);
			dataSets.add(lineDataSet);
		}

		LineData lineData = new LineData(dataSets);
		lineData.setValueTextColor(Color.BLACK);

		lineChart.setData(lineData);
		lineChart.setDrawGridBackground(false);
		lineChart.getDescription().setEnabled(true);
		lineChart.setExtraBottomOffset(50f);
		lineChart.setPinchZoom(true);
        lineChart.zoom(100, 100, 0, 0, YAxis.AxisDependency.LEFT);
        lineChart.setVisibleXRangeMaximum(1000);
        lineChart.setVisibleYRangeMaximum(1000, YAxis.AxisDependency.LEFT);
        lineChart.setVisibleXRangeMinimum(1);
        lineChart.setVisibleYRangeMinimum(1, YAxis.AxisDependency.LEFT);

		Legend legend = lineChart.getLegend();
		legend.setForm(Legend.LegendForm.LINE);
		legend.setTextColor(Color.BLACK);

		XAxis xaxis = lineChart.getXAxis();
		xaxis.setDrawGridLines(true);
		xaxis.setTextColor(Color.BLACK);
		xaxis.setTextSize(15f);
		xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		YAxis yaxis = lineChart.getAxisLeft();
		yaxis.setTextColor(Color.BLACK);
		yaxis.setTextSize(15f);

		yaxis = lineChart.getAxisRight();
		yaxis.setTextColor(Color.BLACK);
		yaxis.setTextSize(15f);

		setContentView(lineChart);
	}

	private void setPoints(ArrayList<Entry> entries, Function f) {
		for(float x = leftBorder; x <= rightBorder; x += distanceBetweenPoints) {
			double doubleX = f.evaluateAt(x);
			float y = Float.parseFloat(String.valueOf(doubleX));
			if(y <= topBorder && y >= bottomBorder)
				entries.add(new Entry(x, y));
		}
	}

//	void showFunctionsList() {
//		String string = "";
//	    for(int i = 0; i < graphsLables.size(); ++i) {
//	    	string += graphsLables.get(i) + "\n";
//        }
//	    textEdit.setText(string);
//	    setContentView(textEdit);
//    }

	public void buttonClick(View view) {
		switch (view.getId()) {
			case R.id.btn_left:
				Graphs.moveCursor(false);
				break;
			case R.id.btn_right:
				Graphs.moveCursor(true);
				break;
			case R.id.btnPNS:
				Graphs.switchNumberSign();
				break;
			case R.id.btnSDR:
				if (btnSDR.getText().equals("DEG")) btnSDR.setText("RAD");
				else btnSDR.setText("DEG");
				Graphs.switchDegreesOrRadian();
				break;
			case R.id.btnX:
				Graphs.pushAction(VARIABLE, ((Button) view).getText().toString());
				break;
			case R.id.btnDraw:
                functionArrayList.add(Graphs.getFunction());
                symbolListArrayList.add(Graphs.getSymbolListFormula());
				for(int i = 0; i < symbolListArrayList.size(); ++i) {
					formulaList.setFormula(symbolListArrayList.get(i));
				}
                saveData();
				DrawGraphs();
				break;
			//Обработка кнопок операторов.
			case R.id.btnAdd:
				Graphs.pushAction(OPERATION, ADD);
				break;
			case R.id.btnDivide:
				Graphs.pushAction(OPERATION, SUB);
				break;
			case R.id.btnSubtract:
				Graphs.pushAction(OPERATION, DIV);
				break;
			case R.id.btnMultiply:
				Graphs.pushAction(OPERATION, MUL);
				break;
			case R.id.btnPow:
				Graphs.pushAction(OPERATION, POW);
				break;
			case R.id.btnSin:
				Graphs.pushAction(FUNCTION, SIN);
				break;
			case R.id.btnCos:
				Graphs.pushAction(FUNCTION, COS);
				break;
			case R.id.btnTan:
				Graphs.pushAction(FUNCTION, TAN);
				break;
			case R.id.btnRoot:
				Graphs.pushAction(FUNCTION, SQRT);
				break;
			case R.id.btnCeiling:
				Graphs.pushAction(FUNCTION, CEILING);
				break;
			case R.id.btnFloor:
				Graphs.pushAction(FUNCTION, FLOOR);
				break;
			case R.id.btnAbs:
				Graphs.pushAction(FUNCTION, ABS);
				break;
			case R.id.btnLn:
				Graphs.pushAction(FUNCTION, LN);
				break;
			case R.id.btn_left_bracket:
				Graphs.pushAction(LBR, null);
				break;
			//Обработка закрывающей скобки
			case R.id.btn_right_bracket:
				Graphs.pushAction(RBR, null);
				break;
			//Обработка кнопки очищения введённой формулы.
			case R.id.btnClear:
				Graphs.clear();
				functionArrayList.clear();
				graphsLables.clear();
				string = "";
				break;
			//Обработка знака равно.
			case R.id.btnAddFunction:
				functionArrayList.add(Graphs.getFunction());
				symbolListArrayList.add(Graphs.getSymbolListFormula());
				Graphs.clear();
				break;
            case R.id.btnHistory:
				setContentView(R.layout.activity_graphs_history);
				recyclerView = findViewById(R.id.recyclerView);
				recyclerView.setHasFixedSize(true);
				recyclerViewLayoutManager = new LinearLayoutManager(this);
				formulaAdapter = new FormulaAdapter(formulaList);
				recyclerView.setLayoutManager(recyclerViewLayoutManager);
				recyclerView.setAdapter(formulaAdapter);
				formulaAdapter.setOnItemClickListener(new FormulaAdapter.OnItemClickListener() {
					@Override
					public void onItemClick(int position) {
						if(formulaList.isSelected(position))
							formulaList.setSelected(position, false);
						else
							formulaList.setSelected(position, true);
						formulaAdapter.notifyItemChanged(position);
						clearData();
						saveData();
					}
				});
				break;
			//Обработка знака запятой.
			case R.id.btnComma:
				Graphs.pushAction(COMMA, ((Button) view).getText().toString());
				break;
			//Обработка удаления последнего символа.
			case R.id.btnDelete:
				Graphs.popAction();
				break;
			case R.id.clearHistory:
				formulaList.clear();
				formulaAdapter.notifyDataSetChanged();
				clearData();
			//Обработка цифр.
			default:
				Graphs.pushAction(NUMBER, ((Button) view).getText().toString());
		}
		showLatex();
	}

	void showLatex() {
		String string = "";
		for(int i = 0; i < symbolListArrayList.size(); ++i) {
			string += "f(x) = " + symbolListArrayList.get(i).toLaTeX() + "\\\\";
		}
		string += "f(x) = " + Graphs.getFormula();
		mathView.setLatex(string);
	}

	private void clearData() {
		SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		//editor.clear();
		editor.remove("task list");
		editor.apply();
	}

	private void saveData() {
		SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		Gson gson = new Gson();
		String json = gson.toJson(formulaList);
		editor.putString("task list", json);
		editor.apply();
	}

	private void loadData() {
		SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
		Gson gson = new Gson();
		String json = sharedPreferences.getString("task list", null);
		Type type = new TypeToken<FormulaList>(){}.getType();
		try {
			formulaList = gson.fromJson(json, type);
			if (formulaList == null) {
				formulaList = new FormulaList();
			}
		} catch (RuntimeException e) {
			string += e.toString();
		}
	}

	@Override
	public void onBackPressed() {
		if(lineChart.isShown() || recyclerView.isShown())
			startActivity(new Intent(this, GraphsActivity.class));
		else
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


}
