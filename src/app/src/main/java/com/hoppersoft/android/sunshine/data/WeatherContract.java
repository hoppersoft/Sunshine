package com.hoppersoft.android.sunshine.data;

import android.provider.BaseColumns;

/**
 * Created by hopperadmin on 9/7/2014.
 */
public class WeatherContract {
    public static final String DATABASE_NAME = "sunshine.db";
    static final int DATABASE_VERSION = 1;

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";
    }

    public static final class LocationEntry implements BaseColumns
    {
        public static final String TABLE_NAME = "location";

        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_NAME = "name";

        // Column with the foreign key into the location table.
        public static final String COLUMN_SETTING = "setting";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LONGITUDE = "longitude";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LATITUDE = "latitude";
    }
}
