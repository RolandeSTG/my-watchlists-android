package com.rolande.mywatchlists.ui.main;

import static com.rolande.mywatchlists.Constants.GRID_LAYOUT_LANDSCAPE_SPAN_COUNT;
import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;
import static com.rolande.mywatchlists.Constants.WATCHLIST_ID_KEY;
import static com.rolande.mywatchlists.Constants.WATCHLIST_NAME_KEY;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.rolande.mywatchlists.R;
import com.rolande.mywatchlists.api.watchlist.WatchlistClient;
import com.rolande.mywatchlists.ui.settings.SettingsActivity;
import com.rolande.mywatchlists.ui.watchlist_detail.WatchlistDetailActivity;
import com.rolande.mywatchlists.model.Settings;
import com.rolande.mywatchlists.model.beans.Watchlist;

import java.util.ArrayList;
import java.util.List;

/**
 * Application's entry point. The main activity actually consists of a 'watchlist' activity.
 * This activity is responsible for getting the list of existing watchlists from the
 * server and allows CRUD operations on them. Upon selecting a watchlist, starts the
 * watchlist-detail activity to provide simulated real-time quotes for securities in the list.
 *
 * @author Rolande St-Gelais
 */
public class MainActivity extends AppCompatActivity {
    final static String TAG = LOG_TAG_PREFIX + MainActivity.class.getSimpleName();
    Toolbar toolbar;
    RecyclerView rv_watchlists, rv_headings;
    ImageButton imgBtn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar         = findViewById(R.id.toolbar);
        rv_headings     = findViewById(R.id.rv_headings);
        rv_watchlists   = findViewById(R.id.rv_watchlists);
        imgBtn_add      = findViewById(R.id.img_btn_add);

