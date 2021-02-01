package com.example.android.bluetooth_prototype;

import android.bluetooth.BluetoothDevice;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.example.android.bluetooth_prototype.bluetooth.BluetoothService;
import com.example.android.bluetooth_prototype.db.Temperature;
import com.example.android.bluetooth_prototype.db.TemperatureProvider;
import com.example.android.bluetooth_prototype.util.DateUtil;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.net.URI;
import java.util.Date;

public class BTDeviceDetailActivity extends BluetoothBaseActivity {

    private static final String TAG = "BLUETOOTH_BASE_ACTIVITY";
    private ContentResolver contentResolver;
    private TextView tvCurrentTemp;
    private TextView tvMode;
    private TextView tvRefrigeratorTemp;
    private String btAddress;
    private Button btDataLoad;
    private TextView tvDataList;

    private String modeVal;
    //private View deviceInfoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_device_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        CollapsingToolbarLayout toolBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        //toolBarLayout.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //deviceInfoView = findViewById(R.id.deviceInfoLayout);
        tvCurrentTemp = (TextView)findViewById(R.id.tvCurrentTemp);
        tvMode = (TextView)findViewById(R.id.tvMode);
        tvRefrigeratorTemp = (TextView)findViewById(R.id.tvRefrigeratorTemp);

        contentResolver = getContentResolver();
        btAddress = getSharedPreferences("device", MODE_PRIVATE).getString("deviceAddress", null);
        btDataLoad = (Button)findViewById(R.id.btDataLoad);
        tvDataList = (TextView)findViewById(R.id.tvDataList);
        btDataLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {

        if(message.what == BluetoothService.MSG_READ) {
            byte[] data = (byte[])message.obj;
            Log.e(TAG, ""+data[0]+" "+data[1]+" "+data[2]+ " " + data[3] + " "+ data[4]);
            int currentTempVal = data[1] - 90;
            modeVal = String.valueOf(data[2]);
            String mode = (data[2] == 1 ? "화이자" : data[2] == 2 ? "모더나" : data[2] == 3 ? "아스트라제네카" : data[2] == 4 ? "얀센" : "-");
            int refTempVal = data[3] - 90;

            tvCurrentTemp.setText(String.valueOf(currentTempVal));
            tvMode.setText(mode);
            tvRefrigeratorTemp.setText(String.valueOf(refTempVal));



            final ContentValues values = new ContentValues();
            values.clear();
            values.put(Temperature.TemperatureEntry.COLUMN_BT_DEVICE, btAddress);
            values.put(Temperature.TemperatureEntry.COLUMN_MODE, data[2]);
            values.put(Temperature.TemperatureEntry.COLUMN_CUR_TEMP, currentTempVal);
            values.put(Temperature.TemperatureEntry.COLUMN_REF_TEMP, refTempVal);
            values.put(Temperature.TemperatureEntry.COLUMN_DATE, DateUtil.dateToDateString(new Date()));

            contentResolver.insert(TemperatureProvider.CONTENT_URI, values);
            //changeCurrentTemp(currentTempVal);

//            try {
//                Thread.sleep(3000);
//                Log.e(TAG, "setText: "+ String.valueOf(currentTempVal));
//                currentTemp.setText(String.valueOf(currentTempVal));
//            } catch (Exception e){
//                e.printStackTrace();
//            }
        }
        return false;
    }


    private void loadData() {
        try {
            final StringBuilder sb = new StringBuilder();
            String selection = Temperature.TemperatureEntry.COLUMN_BT_DEVICE +"=?" + " AND " + Temperature.TemperatureEntry.COLUMN_MODE + "=?";
            Cursor cursor = contentResolver.query(TemperatureProvider.CONTENT_URI, null, selection ,  new String[]{btAddress,modeVal}, null);
            int count = 0;
            while (cursor.moveToNext() && count < 100) {
                sb.append("시간:" + cursor.getString(2) + " 현재온도: " + cursor.getDouble(3) + " 설정온도: " + cursor.getDouble(4));
                sb.append('\n');
                Log.e(TAG, "_ID: " + cursor.getInt(0) + "mac: " + cursor.getString(1) + "시간:" + cursor.getString(2) + " 현재온도: " + cursor.getDouble(3) + " 고내온도: " + cursor.getDouble(4) + " 모드: " + cursor.getString(5));

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvDataList.setText(sb.toString());
                }
            });
        } catch(Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

//    public void changeCurrentTemp(final int currentTempVal){
//       this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                Log.e(TAG, "setText: "+ String.valueOf(currentTempVal));
//                currentTemp.setText(String.valueOf(currentTempVal));
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("뒤로가기");
        builder.setMessage("블루투스 연결을 해제하실건가요?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                disconnect();
                BTDeviceDetailActivity.super.onBackPressed();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.home){
            finish();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}