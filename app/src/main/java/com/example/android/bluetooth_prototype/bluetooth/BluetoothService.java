package com.example.android.bluetooth_prototype.bluetooth;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.example.android.bluetooth_prototype.util.HexUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService extends Application {
    private static final String TAG = "BLUETOOTH_SERVICE";
    private Handler handler; // handler that gets info from Bluetooth service
    private ConnectThread connectThread;

    private int state;

    public static final int MSG_OK = 0;
    public static final int MSG_READ = 1;
    public static final int MSG_WRITE = 2;
    public static final int MSG_CANCEL = 3;
    public static final int MSG_CONNECTED = 4;

    public static final int STATE_NONE = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    private boolean stoppingConnection;

    public BluetoothService(){
        this.state = STATE_NONE;
        this.handler = null;
    }

    public void setActivityHandler(Handler handler){
        this.handler = handler;
    }

    /**
     * Send a message to the current activity registered to the activityHandler varible
     * @param type
     * @param value
     */
    private synchronized void sendMessage(int type, Object value){
        if(handler != null) {
            handler.obtainMessage(type, value).sendToTarget();
        }
    }

    private synchronized void setState(int newState){
        state = newState;
    }

    public synchronized void connect(BluetoothDevice bluetoothDevice){
        stoppingConnection = false;

        if(connectThread != null){
            connectThread.cancel();
            connectThread = null;
        }
        setState(STATE_CONNECTED);

        connectThread = new ConnectThread(bluetoothDevice);
        connectThread.start();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private InputStream inputStream;
        private OutputStream outputStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectThread(BluetoothDevice bluetoothDevice) {
            BluetoothSocket tmp = null;
            try {
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            } catch(IOException e){
                e.printStackTrace();
            }
            socket = tmp;
        }

        public void run() {

            try {
                socket.connect();
            } catch(Exception e){
                if(!stoppingConnection) {
                    try {
                        socket.close();
                    } catch (IOException closeException) {

                        Log.e(TAG, "Could not close the client socket", closeException);

                    }
                    disconnect();
                }
                return;
            }

            setState(STATE_CONNECTED);
            sendMessage(MSG_CONNECTED, null);

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch(IOException e){
                disconnect();
                e.printStackTrace();
                return;
            }


            while(true){
                try{

                    int count = 0;
                    byte data;
                    byte[] bytes = new byte[5];
                    StringBuilder input = new StringBuilder();

                    while(count < 5){
                        data = (byte)inputStream.read();
                        bytes[count] = data;
                        String hexString = HexUtil.byteToHexString(data);

                        if(count==0 && !hexString.equals("7e")) break;

                        input.append(hexString);
                        if(count < 4){
                            input.append(" ");
                        }
                        count++;
                    }
                    if(checksum(bytes) && count==5){
                        sendMessage(MSG_READ, bytes);
                        Thread.sleep(1000*10);
                    }
                } catch(Exception e){
                    if(!stoppingConnection) {
                        Log.e(TAG, "Failed to read");
                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                        disconnect();
                    }
                    break;
                }
            }

        }

        private boolean checksum(byte[] data){
            if(!HexUtil.byteToHexString(data[0]).equals("7e")){
                return false;
            }

            int sum = data[0] + data[1] + data[2] + data[3];
            String hexSumStr = String.valueOf(Integer.toHexString(sum)).substring(1);

            if(!hexSumStr.equals(HexUtil.byteToHexString(data[4]))){
                return false;
            }

            return true;
        }



        // Call this from the main activity to send data to the remote device.
        public boolean write(String out) {
            if(outputStream == null) {
                return false;
            }

            try {
                if(out != null) {
                    sendMessage(MSG_WRITE, out);
                    outputStream.write(out.getBytes());
                } else {
                    outputStream.write(0);
                }
                outputStream.write('\n');
                return true;
            } catch (IOException e){
                e.printStackTrace();
                return false;
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
                e.printStackTrace();
            }
        }
    }

    public boolean write(String out){
        ConnectThread thread;
        synchronized (this){
            if(state != STATE_CONNECTED){
                return false;
            }
            thread = connectThread;
        }
        return thread.write(out);
    }

    public synchronized void disconnect() {
        if(!stoppingConnection) {
            stoppingConnection = true;
            if(connectThread != null) {
                connectThread.cancel();
                connectThread = null;
            }
            setState(STATE_NONE);
            sendMessage(MSG_CANCEL, "Connection ended");
        }
    }
}