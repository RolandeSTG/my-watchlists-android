package com.rolande.mywatchlists.api.watchlist;

import com.google.gson.annotations.SerializedName;
import com.rolande.mywatchlists.model.beans.Security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Mapping of a single Watchlist API Response from JSON to Java.
 * Format of the Response includes all securities associated with a watchlist.
 *
 * @author Rolande
 */

public class WatchlistDetailAPIResponse implements Serializable {
    @SerializedName("_id")
    public Long id;

    @SerializedName("name")
    public String name;

    @SerializedName("date_created")
    public Date dateCreated;

    @SerializedName("securities")
    public ArrayList<Security> securities;

    @SerializedName("environment")
    private String environment;
}
