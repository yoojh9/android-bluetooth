package com.example.android.bluetooth_prototype.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static String dateToDateString(Date date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
            return simpleDateFormat.format(date);
        } catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
