package com.example.android.bluetooth_prototype;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bluetooth_prototype.adapter.BTDeviceListAdapter;
import com.example.android.bluetooth_prototype.bluetooth.BTDevice;
import com.example.android.bluetooth_prototype.bluetooth.BluetoothService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothDeviceListActivity extends AppCompatActivity implements Handler.Callback{

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothService bluetoothService;
    private static final String TAG = "DEVICE_SELECT";
    private static final int REQUEST_ENABLE_BT = 1;
    private List<BTDevice> btPairedDevices = new ArrayList<BTDevice>();
    private List<BTDevice> btAvailableDevices = new ArrayList<BTDevice>();
    private ListView pairedListView, availableListView;
    private AlertDialog connectionAlertDialog;
    private BTDeviceListAdapter btAvailableListAdapter, btPairedListAdapter;

    private static final int ACTION_LIST = 0;
    private static final int BT_ENABLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_device_list);

        bluetoothService = (BluetoothService) getApplicationContext();
        // 기존 페어링 된 기기 리스트
        pairedListView = (ListView) findViewById(R.id.lvPairedDevices);
        btPairedListAdapter = new BTDeviceListAdapter(this, btPairedDevices);
        pairedListView.setAdapter(btPairedListAdapter);
        pairedListView.setOnItemClickListener(btDeviceItemClickListener);

        // 내 주변 기기 리스트
        availableListView = (ListView) findViewById(R.id.lvAvailableDevices);
        btAvailableListAdapter = new BTDeviceListAdapter(this, btAvailableDevices);
        availableListView.setAdapter(btAvailableListAdapter);
        availableListView.setOnItemClickListener(btDeviceItemClickListener);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkBTPermissions();
        }

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }
        Log.e(TAG, "enabled: " + (bluetoothAdapter.isEnabled() ? "true" : "false"));

        registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(receiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));


        startDiscovery();

    }

    private void startDiscovery(){
        if(!checkBluetoothState()){
            finish();
            return;
        }

        btPairedDevices.clear();
        btPairedListAdapter.notifyDataSetChanged();

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device: pairedDevices) {
                btPairedDevices.add(new BTDevice(device.getName(), device.getAddress()));
            }
            btPairedListAdapter.notifyDataSetChanged();
        }

//        btPairedDevices.clear();
//        btPairedListAdapter.notifyDataSetChanged();
        bluetoothAdapter.startDiscovery();

    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTDevice btDevice = new BTDevice(device.getName(), device.getAddress());
                Log.e(TAG, btDevice.toString());
                if(device.getBondState() != BluetoothDevice.BOND_BONDED){   // indicates the remote device is bonded(paired)
                    btAvailableDevices.add(btDevice);
                    btAvailableListAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    private final AdapterView.OnItemClickListener btDeviceItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
            bluetoothAdapter.cancelDiscovery();

            final BTDevice btDevice = (BTDevice) parent.getItemAtPosition(i);
            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("블루투스 연결");
            builder.setMessage(btDevice.getName()+" 기기를 연결하실건가요?");
            builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(btDevice.getAddress());
                    bluetoothService.connect(bluetoothAdapter.getRemoteDevice(device.getAddress()));
                    SharedPreferences sharedPreferences = getSharedPreferences("device", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("deviceAddress", btDevice.getAddress());
                    editor.commit();
                }
            });
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                    bluetoothService.disconnect();
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };



    private boolean checkBluetoothState() {
        if(bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "블루투스가 사용 불가능합니다.", Toast.LENGTH_SHORT).show();
            return false;
        } else if(!(bluetoothAdapter.isEnabled())){
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BT_ENABLE);
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkBTPermissions(){
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode: " +requestCode);
        switch (requestCode) {
            case BT_ENABLE:
                if(!bluetoothAdapter.isEnabled()) {
                    Toast.makeText(getApplicationContext(), "Bluetooth must be enabled", Toast.LENGTH_SHORT).show();
                }
                else {
                    //startDiscovery();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothAdapter !=null){
            bluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(receiver);
    }

    @Override
    protected void onResume() {
        bluetoothService.setActivityHandler(new Handler(this));
        super.onResume();
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        switch(message.what) {
            case BluetoothService.MSG_OK : break;
            case BluetoothService.MSG_CANCEL:
                if(message.obj != null){
                    Toast.makeText(this, (String)message.obj, Toast.LENGTH_SHORT).show();
                }
                break;
            case BluetoothService.MSG_CONNECTED:
                Log.e(TAG, "BLUETOOTH CONNECT");
                startActivityForResult(new Intent(getApplicationContext(), MainActivity.class), ACTION_LIST);
                break;
        }
        return false;
    }
}