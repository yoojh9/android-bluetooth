package com.example.android.bluetooth_prototype.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class TemperatureDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DB_HELPER";
    public static final String DATABASE_NAME = "temperature.db";

    private static final int DATABASE_VERSION = 1;

    public TemperatureDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TEMP_TABLE = "CREATE TABLE " + Temperature.TemperatureEntry.TABLE_NAME + " (" +
                Temperature.TemperatureEntry._ID    + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                Temperature.TemperatureEntry.COLUMN_BT_DEVICE + " TEXT NOT NULL," +
                Temperature.TemperatureEntry.COLUMN_DATE    + " TEXT NOT NULL," +
                Temperature.TemperatureEntry.COLUMN_CUR_TEMP + " REAL NOT NULL," +
                Temperature.TemperatureEntry.COLUMN_REF_TEMP + " REAL NOT NULL," +
                Temperature.TemperatureEntry.COLUMN_MODE + " TEXT NOT NULL" + ");";
        try {
            sqLiteDatabase.execSQL(SQL_CREATE_TEMP_TABLE);
        } catch(SQLException e){
            Log.e(TAG, "created: Failed." + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //기존 테이블을 지우고
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Temperature.TemperatureEntry.TABLE_NAME);
        //새 테이블 생성
        onCreate(sqLiteDatabase);
    }

}
