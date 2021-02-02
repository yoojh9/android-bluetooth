package com.example.android.bluetooth_prototype.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.android.bluetooth_prototype.R;
import com.example.android.bluetooth_prototype.db.Temperature;
import com.example.android.bluetooth_prototype.db.TemperatureProvider;
import com.example.android.bluetooth_prototype.model.TemperatureData;
import com.example.android.bluetooth_prototype.util.DateUtil;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment {
    private final String TAG = "CHART-FRAGMENT";
    private Spinner spinner;
    private Button btSearch;
    private LineChart lineChart;
    private String btAddress;
    private String modeVal;
    private List<TemperatureData> dataList = new ArrayList<TemperatureData>();
    private ContentResolver contentResolver;
    private View view;
    private ArrayList<String> xAxis = new ArrayList<>();

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance() {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btAddress = getActivity().getSharedPreferences("device", Context.MODE_PRIVATE).getString("deviceAddress", null);
        contentResolver = getActivity().getContentResolver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chart, parent, false);
        lineChart = view.findViewById(R.id.lineChart);
        spinner = (Spinner) view.findViewById(R.id.spinnerType);
        ArrayAdapter typeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.type_item, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int position = ++i;
                modeVal = String.valueOf(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btSearch = (Button) view.findViewById(R.id.btnSearch);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dataList.clear();
                        loadData(dataList);
                        setChart(dataList);

                    }
                });
            }
        });
        return view;
    }

    private void setChart(List <TemperatureData> list) {
        lineChart.clear();
        lineChart.invalidate(); //차트 초기화 작업

        Collections.reverse(list);

        ArrayList<Entry> values = new ArrayList<>();//차트 데이터 셋에 담겨질 데이터

        for (int i=0; i < list.size(); i++) { //values에 데이터를 담는 과정
            //String dateTime = data.getDate();
            float temperature = (float) list.get(i).getCurrentTemp();
            values.add(new Entry(i, temperature));
        }

        /*몸무게*/
        LineDataSet lineDataSet = new LineDataSet(values, "온도"); //LineDataSet 선언
        lineDataSet.setColor(Color.GREEN); //LineChart에서 Line Color 설정
        lineDataSet.setCircleColor(Color.GREEN); // LineChart에서 Line Circle Color 설정
        lineDataSet.setCircleHoleColor(Color.GREEN); // LineChart에서 Line Hole Circle Color 설정

        LineData lineData = new LineData(); //LineDataSet을 담는 그릇 여러개의 라인 데이터가 들어갈 수 있습니다.
        lineData.addDataSet(lineDataSet);

        //lineData.setValueTextColor(Color.BLUE); //라인 데이터의 텍스트 컬러 설정
        //lineData.setValueTextSize(9);

        XAxis xAxis = lineChart.getXAxis(); // x 축 설정
        xAxis.setPosition(XAxis.XAxisPosition.TOP); //x 축 표시에 대한 위치 설정
        //xAxis.setValueFormatter(new ChartXValueFormatter()); //X축의 데이터를 제 가공함. new ChartXValueFormatter은 Custom한 소스
        xAxis.setLabelCount(5, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌
        xAxis.setTextColor(Color.BLACK); // X축 텍스트컬러설정
        xAxis.setGridColor(Color.BLACK); // X축 줄의 컬러 설정
        xAxis.setValueFormatter(new GraphXAxisValueFormatter());

        YAxis yAxisLeft = lineChart.getAxisLeft(); //Y축의 왼쪽면 설정
        yAxisLeft.setTextColor(Color.BLACK); //Y축 텍스트 컬러 설정
        yAxisLeft.setGridColor(Color.BLACK); // Y축 줄의 컬러 설정

        YAxis yAxisRight = lineChart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        //y축의 활성화를 제거함

        lineChart.moveViewToX(values.size());
        lineChart.setVisibleXRangeMinimum(60 * 60 * 24 * 1000 * 5); //라인차트에서 최대로 보여질 X축의 데이터 설정
        lineChart.setDescription(null); //차트에서 Description 설정 저는 따로 안했습니다.

        Legend legend = lineChart.getLegend(); //레전드 설정 (차트 밑에 색과 라벨을 나타내는 설정)
        legend.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);//하단 왼쪽에 설정
        legend.setTextColor(Color.BLACK); // 레전드 컬러 설정

        lineChart.setData(lineData);
    }

    private void loadData(List<TemperatureData> dataList) {
        try {

            final StringBuilder sb = new StringBuilder();
            String selection = Temperature.TemperatureEntry.COLUMN_BT_DEVICE +"=?" + " AND " + Temperature.TemperatureEntry.COLUMN_MODE + "=?";
            Cursor cursor = contentResolver.query(TemperatureProvider.CONTENT_URI, null, selection ,  new String[]{btAddress,modeVal}, null);
            int count = 0;
            while (cursor.moveToNext() && count < 100) {
                TemperatureData temperatureData = new TemperatureData();
                temperatureData.setDateTime(cursor.getString(2));
                temperatureData.setCurrentTemp(cursor.getDouble(3));
                temperatureData.setRefTemp(cursor.getDouble(4));
                temperatureData.setMode(cursor.getString(5));
                Log.e(TAG, "_ID: " + cursor.getInt(0) + "mac: " + cursor.getString(1) + "시간:" + cursor.getString(2) + " 현재온도: " + cursor.getDouble(3) + " 고내온도: " + cursor.getDouble(4) + " 모드: " + cursor.getString(5));

                dataList.add(temperatureData);
                xAxis.add(cursor.getString(2).substring(10));
                //addEntry(cursor.getString(2), cursor.getFloat(3));
            }
            //drawChart();
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public class GraphXAxisValueFormatter extends IndexAxisValueFormatter {

        public GraphXAxisValueFormatter(){
            super(xAxis);
        }

    }
}