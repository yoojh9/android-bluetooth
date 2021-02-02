package com.example.android.bluetooth_prototype;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;

import com.example.android.bluetooth_prototype.bluetooth.BluetoothManager;
import com.example.android.bluetooth_prototype.db.Temperature;
import com.example.android.bluetooth_prototype.db.TemperatureProvider;
import com.example.android.bluetooth_prototype.ui.ChartFragment;
import com.example.android.bluetooth_prototype.ui.DataFragment;
import com.example.android.bluetooth_prototype.ui.HomeFragment;
import com.example.android.bluetooth_prototype.util.DateUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.Date;

public class MainActivity extends BluetoothBaseActivity {

    private static final String TAG = "MAIN_ACTIVITY";
    private ContentResolver contentResolver;
    private String btAddress;
    private HomeFragment homeFragment;
    private Fragment dataFragment;
    private Fragment chartFragment;
    private BottomNavigationView bottomNavigationView;
    private static BluetoothManager bluetoothService;
    private boolean preventCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothService = (BluetoothManager)getApplicationContext();

        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        homeFragment = HomeFragment.newInstance();
        dataFragment = DataFragment.newInstance();
        chartFragment = ChartFragment.newInstance();

        contentResolver = getContentResolver();
        btAddress = getSharedPreferences("device", MODE_PRIVATE).getString("deviceAddress", null);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, homeFragment).commit();
    }

        @Override
    public boolean handleMessage(@NonNull Message message) {

        if (message.what == BluetoothManager.MSG_READ) {
            byte[] data = (byte[]) message.obj;
            Log.e(TAG, "" + data[0] + " " + data[1] + " " + data[2] + " " + data[3] + " " + data[4]);
            double currentTempVal = data[1] - 90;
            String modeVal = String.valueOf(data[2]);
            String mode = (data[2] == 1 ? "화이자" : data[2] == 2 ? "모더나" : data[2] == 3 ? "아스트라제네카" : data[2] == 4 ? "얀센" : "-");
            double refTempVal = data[3] - 90;


            homeFragment.setData(mode, currentTempVal, refTempVal);
            //tvCurrentTemp.setText(String.valueOf(currentTempVal));
            //tvMode.setText(mode);
            //tvRefrigeratorTemp.setText(String.valueOf(refTempVal));

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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch(menuItem.getItemId()) {
                        case R.id.navigation_home:
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, homeFragment).commit();
                            return true;
                        case R.id.navigation_note:
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, dataFragment).commit();
                            return true;
                        case R.id.navigation_chart:
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_layout, chartFragment).commit();
                            return true;
                        default: return false;
                    }
                }
            };

}