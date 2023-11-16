package com.rolande.mywatchlists;

/**
 * A class to define constants used by this app.
 *
 * @author Rolande
 */
public class Constants {
    // Tag prefix used for Logcat, to allow quick filtering of this app's messages only
    public  final static String LOG_TAG_PREFIX = "Log_";

    // HTTP Connect Timeout in Retrofit
    public  final static long TCP_CONNECT_TIMEOUT = 4;          // 4 seconds (instead of 10)

    // Keys used to communicate from main (watchlist) activity to detail activity
    public final static String WATCHLIST_ID_KEY = "watchlist-ID";
    public final static String WATCHLIST_NAME_KEY = "watchlist-name";

    // GridLayout column/span count, for Landscape orientation
    public final static int GRID_LAYOUT_LANDSCAPE_SPAN_COUNT = 2;
}