        setupToolbar();
        setupLayoutManagers();                 // Setup recyclerViews layout managers
        setupAddButton();                      // Setup 'Add' watchlist button
    }

    /**
     * Set up the toolbar's properties for this activity.
     */
    private void setupToolbar() {

        setSupportActionBar(toolbar);

        // Set toolbar's 'Home' section using a custom smaller version of app icon...

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setIcon(R.drawable.ic_launcher_home);       // icon displayed (48x48) = Good!

        toolbar.setTitleMarginStart(48);                      // in pixels, looks good
    }

    /**
     * Determine which layout manager to use for the recycler views, depending on the device's
     * orientation.
     *
     * Notice that there are 2 recycler views in this activity: 1 for the
     * headings and 1 for the actual list of watchlists. The reason for this is that I
     * wanted the headings to always appear on the screen (i.e. frozen) as opposed
     * to being scrolled up if they had been included as a special view-type in a
     * single recycler view for the list...This way, headings are always visible
     * and appear correctly when we put the device in landscape mode, i.e. we
     * have the same number of headings as there are columns.
     */
    private void setupLayoutManagers() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager headingsManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
            rv_headings.setLayoutManager(headingsManager);

            LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
            rv_watchlists.setLayoutManager(manager);
        }
        else {
            GridLayoutManager headingsManager = new GridLayoutManager(MainActivity.this, GRID_LAYOUT_LANDSCAPE_SPAN_COUNT);
            rv_headings.setLayoutManager(headingsManager);

            GridLayoutManager manager = new GridLayoutManager(MainActivity.this, GRID_LAYOUT_LANDSCAPE_SPAN_COUNT);
            rv_watchlists.setLayoutManager(manager);
        }

    }

    /**
     * Setup related to the 'add' watchlist button.
     */
    private void setupAddButton() {
        imgBtn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_listName = new EditText(MainActivity.this);
                et_listName.setHint(R.string.add_watchlist_hint);
                et_listName.setInputType(InputType.TYPE_CLASS_TEXT);

                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.add_watchlist_title)
                        .setView(et_listName)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String listName = et_listName.getText().toString().trim();
                                if (! listName.isEmpty()) {
                                    addWatchlist(listName);
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .create();

                alertDialog.show();
            }
        });

    }

    /**
     * Add options menu to the toolbar. For the moment, there is a single option, which is 'Settings'.
     *
     * @param menu The options menu in which the items are placed.
     * @return true (up the chain) to display the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this will add menu items to the app bar

        getMenuInflater().inflate(R.menu.main_options_menu, menu);

        MenuItem item = menu.findItem(R.id.action_settings);

        // Change the color of the Settings action button to that of the toolbar's title text,
        // which is defined by the colorOnPrimary attribute...

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int color = typedValue.data;
        item.setIconTintList(ColorStateList.valueOf(color));

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Launches the activity corresponding to the menu item selected.
     *
     * @param item The menu item that was selected.
     * @return true if menu action was handled; false otherwise.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent, null);

            // Tell Android the menu action has been handled...
            return true;
        }

        // super returns false here...let Android know we dealt with the item being clicked;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Things to do when activity becomes ready for user interaction.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Settings.setContext(getApplicationContext());          // Set context for SharedPreferences...

        getWatchlists();
    }

    /**
     * Get the list of existing watchlists from the Watchlist Server.
     */
    private void getWatchlists() {

        WatchlistClient client = new WatchlistClient(MainActivity.this);

        client.getWatchlists(new WatchlistClient.GetWatchlistsResponseListener() {
            @Override
            public void onResponse(List<Watchlist> list) {
                setWatchlistRecyclerView(list);
            }

            @Override
            public void onFailure(String errorMsg) {
                setWatchlistRecyclerView(new ArrayList<>());    // set view with empty list (to reset it if had any before
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Prepares the watchlist recycler view for display. Namely, sets its adapter
     * and defines this activity as the listener for CRUD operations callback.
     * It also provides for proper layout orientation.
     *
     * @param list List of watchlists to use by the adapter
     */
    private void setWatchlistRecyclerView(List<Watchlist> list) {

        // Set adapter for headings recycleView
        WatchlistHeaderAdapter headerWatchlistAdapter = new WatchlistHeaderAdapter(MainActivity.this, null,
                getResources().getConfiguration().orientation);
        rv_headings.setAdapter(headerWatchlistAdapter);

        // Set adapter for watchlists recycleView
        WatchlistAdapter watchlistAdapter = new WatchlistAdapter(MainActivity.this, list);

        // Handle the adapter's events in here...
        watchlistAdapter.setListener(new WatchlistAdapter.Listener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(MainActivity.this, WatchlistDetailActivity.class);
                intent.putExtra(WATCHLIST_ID_KEY, watchlistAdapter.getItemId(position));
                intent.putExtra(WATCHLIST_NAME_KEY, watchlistAdapter.getItemName(position));

                startActivity(intent, null);
            }

            @Override
            public void onUpdate(int position, String newName) {
                updateWatchlist(position, newName);
            }

            @Override
            public void onDelete(int position) {
                deleteWatchlist(watchlistAdapter.getItemId(position), position);
            }
        });

        rv_watchlists.setAdapter(watchlistAdapter);
    }

    /**
     * Sends an add-watchlist request to the watchlist server. Add request will be denied if a watchlist
     * by the same name already exists. Watchlist names are case insensitive.
     *
     * @param name Name of the watchlist to add.
     */
    private void addWatchlist(String name) {

        WatchlistClient client = new WatchlistClient(MainActivity.this);

        client.addWatchlist(name, new WatchlistClient.AddWatchlistResponseListener() {
            @Override
            public void onResponse(Watchlist watchlistAdded) {
                WatchlistAdapter adapter = (WatchlistAdapter) rv_watchlists.getAdapter();
                adapter.addItem(watchlistAdded);
            }

            @Override
            public void onFailure(String errorMsg) {
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sends an update-watchlist request to the watchlist server. An update consists only in
     * renaming a watchlist, nothing else. If the new name is already used by another list,
     * update request will be denied.
     *
     * @param position Position of the watchlist in the adapter's list
     * @param newName New name to give to watchlist
     */
    private void updateWatchlist(int position, String newName) {

        WatchlistAdapter adapter = (WatchlistAdapter) rv_watchlists.getAdapter();
        Watchlist watchlist = adapter.getItem(position);

        // Only the name can be changed...
        Watchlist newWatchlist = new Watchlist(watchlist.getId(), newName, watchlist.getDateCreated(),
                watchlist.getNumberOfSecurities());

        WatchlistClient client = new WatchlistClient(MainActivity.this);

        client.updateWatchlist(newWatchlist, new WatchlistClient.BooleanResponseListener() {
            @Override
            public void onResponse(boolean success, String errorMsg) {
                if (success) {
                    adapter.updateItem(position, newName);
                }
                else {
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    /**
     *  Sends a delete-watchlist request to the watchlist server.
     *
     * @param id Id of the watchlist to delete
     * @param position Position of the watchlist in the adapter
     */
    private void deleteWatchlist(long id, int position) {

        WatchlistClient client = new WatchlistClient(MainActivity.this);

        client.deleteWatchlist(id, new WatchlistClient.BooleanResponseListener() {
            @Override
            public void onResponse(boolean success, String errorMsg) {
                if (success) {
                    WatchlistAdapter adapter = (WatchlistAdapter) rv_watchlists.getAdapter();
                    adapter.removeItem(position);
                }
                else {
                    Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

}

