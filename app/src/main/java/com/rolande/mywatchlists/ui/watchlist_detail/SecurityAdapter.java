package com.rolande.mywatchlists.ui.watchlist_detail;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rolande.mywatchlists.R;
import com.rolande.mywatchlists.api.quote.QuoteClient;
import com.rolande.mywatchlists.model.beans.Quote;
import com.rolande.mywatchlists.model.beans.Security;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter class for Securities handled by a RecyclerView.
 *
 * @author Rolande
 */
public class SecurityAdapter extends RecyclerView.Adapter<SecurityAdapter.ViewHolder> {
    final static String TAG = LOG_TAG_PREFIX + SecurityAdapter.class.getSimpleName();
    private Context context;
    private List<Security> data;

    public SecurityAdapter(Context context, List<Security> data)  {
        this.context = context;
        this.data = data;
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

    /**
     *  ViewHolder of a Security (and its associated quote)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_idx, tv_symbol, tv_price, tv_change, tv_percent_change;
        TextView tv_company_name, tv_last_trade_timestamp, tv_bid_price, tv_bid_volume;
        TextView tv_ask_price, tv_ask_volume;
        CardView card_view;
        Runnable fetchQuoteRunnable;

        public ViewHolder(@NonNull View itemLayout) {
            super(itemLayout);

            card_view = itemLayout.findViewById(R.id.card_view);

            tv_idx = itemLayout.findViewById(R.id.tv_idx);
            tv_symbol = itemLayout.findViewById(R.id.tv_symbol);
            tv_price = itemLayout.findViewById(R.id.tv_price);
            tv_change = itemLayout.findViewById(R.id.tv_change);
            tv_percent_change = itemLayout.findViewById(R.id.tv_percent_change);
            tv_company_name = itemLayout.findViewById(R.id.tv_company_name);
            tv_last_trade_timestamp = itemLayout.findViewById(R.id.tv_last_trade_timestamp);
            tv_bid_price = itemLayout.findViewById(R.id.tv_bid_price);
            tv_bid_volume = itemLayout.findViewById(R.id.tv_bid_volume);
            tv_ask_price = itemLayout.findViewById(R.id.tv_ask_price);
            tv_ask_volume = itemLayout.findViewById(R.id.tv_ask_volume);

            fetchQuoteRunnable = null;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.security_item_view, parent, false);

        return new ViewHolder(v);
    }

    /**
     * Binds the view with specified payload, if any...Used to update the 'quote' view portion
     * of a security, and only that.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full
     *                 update.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        // Log.i(TAG, "onBindViewHolder(payload): Pos(" + position + ") = " + position + ", payloads = " + payloads );

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);        // full update
            return;
        }

        // Else, we got a payload, do partial update only
        Security security = data.get(position);
        Quote oldQuote = security.getQuote();

        Quote newQuote = (Quote) payloads.get(0);

        bindQuote(holder, position, newQuote);           // binds the new quote only...
        security.setQuote(newQuote);
    }

    /**
     * Binds the view holder with security info & its latest quote.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Security security = data.get(position);

        // Log.i(TAG, "onBindViewHolder: Pos(" + position + ") = " + security.getSymbol() );

        // Bind the security info portion
        holder.tv_idx.setText(String.format("%2s", String.valueOf(position + 1)));   // position: 0-based; we want 1-based
        holder.tv_symbol.setText(security.getSymbol());
        holder.tv_company_name.setText(String.format("%.15s", security.getName()));

        // Bind the quote portion
        bindQuote(holder, position, security.getQuote());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Detail security info not implemented, inform how to delete instead ...
                Toast.makeText(context, R.string.onclick_msg, Toast.LENGTH_SHORT).show();
            }
        });

        // define a runnable to fetch a new quote at random intervals...
        holder.fetchQuoteRunnable = new Runnable() {
            public void run() {
                int pos = holder.getBindingAdapterPosition();

                //String logMsg = "run() Handler: Pos(" + pos + ") = " +  holder.tv_symbol.getText();

                // if we have no position (i.e. pos = -1), symbol has been deleted, so stop fetching quote for it
                if (pos != RecyclerView.NO_POSITION) {
                    fetchAPIQuote(data.get(pos));

                    long randomDelay = getRandomFetchDelay();
                    // Log.i(TAG, logMsg + " --> Reposted(delay = " + randomDelay/1000 + " sec)");
                    holder.itemView.postDelayed(this, randomDelay);    // fetch new quote in 'random' seconds
                }
                else {
                    //Log.w(TAG, logMsg + "--> NOT Reposted (-1)");
                }
            }
        };

    }

    /**
     * Binds a quote instance into its view holder.
     *
     * @param holder View holder of the security's quote to update
     * @param position Position of the view holder's data in the adapter's data set.
     * @param quote The new quote instance to use for updating the view
     */
    private void bindQuote(@NonNull ViewHolder holder, int position, Quote quote) {

        // Animate the change on latest trading price, to see which one has changed among the list...
        holder.tv_price.setText(String.format("%.2f", quote.getLastTradePrice()));
        holder.tv_price.startAnimation(AnimationUtils.loadAnimation(holder.tv_price.getContext(), R.anim.price_change));

        double priceChange = quote.getLastTradePrice() - quote.getPreviousClosePrice();
        holder.tv_change.setText(String.format("%.2f", priceChange));
        holder.tv_change.setTextColor(priceChange < 0.0 ? Color.RED : getContext().getResources().getColor(R.color.sea_green,null));

        // Make sure we have a previousClosePrice in order NOT to divide by 0...
        double percentChange = (quote.getPreviousClosePrice() > 0) ? (priceChange / quote.getPreviousClosePrice()) : (0);
        holder.tv_percent_change.setText(String.format("%.2f %%", percentChange * 100));
        holder.tv_percent_change.setTextColor(holder.tv_change.getCurrentTextColor());

        if (quote.getLastTradeTimestamp() == null) {
            quote.setLastTradeTimestamp(new Timestamp(System.currentTimeMillis()));
        }

        // Show time of last trade using <hours:minutes:seconds AM/PM> format
        String time = new SimpleDateFormat("h:mm:ss aa").format(quote.getLastTradeTimestamp());
        holder.tv_last_trade_timestamp.setText(time);

        // Show bid & ask volumes as number of lots (a lot being 100 shares) to make it lighter on the eye.
        holder.tv_bid_price.setText(String.format("%.2f", quote.getBidPrice()));
        holder.tv_bid_volume.setText(String.valueOf(quote.getBidVolume() / 100));
        holder.tv_ask_price.setText(String.format("%.2f", quote.getAskPrice()));
        holder.tv_ask_volume.setText(String.valueOf(quote.getAskVolume() / 100));
    }

    /**
     * Fetch the latest quote from the Quote Service for a given security.
     *
     * @param s Security instance for which to fetch a new quote.
     */
    private void fetchAPIQuote(Security s) {

        QuoteClient client = new QuoteClient(context);

        client.getQuote(s.getSymbol(), new QuoteClient.GetQuoteResponseListener() {
            @Override
            public void onResponse(Quote latestQuote) {
                //Log.i(TAG, "New quote = " + latestQuote);
                updateSecurityQuote(s.getSymbol(), latestQuote);
            }

            @Override
            public void onFailure(String errorMsg) {
                // Update the security's quote to zeroes (with empty quote), if couldn't get a new one
                updateSecurityQuote(s.getSymbol(), new Quote());

                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * Update a security's latest quote information, by notifying the adapter to update its view.
     *
     * @param symbol Symbol of the security involved.
     * @param newQuote New quote information to assign to the security.
     */
    private void updateSecurityQuote(String symbol, Quote newQuote) {

        int position = getSecurityPosition(symbol);

        if (position >= 0) {
            notifyItemChanged(position, newQuote);        // newQuote = Payload

            //Log.i(TAG, "Security (" + symbol + "): Updated with New Quote");
        }
        else {
            //Log.w(TAG, "Security (" + symbol + "): Removed -- No quote update needed");
        }

    }

    /**
     * Find the position of a security in the adapter using its symbol.
     *
     * @param symbol Symbol of the security
     * @return position of the security if found, -1 otherwise (i.e. NO_POSITION)
     */
    private int getSecurityPosition(String symbol) {
        int position = -1;

        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getSymbol().compareTo(symbol) == 0) {
                position = i;
                break;
            }
        }

        return position;
    }

    /**
     * Enable callback to fetch quote when a security is visible by end-user.
     *
     * @param holder Holder of the view being attached
     */
    @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        long randomDelay = getRandomFetchDelay();
        //Log.i(TAG, "onViewAttachedToWindow(): Enabling callback for (" + holder.tv_symbol.getText() + "), delay = " + randomDelay/1000 + " secs");

        holder.itemView.postDelayed(holder.fetchQuoteRunnable, randomDelay);
    }

    /**
     * Generate a random delay to fetch a new quote for a security.  Random delay is set
     * to be between 1 and 10 seconds, to mimic real-time markets.
     *
     * @return delay generated, in milliseconds   (ex: 8000 for 8 seconds)
     */
    private long getRandomFetchDelay(){
        long randomDelay = (1 + (long) (Math.random() * 10)) * 1000; // x 1000 milliseconds = 1 sec

        return randomDelay;
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        //Log.i(TAG, "onViewRecycled(): Was used for = (" + holder.tv_symbol.getText() + ")");
     }

    /**
     * Remove callback for fetching quote when a given security is not in end-user's view anymore.
     *
     * @param holder Holder of the view being detached
     */
    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        //Log.i(TAG, "onViewDetachedFromWindow(): Removing callback for (" + holder.tv_symbol.getText() + ")");

        holder.itemView.removeCallbacks(holder.fetchQuoteRunnable);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        // Not called if going back to main watchlist activity
        // Log.i(TAG, "onDetachedFromRecyclerView()");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addItem(Security security) {
        data.add(security);
        notifyItemInserted(data.size()-1);
    }

    public void removeItem(int position) {
        Log.i(TAG, "Security (" + data.get(position).getSymbol() + "): Removed");

        data.remove(position);
        //  notifyItemRemoved(position);
        notifyDataSetChanged();                 // Need this one to get position numbers refreshed properly
    }

}
