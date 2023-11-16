package com.rolande.mywatchlists.ui.settings;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.rolande.mywatchlists.R;

/**
 * Provides ability to modify this app's preferences, which are essentially the
 * information on how to reach the two API services we are using: Watchlist and Quote.
 * Launches a native android 'SettingsFragment' to manage the UI.
 *
 * @author Rolande
 */
public class SettingsActivity extends AppCompatActivity {
    final static String TAG = LOG_TAG_PREFIX + SettingsActivity.class.getSimpleName();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(R.string.title_activity_settings);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.settings_container, new SettingsFragment()).commit();
    }
}