package com.hoppersoft.android.sunshine;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ahopper on 8/9/2014.
 */
public class FetchJsonTask extends AsyncTask<URL, String, String> {
    private final String LOG_TAG = FetchJsonTask.class.getSimpleName();

    private IAsyncTaskNotificationRecipient<String, String> _listener;
    public FetchJsonTask(IAsyncTaskNotificationRecipient<String, String> listener) {
        if (listener == null) throw new IllegalArgumentException();
        _listener = listener;
    }

    @Override
    protected String doInBackground(URL... urls) {
        return getForecastData(urls[0]);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        _listener.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        _listener.onCompleted(s);
    }

    private String getForecastData(URL url) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");

            Log.v(LOG_TAG, "Connecting to " + url.toString());
            urlConnection.connect();

            Log.v(LOG_TAG, "Reading response...");
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream != null) {
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() != 0) {
                    // Stream was empty.  No point in parsing.
                    forecastJsonStr = buffer.toString();
                } else {
                    Log.w(LOG_TAG, "An empty response was returned.");
                }
            } else {
                Log.w(LOG_TAG, "No input stream.");
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            forecastJsonStr = null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return forecastJsonStr;
    }

}
