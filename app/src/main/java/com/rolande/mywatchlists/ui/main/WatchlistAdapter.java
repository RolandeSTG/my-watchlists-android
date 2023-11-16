package com.rolande.mywatchlists.ui.main;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rolande.mywatchlists.R;
import com.rolande.mywatchlists.model.beans.Watchlist;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Adapter class for Watchlists handled by a RecyclerView. Defines an inner
 * interface for listeners to be notified upon click, update and delete events
 * regarding a data item.
 *
 * @author Rolande
 */
public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.ViewHolder> {
    final static String TAG = LOG_TAG_PREFIX + WatchlistAdapter.class.getSimpleName();
    private Context context;
    private List<Watchlist> data;
    private Listener listener;

    /**
     * Listener interface for those wishing to be notified upon the following events...
     */
    interface Listener {
        void onClick(int position);
        void onUpdate(int position, String newName);
        void onDelete(int position);
    }

    /**
     * Constructor for this adapter, specifying that it also has stable IDs (provided by the Watchlist Service)
     *
     * @param context Context of the calling activity/fragment
     * @param data List of watchlist objects
     */
    public WatchlistAdapter(Context context, List<Watchlist> data) {
        this.context = context;
        this.data = data;

        this.setHasStableIds(true);   // i.e. watchlist-id are provided by server
    }

    /**
     * Set a listener to this adatper's interface.
     *
     * @param listener Instance of a Listener
     */
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    /**
     *  ViewHolder of a Watchlist
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_list_name, tv_date_created, tv_count;
        ImageButton imgBtn_delete, imgBtn_edit;
        public ViewHolder(@NonNull View itemLayout) {
            super(itemLayout);

            tv_list_name    = itemLayout.findViewById(R.id.tv_list_name);
            tv_count        = itemLayout.findViewById(R.id.tv_count);
            tv_date_created = itemLayout.findViewById(R.id.tv_date_created);
            imgBtn_edit     = itemLayout.findViewById(R.id.img_btn_edit);
            imgBtn_delete   = itemLayout.findViewById(R.id.img_btn_delete);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_item_view, parent, false);

        return new ViewHolder(v);
    }

    /**
     * Binds a Watchlist object in the adapter's data set to its view holder.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Watchlist watchlist = data.get(position);

        holder.tv_list_name.setText(watchlist.getName());
        holder.tv_count.setText("(" + String.valueOf(watchlist.getNumberOfSecurities()) + ")");

        if (watchlist.getDateCreated() == null) {
            watchlist.setDateCreated(new Timestamp(System.currentTimeMillis()));
        }

        // Show date created using <Oct 2, 2023> format
        String dateCreated = new SimpleDateFormat("MMM d, yyyy").format(watchlist.getDateCreated());
        holder.tv_date_created.setText(dateCreated);

        // Edit button's onClick listener
        holder.imgBtn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getBindingAdapterPosition();

                EditText et_listName = new EditText(context);
                et_listName.setText(holder.tv_list_name.getText());
                et_listName.requestFocus();

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle(R.string.rename_list)
                        .setView(et_listName)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String listName = et_listName.getText().toString().trim();
                                if (listener != null) {
                                    listener.onUpdate(pos, listName);
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

        // Delete button's onClick listener
        holder.imgBtn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getBindingAdapterPosition();
                String listName = holder.tv_list_name.getText().toString();

                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("Delete list [" + listName + "] ? ")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (listener != null) {
                                    listener.onDelete(pos);
                                }
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .create();

                alertDialog.show();
            }
        });

        // An item's general onClick listener (if clicked anywhere else)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(holder.getBindingAdapterPosition());
                }
            }
        });
    }

    /**
     * Get the number of items in adapter's data set
     *
     * @return number of items
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Get the watchlist instance at position queried.
     *
     * @param position Position of the instance in the adapter's data set.
     * @return Watchlist instance at the given position
     */
    public Watchlist getItem(int position) {
         return data.get(position);
    }

    /**
     * Returns the ID of the watchlist object at requested position.
     *
     * @param position Adapter position to query
     * @return ID of the watchlist at this position.
     */
    @Override
    public long getItemId(int position) {
        Watchlist watchlist = data.get(position);

        return watchlist.getId();
    }

    /**
     * Get the name of the watchlist at a given position in the data set.
     *
     * @param position Position of the watchlist queried
     * @return name of the watchlist
     */
    public String getItemName(int position) {
        Watchlist watchlist = data.get(position);

        return watchlist.getName();
    }

    /**
     * Add a watchlist instance at the end of the data set.
     *
     * @param w Watchlist instance to add.
     */
    public void addItem(Watchlist w) {
        data.add(w);
        this.notifyItemInserted(data.size()-1);
    }

    /**
     * Update (the name of) a watchlist instance at position specified.
     *
     * @param position Position of the watchlist to update
     * @param newName The new name to assign to this watchlist.
     */
    public void updateItem(int position, String newName) {
        data.get(position).setName(newName);
        this.notifyItemChanged(position);
    }

    /**
     * Remove a watchlist item from the adapter's data set.
     * @param position Position of the instance to remove
     */
    public void removeItem(int position) {
        data.remove(position);
        this.notifyItemRemoved(position);
    }

}
