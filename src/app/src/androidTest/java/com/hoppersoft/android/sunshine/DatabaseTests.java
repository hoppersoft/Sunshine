package com.hoppersoft.android.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.hoppersoft.android.sunshine.data.WeatherContract;
import com.hoppersoft.android.sunshine.data.WeatherContract.WeatherEntry;
import com.hoppersoft.android.sunshine.data.WeatherContract.LocationEntry;
import com.hoppersoft.android.sunshine.data.WeatherDbHelper;

import java.util.Map;

/**
 * Created by hopperadmin on 9/7/2014.
 */
public class DatabaseTests extends AndroidTestCase {
    public static final String LOG_TAG = DatabaseTests.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext.deleteDatabase(WeatherContract.DATABASE_NAME);
    }

    public void testCreateDb() {
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }

    public void testLocationCreation() {
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_NAME, "Home");
        values.put(LocationEntry.COLUMN_SETTING, "30345");
        values.put(LocationEntry.COLUMN_LATITUDE, 33.846200);
        values.put(LocationEntry.COLUMN_LONGITUDE, -84.271599);
        db.insert(LocationEntry.TABLE_NAME, null, values);
        Cursor cursor = db.query(true, LocationEntry.TABLE_NAME, new String[]{LocationEntry._ID}, LocationEntry.COLUMN_NAME + " = ?", new String[]{"Home"}, null, null, null, "1");
        assertEquals(1, cursor.getCount());
    }

    public void testWeatherRetrieval() throws Exception {
        testLocationCreation();
        SQLiteDatabase db = new WeatherDbHelper(mContext).getWritableDatabase();

        int locationId = 0;
        Cursor cursor = db.query(true, LocationEntry.TABLE_NAME, new String[]{LocationEntry._ID}, LocationEntry.COLUMN_NAME + " = ?", new String[]{"Home"}, null, null, null, "1");
        if (cursor.moveToFirst()) {
            locationId = cursor.getInt(0);
        }

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75.0);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65.0);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

        cursor = db.query(WeatherEntry.TABLE_NAME, null, null, null, null, null, null);
        assertEquals(1, cursor.getCount());

        assertTrue(cursor.moveToFirst());
        for (Map.Entry<String, Object> column : weatherValues.valueSet()) {
            Object value = null;
            String key = column.getKey();
            int columnIndex = cursor.getColumnIndex(key);
            switch (DbCompat.getType(cursor, columnIndex)) {
                case DbCompat.FIELD_TYPE_FLOAT:
                    float expectedFloat = new Float((Double)column.getValue());
                    assertEquals(expectedFloat, cursor.getFloat(columnIndex));
                    break;
                case DbCompat.FIELD_TYPE_INTEGER:
                    int expectedInt = (Integer)column.getValue();
                    assertEquals(expectedInt, cursor.getInt(columnIndex));
                    break;
                case DbCompat.FIELD_TYPE_STRING:
                    String expectedString = (String)column.getValue();
                    assertEquals(expectedString, cursor.getString(columnIndex));
                    break;
            }
        }
    }
}

class DbCompat {

    protected static final int FIELD_TYPE_BLOB = 4;
    protected static final int FIELD_TYPE_FLOAT = 2;
    protected static final int FIELD_TYPE_INTEGER = 1;
    protected static final int FIELD_TYPE_NULL = 0;
    protected static final int FIELD_TYPE_STRING = 3;

    public static int getType(Cursor cursor, int i) throws Exception {
        SQLiteCursor sqLiteCursor = (SQLiteCursor) cursor;
        CursorWindow cursorWindow = sqLiteCursor.getWindow();
        int pos = cursor.getPosition();
        int type = -1;
        if (cursorWindow.isNull(pos, i)) {
            type = FIELD_TYPE_NULL;
        } else if (cursorWindow.isLong(pos, i)) {
            type = FIELD_TYPE_INTEGER;
        } else if (cursorWindow.isFloat(pos, i)) {
            type = FIELD_TYPE_FLOAT;
        } else if (cursorWindow.isString(pos, i)) {
            type = FIELD_TYPE_STRING;
        } else if (cursorWindow.isBlob(pos, i)) {
            type = FIELD_TYPE_BLOB;
        }

        return type;
    }
}
