package com.example.android.bluetooth_prototype.util;

public class HexUtil {
    public static String byteToHexString(byte data){
        int decimal = (int) data & 0xff;
        String hex = Integer.toHexString(decimal);
        if(hex.length() % 2 == 1) {
            hex = "0" + hex;
        }
        return hex;
    }
}
