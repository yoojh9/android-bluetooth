package com.example.android.bluetooth_prototype.db;

import android.provider.BaseColumns;

import java.util.Date;

public class Temperature {
    public static final class TemperatureEntry implements BaseColumns {
        // table 이름
        public static final String TABLE_NAME = "temperature";
        public static final String COLUMN_BT_DEVICE = "bt_device";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_CUR_TEMP = "cur_temp";
        public static final String COLUMN_MODE = "mode";
        public static final String COLUMN_REF_TEMP = "ref_temp";
    }
}
