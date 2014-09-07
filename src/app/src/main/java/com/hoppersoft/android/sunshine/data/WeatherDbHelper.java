package com.hoppersoft.android.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hopperadmin on 9/7/2014.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    public WeatherDbHelper(Context context) {
        super(context, WeatherContract.DATABASE_NAME, null, WeatherContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String locationTableCreationCommand = "CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME + " (" +
            WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            WeatherContract.LocationEntry.COLUMN_NAME + " TEXT NOT NULL," +
            WeatherContract.LocationEntry.COLUMN_SETTING + " TEXT UNIQUE NOT NULL," +
            WeatherContract.LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL," +
            WeatherContract.LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL," +
            "UNIQUE (" + WeatherContract.LocationEntry.COLUMN_SETTING + ") ON CONFLICT IGNORE" +
            ");";
        db.execSQL(locationTableCreationCommand);
        String weatherTableCreationCommand = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " (" +
                WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL REFERENCES " + WeatherContract.LocationEntry.TABLE_NAME + "(" + WeatherContract.LocationEntry._ID + ")," +
                WeatherContract.WeatherEntry.COLUMN_DATETEXT + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_WIND_SPEED+ " REAL NOT NULL," +
                WeatherContract.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL," +
                "UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATETEXT + ", " + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE" +
                ");";
        db.execSQL(weatherTableCreationCommand);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME + ";");
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME + ";");
        onCreate(db);
    }
}
