package com.example.android.bluetooth_prototype;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.android.bluetooth_prototype.bluetooth.BluetoothManager;

public class BluetoothBaseActivity extends AppCompatActivity implements Handler.Callback{

    private static BluetoothManager bluetoothService;

    protected boolean preventCancel;

    private final static String TAG = "BLUETOOTH_BASE_ACTIVITY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothService = (BluetoothManager) getApplicationContext();
    }

    protected boolean write(String message){
        return bluetoothService.write(message);
    }

    protected void disconnect(){
        bluetoothService.disconnect();
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        switch (message.what){
            case BluetoothManager.MSG_OK:
                break;
            case BluetoothManager.MSG_CANCEL:
                setResult(BluetoothManager.MSG_CANCEL, new Intent());
                finish();
                break;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Message.obtain(new Handler(this), resultCode).sendToTarget();
    }

    @Override
    protected void onResume() {
        bluetoothService.setActivityHandler(new Handler(this));
        preventCancel = false;
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(BluetoothManager.MSG_OK, new Intent());
        finish();
    }

    @Override
    public void finish() {
        bluetoothService.setActivityHandler(null);
        super.finish();
    }

    @Override
    protected void onPause() {
        if(!preventCancel){
            Message.obtain(new Handler(this), BluetoothManager.MSG_CANCEL).sendToTarget();
        }
        super.onPause();
    }
}
