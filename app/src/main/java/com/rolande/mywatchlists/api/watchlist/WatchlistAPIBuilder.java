package com.rolande.mywatchlists.api.watchlist;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import com.rolande.mywatchlists.api.APIBuilder;
import com.rolande.mywatchlists.model.Settings;

/**
 * Builder class for the Watchlist API.
 *
 * @author Rolande
 */
public class WatchlistAPIBuilder extends APIBuilder {
    final static String TAG = LOG_TAG_PREFIX + WatchlistAPIBuilder.class.getSimpleName();

    /**
     * Initialize the builder with IP Address & port specified in settings, along with its service path.
     */
    public WatchlistAPIBuilder() {
        super(Settings.read().getWatchlistIpAddress(), Settings.read().getWatchlistPort(), "/watchlist-service", TAG);
    }

    /**
     * Build a new Retrofit WatchlistAPI Proxy instance.
     *
     * @return a new proxy instance for the Watchlist API
     */
    public WatchlistAPI build() {

        return getRetrofit().create(WatchlistAPI.class);
    }

}
