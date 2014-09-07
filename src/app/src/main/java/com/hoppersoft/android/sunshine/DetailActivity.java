package com.hoppersoft.android.sunshine;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.hoppersoft.android.sunshine.R;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id)
        {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_show_map:
                Intent mapIntent = new Intent(Intent.ACTION_VIEW);
                String location = PreferenceManager
                        .getDefaultSharedPreferences(this)
                        .getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("geo");
                builder.path("0,0");
                builder.appendQueryParameter("q", location);
                mapIntent.setData(builder.build());
                ComponentName handler = mapIntent.resolveActivity(getPackageManager());
                if (handler != null) startActivity(mapIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private String _currentDetails;

        public PlaceholderFragment() {
            this.setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            TextView title = (TextView)rootView.findViewById(R.id.detail_title);
            _currentDetails = intent.getStringExtra(Intent.EXTRA_TEXT);
            title.setText(_currentDetails);
            return rootView;
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.detailfragment, menu);
            MenuItem shareAction = menu.findItem(R.id.action_share);
            ShareActionProvider shareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareAction);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, _currentDetails + "#SunshineApp");
            shareProvider.setShareIntent(shareIntent);
        }
    }
}
