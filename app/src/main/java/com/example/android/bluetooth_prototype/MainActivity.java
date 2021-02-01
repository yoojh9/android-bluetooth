package com.example.android.bluetooth_prototype;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.bluetooth_prototype.bluetooth.BluetoothService;
import com.example.android.bluetooth_prototype.db.Temperature;
import com.example.android.bluetooth_prototype.db.TemperatureProvider;
import com.example.android.bluetooth_prototype.ui.BottomNavigationViewHolder;
import com.example.android.bluetooth_prototype.util.DateUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Date;

public class MainActivity extends BluetoothBaseActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private ContentResolver contentResolver;
    private String btAddress;
    private TextView tvCurrentTemp;
    private TextView tvMode;
    private TextView tvRefrigeratorTemp;
    private final int ACTIVITY_NUM = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tvCurrentTemp = (TextView)findViewById(R.id.tvCurrentTemp);
        tvMode = (TextView)findViewById(R.id.tvMode);
        tvRefrigeratorTemp = (TextView)findViewById(R.id.tvRefrigeratorTemp);
        contentResolver = getContentResolver();
        btAddress = getSharedPreferences("device", MODE_PRIVATE).getString("deviceAddress", null);

        setupBottomNavigationView();

    }

    @Override
    public boolean handleMessage(@NonNull Message message) {

        if (message.what == BluetoothService.MSG_READ) {
            byte[] data = (byte[]) message.obj;
            Log.e(TAG, "" + data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " " + data[4]);
            double currentTempVal = data[1] - 90;
            String modeVal = String.valueOf(data[2]);
            String mode = (data[2] == 1 ? "화이자" : data[2] == 2 ? "모더나" : data[2] == 3 ? "아스트라제네카" : data[2] == 4 ? "얀센" : "-");
            double refTempVal = data[3] - 90;

            tvCurrentTemp.setText(String.valueOf(currentTempVal));
            tvMode.setText(mode);
            tvRefrigeratorTemp.setText(String.valueOf(refTempVal));

            insertBluetoothData(currentTempVal, refTempVal, modeVal);
        }
        return false;
    }

    private void insertBluetoothData(double currentTempVal, double refTempVal, String modeVal){
        final ContentValues values = new ContentValues();
        values.clear();
        values.put(Temperature.TemperatureEntry.COLUMN_BT_DEVICE, btAddress);
        values.put(Temperature.TemperatureEntry.COLUMN_MODE, modeVal);
        values.put(Temperature.TemperatureEntry.COLUMN_CUR_TEMP, currentTempVal);
        values.put(Temperature.TemperatureEntry.COLUMN_REF_TEMP, refTempVal);
        values.put(Temperature.TemperatureEntry.COLUMN_DATE, DateUtil.dateToDateString(new Date()));

        contentResolver.insert(TemperatureProvider.CONTENT_URI, values);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("뒤로가기");
        builder.setMessage("블루투스 연결을 해제하실건가요?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                disconnect();
                MainActivity.super.onBackPressed();
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



    private void setupBottomNavigationView(){
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        BottomNavigationViewHolder.enableNavigation(MainActivity.this, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}