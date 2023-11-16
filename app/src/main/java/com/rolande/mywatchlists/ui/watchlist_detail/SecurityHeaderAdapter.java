package com.rolande.mywatchlists.ui.watchlist_detail;

import static com.rolande.mywatchlists.Constants.GRID_LAYOUT_LANDSCAPE_SPAN_COUNT;
import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import com.rolande.mywatchlists.R;
import com.rolande.mywatchlists.model.beans.Security;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for the Headers of a Security RecyclerView.  They are used to
 * provide the proper number of headings to the Security Fragment's layout
 * (which can be either Linear of Grid) upon change in a device's orientation.
 *
 * @author Rolande
 */
public class SecurityHeaderAdapter extends RecyclerView.Adapter<SecurityHeaderAdapter.ViewHolder> {
    final static String TAG = LOG_TAG_PREFIX + SecurityHeaderAdapter.class.getSimpleName();
    private Context context;
    private List<Security> data;
    int orientation;

    public SecurityHeaderAdapter(Context context, List<Security> data, int orientation )  {
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

    public List<Security> getData() {
        return data;
    }

    public void setData(List<Security> data) {
        this.data = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_idx, tv_symbol, tv_price, tv_change, tv_percent_change;

        public ViewHolder(@NonNull View itemLayout) {
            super(itemLayout);

            tv_idx = itemLayout.findViewById(R.id.tv_idx);
            tv_symbol = itemLayout.findViewById(R.id.tv_symbol);
            tv_price = itemLayout.findViewById(R.id.tv_price);
            tv_change = itemLayout.findViewById(R.id.tv_change);
            tv_percent_change = itemLayout.findViewById(R.id.tv_percent_change);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.security_header_view, parent, false);

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

        holder.tv_idx.setText(R.string.number);                   // No.
        holder.tv_symbol.setText(R.string.symbol);             // Symbol
        holder.tv_price.setText(R.string.price);               // Price
        holder.tv_change.setText(R.string.change);             // Change
        holder.tv_percent_change.setText(R.string.percent_change);      // Chg %
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
