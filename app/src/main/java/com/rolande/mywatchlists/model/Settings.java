package com.rolande.mywatchlists.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import androidx.preference.PreferenceManager;

import com.rolande.mywatchlists.R;

/**
 * Defines the settings properties (or preferences) a user can set in this app,
 * namely the IP address and port of the services used. The settings keys and default
 * values are defined in strings.xml, in order to share them with the preferences.xml
 * file definition.
 *
 * @author Rolande
 */

public class Settings {
    private String watchlistIpAddress;
    private int watchlistPort;
    private String quoteIpAddress;
    private int quotePort;
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Settings.context = context;
    }

    public Settings() {}

    public Settings(String watchlistIpAddress, int watchlistPort, String quoteIpAddress, int quotePort) {
        this.watchlistIpAddress = watchlistIpAddress;
        this.watchlistPort = watchlistPort;
        this.quoteIpAddress = quoteIpAddress;
        this.quotePort = quotePort;
    }

    public String getWatchlistIpAddress() {
        return watchlistIpAddress;
    }

    public void setWatchlistIpAddress(String watchlistIpAddress) {
        this.watchlistIpAddress = watchlistIpAddress;
    }

    public int getWatchlistPort() {
        return watchlistPort;
    }

    public void setWatchlistPort(int watchlistPort) {
        this.watchlistPort = watchlistPort;
    }

    public String getQuoteIpAddress() {
        return quoteIpAddress;
    }

    public void setQuoteIpAddress(String quoteIpAddress) {
        this.quoteIpAddress = quoteIpAddress;
    }

    public int getQuotePort() {
        return quotePort;
    }

    public void setQuotePort(int quotePort) {
        this.quotePort = quotePort;
    }

    @Override
    public String toString() {
        return "w-IP='" + watchlistIpAddress + '\'' +
                ", w-Port=" + watchlistPort +
                ", q-IP='" + quoteIpAddress + '\'' +
                ", q-Port=" + quotePort +
                '}';
    }

    /**
     * Utility static method to get all the app' settings at once.
     *
     * @return Settings object instance created from the shared preferences.
     */
    public static Settings read() {
        Settings settings = new Settings();

        // Preferences are stored as 'String' by default by PreferenceManager...
        // So, get port values as String and not Integer...
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        Resources resources = context.getResources();

        settings.watchlistIpAddress = sp.getString(resources.getString(R.string.watchlist_api_ip_key),
                                                   resources.getString(R.string.watchlist_api_ip_default));

        settings.watchlistPort      = Integer.parseInt(sp.getString(resources.getString(R.string.watchlist_api_port_key),
                                                                    resources.getString(R.string.watchlist_api_port_default)));

        settings.quoteIpAddress     = sp.getString(resources.getString(R.string.quote_api_ip_key),
                                                   resources.getString(R.string.quote_api_ip_default));

        settings.quotePort          = Integer.parseInt(sp.getString(resources.getString(R.string.quote_api_port_key),
                                                                    resources.getString(R.string.quote_api_port_default)));

        return settings;
    }

    /**
     * Utility static method to write out all settings at once.
     * Note: Not used at the moment, kept for future reference.
     */
    public static void write(Settings settings) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        Resources resources = context.getResources();

        editor.putString(resources.getString(R.string.watchlist_api_ip_key), settings.watchlistIpAddress);
        editor.putString(resources.getString(R.string.watchlist_api_port_key), String.valueOf(settings.watchlistPort));
        editor.putString(resources.getString(R.string.quote_api_ip_key), settings.quoteIpAddress);
        editor.putString(resources.getString(R.string.quote_api_port_key), String.valueOf(settings.quotePort));

        editor.apply();
    }
}
