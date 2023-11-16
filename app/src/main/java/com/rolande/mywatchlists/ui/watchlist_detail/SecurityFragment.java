package com.rolande.mywatchlists.ui.watchlist_detail;

import static com.rolande.mywatchlists.Constants.GRID_LAYOUT_LANDSCAPE_SPAN_COUNT;
import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rolande.mywatchlists.Constants;
import com.rolande.mywatchlists.R;
import com.rolande.mywatchlists.api.watchlist.WatchlistClient;
import com.rolande.mywatchlists.model.beans.Security;
import com.rolande.mywatchlists.util.MyUtils;

import java.util.List;

/**
 * Fragment for displaying and managing a list of securities (i.e. the content of a watchlist).
 * Receives the watchlist ID upon creation.
 *
 * @author Rolande
 */
public class SecurityFragment extends Fragment {
    final static String TAG = LOG_TAG_PREFIX + SecurityFragment.class.getSimpleName();
    private Long mparam_WatchlistID;         /* Fragment's 1st (and only) param */
    RecyclerView rv_headings;
    RecyclerView rv_securities;

    public SecurityFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            this.mparam_WatchlistID = getArguments().getLong(Constants.WATCHLIST_ID_KEY);
            setHasOptionsMenu(true);
        }
     }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        // Inflate the menu; this will add this fragment's menu items to the activity's app bar
        inflater.inflate(R.menu.security_fragment_options_menu, menu);

        MenuItem item = menu.findItem(R.id.action_add);

        // Change the color of the Add action button (+ sign) to that of the toolbar's title text,
        // which is defined by the colorOnPrimary attribute...

        TypedValue typedValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
        int color = typedValue.data;
        item.setIconTintList(ColorStateList.valueOf(color));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_security, container, false);

        rv_headings   = v.findViewById(R.id.rv_headings);
        rv_securities = v.findViewById(R.id.rv_securities);

        // Setup layout manager for both recycler views (headings & securities)
        setupLayoutManagers();

        // Setup Item touch helper & enable 'delete by swiping' into the securities' recycler view
        ItemTouchHelper.SimpleCallback callback = setItemTouchHelperCallback();
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(rv_securities);

        return v;
    }

    /**
     * Determine which layout manager to use for the recycler views, depending on the device's
     * orientation.
     */
    private void setupLayoutManagers() {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            LinearLayoutManager headingsManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
            rv_headings.setLayoutManager(headingsManager);

            LinearLayoutManager manager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
            rv_securities.setLayoutManager(manager);
        }
        else {
            GridLayoutManager headingsManager = new GridLayoutManager(requireActivity(), GRID_LAYOUT_LANDSCAPE_SPAN_COUNT);
            rv_headings.setLayoutManager(headingsManager);

            GridLayoutManager manager = new GridLayoutManager(requireActivity(), GRID_LAYOUT_LANDSCAPE_SPAN_COUNT);
            rv_securities.setLayoutManager(manager);
        }

    }

    /**
     * Define the callback to run for each item in the securities recycler view.
     * Implement the 'delete' on-swipe functionality for each item (but without snackbar)...
     *
     * @return itemTouchHelper callback created
     */
    private ItemTouchHelper.SimpleCallback setItemTouchHelperCallback() {

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f;            // consider it a delete at 50% of surface swipe
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;           // not implemented
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                SecurityAdapter.ViewHolder holder = (SecurityAdapter.ViewHolder) viewHolder;
                deleteSymbol(holder);
            }

            // This onChildDraw() implementation comes mostly from a Digital Ocean online tutorial,
            // and adapted here for this app's specific usage...
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                boolean isCancelled = (dX == 0) && (! isCurrentlyActive);

                if (isCancelled) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    return;
                }

                ColorDrawable swipeBackground = new ColorDrawable();
                swipeBackground.setColor(Color.RED);

                int viewWidth = itemView.getWidth();
                int viewHeight = itemView.getHeight();

                // Set the swipe surface to be maximum 50% of the item view's width
                int maxSwipeSurface = (viewWidth / 2) * -1;        // 50% of surface, negative

                if (dX < maxSwipeSurface)  dX = maxSwipeSurface;

                swipeBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                swipeBackground.draw(c);

                Drawable deleteDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                deleteDrawable.setTint(Color.WHITE);

                // Get the delete icon's width & height
                int iconWidth = deleteDrawable.getIntrinsicWidth();
                int iconHeight = deleteDrawable.getIntrinsicHeight();

                // Then, calculate its bounds to center it in the swipe surface
                int iconMargin = (viewHeight - iconHeight) / 2;
                int iconTop = itemView.getTop() + (viewHeight - iconHeight) / 2;
                int iconRight = itemView.getRight() - iconMargin;
                int iconBottom = iconTop + iconHeight;
                int iconLeft = itemView.getRight() - iconMargin - iconWidth;

                deleteDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                deleteDrawable.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        return callback;
    }

    /**
     * Provide event handler for the 'Add' symbol to watchlist menu action item.
     * Note: Only a short list of securities have been defined on the server side.  The list
     *       of available securities will be displayed in the Add dialog in order to
     *       make entering a valid symbol straightforward...
     *
     * @param item The menu item that was selected.
     * @return true if the selection event was handled, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        Context context = requireContext();

        if (itemId == R.id.action_add) {
            EditText et_symbol = new EditText(context);
            et_symbol.setHint(R.string.add_symbol_hint);

            // Fetch the list of existing security symbols on the server-side...
            String definedSymbols = getResources().getString(R.string.defined_symbols);

            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle(R.string.add_symbol)
                    .setMessage("\nChoose among: " + definedSymbols + "\n")
                    .setView(et_symbol)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String symbol = et_symbol.getText().toString().trim();
                            if (! symbol.isEmpty()) {
                                addSymbol(symbol);
                            }
                            else {
                                Toast.makeText(requireContext(), R.string.field_cannot_be_blank, Toast.LENGTH_SHORT).show();
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

            // let Android know we dealt with the menu item being clicked...
            return true;
        }

        // if it was not a menu item handle by this fragment, super returns false here...
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sends a request to add a symbol to a watchlist to the watchlist server (using Retrofit).
     * Symbol must be defined on the server side and must not already be present in the
     * current watchlist.
     *
     * @param symbol The symbol of the security to add.
     */
    private void addSymbol(String symbol) {
        WatchlistClient client = new WatchlistClient(requireContext());

        client.addSymbol(mparam_WatchlistID, symbol, new WatchlistClient.AddSymbolResponseListener() {
            @Override
            public void onResponse(Security securityAdded) {
                ((SecurityAdapter) rv_securities.getAdapter()).addItem(securityAdded);
            }

            @Override
            public void onFailure(String errorMsg) {
                // On failure, make sure we still have a context before Toasting the error message
                // (otherwise, would crash)...It is possible to have no context attached to the
                // fragment if we returned back to main screen before this call back arrives...

                MyUtils.doSafeToast(getContext(), errorMsg, TAG, "addSymbol");
            }
        });
    }

    /**
     * Sends a request to delete a symbol from the current watchlist to the watchlist server.
     *
     * @param holder viewHolder of the security/symbol to remove from the watchlist.
     */
    private void deleteSymbol(SecurityAdapter.ViewHolder holder) {

        String symbol = holder.tv_symbol.getText().toString();

        WatchlistClient client = new WatchlistClient(requireContext());

        client.deleteSymbol(mparam_WatchlistID, symbol, new WatchlistClient.BooleanResponseListener() {
            @Override
            public void onResponse(boolean success, String errorMsg) {
                if (success) {
                    ((SecurityAdapter) rv_securities.getAdapter()).removeItem(holder.getBindingAdapterPosition());
                }
                else {
                    // Force redraw of the view holder, otherwise would be stuck with delete-swipe view...
                    ((SecurityAdapter) rv_securities.getAdapter()).notifyItemChanged(holder.getBindingAdapterPosition());

                    MyUtils.doSafeToast(getContext(), errorMsg, TAG, "deleteSymbol");
                }
            }

        });

    }

    /**
     * Get content of the watchlist received at creation time as soon as we get foreground focus.
     */
    @Override
    public void onResume() {
        super.onResume();
        getWatchlistSecurities(mparam_WatchlistID);
    }

    /**
     * Send a request to retrieve all securities in the specified watchlist to the server.
     *
     * @param watchlistID  Watchlist ID to retrieve
     */
    private void getWatchlistSecurities(Long watchlistID) {
        WatchlistClient client = new WatchlistClient(requireContext());

        client.getWatchlistSecurities(watchlistID, new WatchlistClient.GetWatchlistSecuritiesResponseListener() {
            @Override
            public void onResponse(List<Security> list) {
                SecurityHeaderAdapter headerSecurityAdapter = new SecurityHeaderAdapter(requireActivity(), null,
                        getResources().getConfiguration().orientation);
                rv_headings.setAdapter(headerSecurityAdapter);

                SecurityAdapter securityAdapter = new SecurityAdapter(requireActivity(), list);
                rv_securities.setAdapter(securityAdapter);
            }

            @Override
            public void onFailure(String errorMsg) {
                MyUtils.doSafeToast(getContext(), errorMsg, TAG, "getWatchlistSecurities");
            }
        });

    }

    /**
     * Reset our recycler views when fragment loses foreground focus (in order not to keep
     * fetching quotes for invisible securities).
     */

    @Override
    public void onPause() {
        super.onPause();

        rv_headings.setAdapter(null);
        rv_securities.setAdapter(null);   // will cause Adapter's onDetachedFromRecyclerView to be called
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
  }