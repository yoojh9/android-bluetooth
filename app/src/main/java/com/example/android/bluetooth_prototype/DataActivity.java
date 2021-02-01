package com.example.android.bluetooth_prototype;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.android.bluetooth_prototype.adapter.DataListViewAdapter;
import com.example.android.bluetooth_prototype.db.Temperature;
import com.example.android.bluetooth_prototype.db.TemperatureProvider;
import com.example.android.bluetooth_prototype.model.TemperatureData;
import com.example.android.bluetooth_prototype.ui.BottomNavigationViewHolder;
import com.example.android.bluetooth_prototype.util.DateUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataActivity extends BluetoothBaseActivity {

    private static final String TAG = "DATA_ACTIVITY";
    private final int ACTIVITY_NUM = 1;
    private DataListViewAdapter dataListViewAdapter;
    private ListView lvDevice;
    private Button btSearch;
    private List<TemperatureData> dataList = new ArrayList<TemperatureData>();
    private ContentResolver contentResolver;
    private String btAddress;
    private Spinner spinner;
    private String modeVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        btAddress = getSharedPreferences("device", MODE_PRIVATE).getString("deviceAddress", null);

        contentResolver = getContentResolver();

        spinner = (Spinner) findViewById(R.id.spinnerType);
        ArrayAdapter typeAdapter = ArrayAdapter.createFromResource(this, R.array.type_item, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeAdapter);

        dataListViewAdapter = new DataListViewAdapter(this, dataList);
        lvDevice = (ListView) findViewById(R.id.lvDevice);
        lvDevice.setAdapter(dataListViewAdapter);


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

        btSearch = (Button) findViewById(R.id.btnSearch);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataList.clear();
                loadData(dataList);
                dataListViewAdapter.notifyDataSetChanged();
            }
        });

        setupBottomNavigationView();
    }

    private void loadData(List<TemperatureData> dataList) {
        try {

            final StringBuilder sb = new StringBuilder();
            String selection = Temperature.TemperatureEntry.COLUMN_BT_DEVICE +"=?" + " AND " + Temperature.TemperatureEntry.COLUMN_MODE + "=?";
            Cursor cursor = contentResolver.query(TemperatureProvider.CONTENT_URI, null, selection ,  new String[]{btAddress,modeVal}, null);
            int count = 0;
            while (cursor.moveToNext() && count < 100) {
                TemperatureData temperatureData = new TemperatureData();
                temperatureData.setDate(cursor.getString(2));
                temperatureData.setRefTemp(cursor.getDouble(3));
                temperatureData.setCurrentTemp(cursor.getDouble(4));
                temperatureData.setMode(cursor.getString(5));
                Log.e(TAG, "_ID: " + cursor.getInt(0) + "mac: " + cursor.getString(1) + "시간:" + cursor.getString(2) + " 현재온도: " + cursor.getDouble(3) + " 고내온도: " + cursor.getDouble(4) + " 모드: " + cursor.getString(5));

                dataList.add(temperatureData);
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        BottomNavigationViewHolder.enableNavigation(DataActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

}