package com.rolande.mywatchlists.ui.main;

import static com.rolande.mywatchlists.Constants.GRID_LAYOUT_LANDSCAPE_SPAN_COUNT;
import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rolande.mywatchlists.R;
import com.rolande.mywatchlists.model.beans.Watchlist;

import java.util.List;

/**
 * Adapter class for the Headers of a Watchlist RecyclerView.  They are used to
 * provide the proper number of headings to the Main (Watchlist) Activity's layout
 * (which can be either Linear of Grid) upon change in a device's orientation.
 *
 * @author Rolande
 */
public class WatchlistHeaderAdapter extends RecyclerView.Adapter<WatchlistHeaderAdapter.ViewHolder> {
    final static String TAG = LOG_TAG_PREFIX + WatchlistHeaderAdapter.class.getSimpleName();
    private Context context;
    private List<Watchlist> data;

    int orientation;

    public WatchlistHeaderAdapter(Context context, List<Watchlist> data, int orientation )  {
        this.context = context;
        this.data = data;
        this.orientation = orientation;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Watchlist> getData() {
        return data;
    }

    public void setData(List<Watchlist> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name_label, tv_created_label;

        public ViewHolder(@NonNull View itemLayout) {
            super(itemLayout);

            tv_name_label = itemLayout.findViewById(R.id.tv_name_label);
            tv_created_label = itemLayout.findViewById(R.id.tv_created_label);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_header_view, parent, false);

        return new ViewHolder(v);
    }

    /**
     * Binds a single heading definition to their respective columns, multiple times if needed.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Bind the labels to their columns

        holder.tv_name_label.setText(R.string.name);            // Name
        holder.tv_created_label.setText(R.string.created);      // Created
    }

    /**
     * Returns the number of headings to provide to the layout.
     *
     * @return 1 for portrait orientation/linear layout, 2 for landscape/grid layout.
     */
    @Override
    public int getItemCount() {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return GRID_LAYOUT_LANDSCAPE_SPAN_COUNT;                       // 2 columns
        }

        return 1;
    }

}
