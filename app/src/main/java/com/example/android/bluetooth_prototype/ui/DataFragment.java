package com.example.android.bluetooth_prototype.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.android.bluetooth_prototype.R;
import com.example.android.bluetooth_prototype.adapter.DataListViewAdapter;
import com.example.android.bluetooth_prototype.db.Temperature;
import com.example.android.bluetooth_prototype.db.TemperatureProvider;
import com.example.android.bluetooth_prototype.model.TemperatureData;
import com.example.android.bluetooth_prototype.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DataFragment extends Fragment {
    private static final String TAG = "DATA_FRAGMENT";
    private DataListViewAdapter dataListViewAdapter;
    private ListView lvDevice;
    private String btAddress;
    private List<TemperatureData> dataList = new ArrayList<TemperatureData>();
    private ContentResolver contentResolver;
    private Spinner spinner;
    private String modeVal;
    private Button btSearch;

    public DataFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DataFragment newInstance() {
        DataFragment fragment = new DataFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_data, container, false);
        spinner = (Spinner) view.findViewById(R.id.spinnerType);
        ArrayAdapter typeAdapter = ArrayAdapter.createFromResource(getContext(), R.array.type_item, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeAdapter);

        dataListViewAdapter = new DataListViewAdapter(dataList);
        lvDevice = (ListView) view.findViewById(R.id.lvDevice);
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

        btSearch = (Button) view.findViewById(R.id.btnSearch);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dataList.clear();
                loadData(dataList);
                dataListViewAdapter.notifyDataSetChanged();
            }
        });
        return view;
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
                Log.e(TAG, "_ID: " + cursor.getInt(0) + "mac: " + cursor.getLong(1) + "시간:" + cursor.getString(2) + " 현재온도: " + cursor.getDouble(3) + " 고내온도: " + cursor.getDouble(4) + " 모드: " + cursor.getString(5));

                dataList.add(temperatureData);
            }
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}