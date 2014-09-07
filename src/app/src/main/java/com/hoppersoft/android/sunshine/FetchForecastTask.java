package com.hoppersoft.android.sunshine;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.net.Uri.Builder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ahopper on 8/9/2014.
 */
public class FetchForecastTask
        implements IAsyncTaskNotificationRecipient<String, String> {
    private final String LOG_TAG = FetchJsonTask.class.getSimpleName();
    private IAsyncTaskNotificationRecipient<String, List<String>> _listener;
    private FetchJsonTask _fetchJsonTask;

    public FetchForecastTask(IAsyncTaskNotificationRecipient<String, List<String>> listener)
    {
        if (listener == null) throw new IllegalArgumentException();
        _listener = listener;
        _fetchJsonTask = new FetchJsonTask(this);
    }

    public void fetchForecast(String postalCode, String units) {
        if (postalCode == null) throw new IllegalArgumentException();

        // Construct the URL for the OpenWeatherMap query
        // Possible parameters are available at OWM's forecast API page, at
        // http://openweathermap.org/API#forecast
        Builder builder = new Builder();
        builder.scheme("http").authority("api.openweathermap.org").path("data/2.5/forecast/daily");
        builder.appendQueryParameter("mode", "json");
        String requestedUnits = "metric";
        if ("F".equals(units))
        {
            requestedUnits = "imperial";
        }
        builder.appendQueryParameter("units", requestedUnits);
        builder.appendQueryParameter("cnt", "7");
        builder.appendQueryParameter("q", postalCode);
        Uri uri = builder.build();
        Log.v(LOG_TAG, "Fetching data from: " + uri.toString());

        URL getUrl = null;
        FetchJsonTask fetcher = new FetchJsonTask(this);
        try {
            //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
            // I hate Java. Well, more precisely, I hate whomever designed this dumbass API.
            // Seriously? I can't get a freaking URL directly from a Uri.Builder? .NET FTW.
            getUrl = new URL(uri.toString());
        }
        catch (MalformedURLException e)
        {
            Log.e(LOG_TAG, "Fetch error: ", e);
        }
        fetcher.execute(getUrl);
        return;
    }

    @Override
    public void onProgressUpdate(String... progress) {
        Log.v(LOG_TAG, "FetchProgress: " + progress);
    }

    @Override
    public void onCompleted(String result) {
        List<String> output = null;
        try {
            String[] parsedData = getWeatherDataFromJson(result, 7);
            output = Arrays.asList(parsedData);
        }
        catch (JSONException e)
        {
            Log.e(LOG_TAG, "Error parsing forecast data ", e);
        }
        _listener.onCompleted(output);
    }


    /* The date/time conversion code is going to be moved outside the asynctask later,
     * so for convenience we're breaking it out into its own method now.
     */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }
}
