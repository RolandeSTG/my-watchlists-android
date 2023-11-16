package com.rolande.mywatchlists.ui.watchlist_detail;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;
import static com.rolande.mywatchlists.Constants.WATCHLIST_ID_KEY;
import static com.rolande.mywatchlists.Constants.WATCHLIST_NAME_KEY;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.rolande.mywatchlists.Constants;
import com.rolande.mywatchlists.R;

/**
 * Activity to manage a single watchlist securities, delegated to the Security fragment.
 * It receives the watchlist ID and name as parameters upon creation.
 *
 * @author Rolande
 */
public class WatchlistDetailActivity extends AppCompatActivity {
    final static String TAG = LOG_TAG_PREFIX + WatchlistDetailActivity.class.getSimpleName();
    private Long watchlistId;               // param 1
    private String watchlistName;           // param 2
    Toolbar toolbar;
    static SecurityFragment securityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist_detail);

        watchlistId = getIntent().getExtras().getLong(WATCHLIST_ID_KEY);
        watchlistName = getIntent().getExtras().getString(WATCHLIST_NAME_KEY);

        toolbar = findViewById(R.id.toolbar);

        // Setup the toolbar...
        toolbar.setTitle(watchlistName);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Setup the fragment and launch it...
        securityFragment = new SecurityFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(Constants.WATCHLIST_ID_KEY, watchlistId);
        securityFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().replace(R.id.list_container, securityFragment).commit();
    }

}
