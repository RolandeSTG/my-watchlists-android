package com.rolande.mywatchlists.api.watchlist;

import com.google.gson.annotations.SerializedName;
import com.rolande.mywatchlists.model.beans.Watchlist;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Format of API Response when getting all watchlists.  The watchlists
 * provided are summary objects only (with their security count but no securities).
 *
 * @author Rolande
 */
public class WatchlistsAPIResponse implements Serializable {
    @SerializedName("watchlists")
    //@SerializedName("ArrayList")
    public ArrayList<Watchlist> list;
}

