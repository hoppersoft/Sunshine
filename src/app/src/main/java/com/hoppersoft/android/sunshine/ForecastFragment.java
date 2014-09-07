package com.hoppersoft.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* Created by ahopper on 8/9/2014.
*/
public class ForecastFragment extends Fragment
    implements IAsyncTaskNotificationRecipient<String, List<String>>
{
    private final String LOG_TAG = FetchJsonTask.class.getSimpleName();
    private ListView _forecasts;
    private ArrayAdapter<String> _itemsAdapter;

    public ForecastFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_debugRefresh) {
            fetchForecast();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        _forecasts = (ListView)rootView.findViewById(R.id.listview_forecast);
        _itemsAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview);
        _forecasts.setAdapter(_itemsAdapter);

        _forecasts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)_itemsAdapter.getItem(position);
                Intent detailsIntent = new Intent(getActivity(), DetailActivity.class);
                detailsIntent.putExtra(Intent.EXTRA_TEXT, item);
                startActivity(detailsIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        fetchForecast();
    }

    private void fetchForecast()
    {
        FetchForecastTask fetcher = new FetchForecastTask(this);
        String location = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
        String units = PreferenceManager
                .getDefaultSharedPreferences(getActivity())
                .getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
        fetcher.fetchForecast(location, units);
    }

    @Override
    public void onProgressUpdate(String... progress) {
        Log.v(LOG_TAG, "FetchProgress: " + progress);
    }

    @Override
    public void onCompleted(List<String> result) {
        Log.i(LOG_TAG, "Success! " + Arrays.toString(result.toArray()));

        _itemsAdapter.clear();
        _itemsAdapter.addAll(result);
    }
}
