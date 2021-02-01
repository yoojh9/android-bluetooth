package com.example.android.bluetooth_prototype.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TemperatureProvider extends ContentProvider {

    TemperatureDbHelper mOpenHelper;
    private static final String AUTHORITY = "com.example.android.bluetooth_prototype.db";
    private static final String BASE_PATH = "temperature";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    /** URI matcher used to recognize URIs sent by applications */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int TEMPERATURE_LIST = 100;
    private static final int BT_TEMPERATURE_LIST = 101;
    private static final int BT_TEMPERATURE_ABNORMAL_LIST = 102;

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, TEMPERATURE_LIST);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH+"/#", BT_TEMPERATURE_LIST);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH+"/#"+"/abnormal", BT_TEMPERATURE_ABNORMAL_LIST);
    }


    @Override
    public boolean onCreate() {
        // onCreate() 안에서 TemperatureDbHelper를 객체화한다.
        mOpenHelper = new TemperatureDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projections, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setStrict(true);

        int match = sURIMatcher.match(uri);
        switch (match) {
            case TEMPERATURE_LIST :
                qb.setTables(Temperature.TemperatureEntry.TABLE_NAME);
                break;
            case BT_TEMPERATURE_LIST :
                qb.setTables(Temperature.TemperatureEntry.TABLE_NAME);
                qb.appendWhere(Temperature.TemperatureEntry.COLUMN_BT_DEVICE+"=");
                qb.appendWhere(uri.getPathSegments().get(1));
                break;
            case BT_TEMPERATURE_ABNORMAL_LIST:
                qb.setTables(Temperature.TemperatureEntry.TABLE_NAME);
                qb.appendWhere(Temperature.TemperatureEntry.COLUMN_BT_DEVICE+"=");
                qb.appendWhere(uri.getPathSegments().get(1));
                selection = Temperature.TemperatureEntry.COLUMN_REF_TEMP +"> -70";
                break;
        }
        String order = sortOrder==null ? Temperature.TemperatureEntry.COLUMN_DATE + " COLLATE NOCASE DESC" : sortOrder;
        Cursor cursor = qb.query(db, projections, selection, selectionArgs, null, null, order);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sURIMatcher.match(uri);
        switch (match) {
            case TEMPERATURE_LIST :
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + BASE_PATH;
            case BT_TEMPERATURE_LIST:
            case BT_TEMPERATURE_ABNORMAL_LIST:
                return "vnd.android.curosr.item/vnd." + AUTHORITY + BASE_PATH;
            default:
                throw new IllegalArgumentException("Unsupported URI" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sURIMatcher.match(uri);
        long rowId;

        switch(match) {
            case TEMPERATURE_LIST:
                rowId = db.insertOrThrow(Temperature.TemperatureEntry.TABLE_NAME, null, contentValues);
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(CONTENT_URI, rowId);
            default:
                throw new IllegalArgumentException("UnKnown URI: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
