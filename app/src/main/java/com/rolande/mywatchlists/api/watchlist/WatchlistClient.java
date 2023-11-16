package com.rolande.mywatchlists.api.watchlist;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import com.rolande.mywatchlists.api.APIClient;
import com.rolande.mywatchlists.model.beans.Security;
import com.rolande.mywatchlists.model.beans.Watchlist;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Client class responsible for all network interactions with the Watchlist Service.
 *
 * @author Rolande
 */

public class WatchlistClient extends APIClient {
    final static String TAG = LOG_TAG_PREFIX + WatchlistClient.class.getSimpleName();

    public WatchlistClient(Context context) {
        super(context, TAG);
    }

    /**
     * Callback interface to pass on results of the getWatchlists request
     */
    public interface GetWatchlistsResponseListener {
        void onResponse(List<Watchlist> list);

        void onFailure(String errorMsg);
    }

    /**
     * Get the list of existing watchlists from the Watchlist server.
     *
     * @param listener Object implementing the response's callback interface
     */
    public void getWatchlists(GetWatchlistsResponseListener listener) {
        Call<WatchlistsAPIResponse> call = new WatchlistAPIBuilder().build().getWatchlists();

        call.enqueue(new Callback<WatchlistsAPIResponse>() {

            @Override
            public void onResponse(Call<WatchlistsAPIResponse> call, Response<WatchlistsAPIResponse> response) {
                if (response.body() != null) {
                    List<Watchlist> watchlists = new ArrayList<>();     // set a non-null empty list to start with

                    if (response.body().list != null) {
                        watchlists.addAll(response.body().list);
                    }

                    listener.onResponse(watchlists);
                }
                else {
                    String errorMsg = getUIErrorMessage("Get Watchlists", response.code(), response.errorBody());
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<WatchlistsAPIResponse> call, Throwable t) {
                logOnFailureMessage("getWatchlists", null, t);

                String errorMsg = "** Error connecting to Watchlist API -- Verify Settings";
                listener.onFailure(errorMsg);
            }
        });

    }

    /**
     * Callback interface to pass on results of the addWatchlist request
     */
    public interface AddWatchlistResponseListener {
        void onResponse(Watchlist watchlistAdded);
        void onFailure(String errorMsg);
    }

    /**
     * Sends a request to add a new watchlist with given name to the watchlist server.
     * Add request will be denied if a watchlist by the same name already exists.
     * Watchlist names are case insensitive.
     *
     * @param name Name of the watchlist to add.
     * @param listener Object implementing the response's callback interface
     */
    public void addWatchlist(String name, AddWatchlistResponseListener listener) {
        Watchlist watchlist = new Watchlist(0L, name, new Date(), 0);

        Call<Watchlist> call = new WatchlistAPIBuilder().build().addWatchlist(watchlist);

        call.enqueue(new Callback<Watchlist>() {
            @Override
            public void onResponse(Call<Watchlist> call, Response<Watchlist> response) {

                // if body is valid, get the new Watchlist object
                if (response.body() != null) {
                    Watchlist watchlistAdded = response.body();
                    listener.onResponse(watchlistAdded);
                }
                else {
                    // A null body means failure, as newly added watchlist would be there if success
                    String errorMsg = getUIErrorMessage("Add", response.code(), response.errorBody());
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Watchlist> call, Throwable t) {
                logOnFailureMessage("addWatchlist", name, t);

                String errorMsg = "** Error adding list to API";
                listener.onFailure(errorMsg);
            }
        });
    }

    /**
     * Callback interface to pass on results of requests that only return an HTTP status code
     * that indicates success or failure. Such is the case for updateWatchlist, deleteWatchlist and
     * deleteSymbol requests.
     */
    public interface BooleanResponseListener {
        void onResponse(boolean success, String errorMsg);
    }

    /**
     * Sends a request to update a given watchlist to the server.
     *
     * @param newWatchlist Watchlist object to update
     * @param listener Object implementing the response's callback interface
     */
    public void updateWatchlist(Watchlist newWatchlist, BooleanResponseListener listener) {

        Call<Void> call = new WatchlistAPIBuilder().build().updateWatchlist(newWatchlist);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onResponse(true, null);
                }
                else {
                    String errorMsg = getUIErrorMessage("Update", response.code(), response.errorBody());
                    listener.onResponse(false, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String args = newWatchlist.getId() + "," + newWatchlist.getName();
                logOnFailureMessage("updateWatchlist", args, t);

                String errorMsg = "** Error updating List from API";
                listener.onResponse(false, errorMsg);
            }
        });

    }

    /**
     * Sends a request to delete the specified watchlist to the server.
     *
     * @param watchlistId ID of the watchlist to delete
     * @param listener Object implementing the response's callback interface
     */
    public void deleteWatchlist(long watchlistId, BooleanResponseListener listener) {

        Call<Void> call = new WatchlistAPIBuilder().build().deleteWatchlist(watchlistId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onResponse(true, null);
                }
                else {
                    String errorMsg = getUIErrorMessage("Delete", response.code(), response.errorBody());
                    listener.onResponse(false, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                logOnFailureMessage("deleteWatchlist", String.valueOf(watchlistId), t);

                String errorMsg = "** Error deleting list from API";
                listener.onResponse(false, errorMsg);
            }
        });

    }

    /**
     * Callback interface to pass on results of the getWatchlistSecurities request
     */
    public interface GetWatchlistSecuritiesResponseListener {
        void onResponse(List<Security> list);
        void onFailure(String errorMsg);
    }

    /**
     * Sends a request to obtain the list of securities of a given watchlist.
     *
     * @param watchlistId ID of the watchlist for which list of securities is wanted
     * @param listener Object implementing the response's callback interface
     */
    public void getWatchlistSecurities(Long watchlistId, GetWatchlistSecuritiesResponseListener listener) {
        Call<WatchlistDetailAPIResponse> call = new WatchlistAPIBuilder().build().getWatchlist(watchlistId);

        call.enqueue(new Callback<WatchlistDetailAPIResponse>() {
            @Override
            public void onResponse(Call<WatchlistDetailAPIResponse> call, Response<WatchlistDetailAPIResponse> response) {

                if (response.body() != null) {
                    List<Security> securityList = new ArrayList<>();  // provide empty security list if watchlist has none

                    if (response.body().securities != null) {
                        securityList.addAll(response.body().securities);
                    }

                    listener.onResponse(securityList);
                }
                else {
                    String errorMsg = getUIErrorMessage("Get Watchlist", response.code(), response.errorBody());
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<WatchlistDetailAPIResponse> call, Throwable t) {
                logOnFailureMessage("getWatchlistSecurities", watchlistId.toString(), t);

                String errorMsg = "** Error getting watchlist from API";
                listener.onFailure(errorMsg);
            }
        });

    }

    /**
     * Callback interface to pass on results of the addSymbol request
     */
    public interface AddSymbolResponseListener {
        void onResponse(Security securityAdded);
        void onFailure(String errorMsg);
    }

    /**
     * Sends a request to add a symbol to a given watchlist to the watchlist server.
     *
     * @param watchlistId ID of the watchlist to add to.
     * @param symbol The symbol of the security to add.
     * @param listener Object implementing the response's callback interface
     */
    public void addSymbol(Long watchlistId, String symbol, AddSymbolResponseListener listener) {
        Call<Security> call = new WatchlistAPIBuilder().build().addSecurity(watchlistId, symbol);

        call.enqueue(new Callback<Security>() {
            @Override
            public void onResponse(Call<Security> call, Response<Security> response) {
                if (response.body() != null) {
                    Security securityAdded = response.body();
                    listener.onResponse(securityAdded);
                }
                else {
                    String errorMsg = getUIErrorMessage("Add", response.code(), response.errorBody());
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Security> call, Throwable t) {
                String args = watchlistId.toString() + "," + symbol;
                logOnFailureMessage("addSymbol", args,  t);

                String errorMsg = "** Error adding symbol to list (API)";
                listener.onFailure(errorMsg);
            }
        });
    }

    /**
     * Sends a request to delete a symbol from a given watchlist to the watchlist server.
     *
     * @param watchlistId ID of the watchlist involved
     * @param symbol Symbol of the security to delete from the watchlist
     * @param listener Object implementing the response's callback interface
     */
    public void deleteSymbol(Long watchlistId, String symbol, BooleanResponseListener listener) {

        Call<Void> call = new WatchlistAPIBuilder().build().deleteSecurity(watchlistId, symbol);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    listener.onResponse(true, null);
                }
                else {
                    String errorMsg = getUIErrorMessage("Delete", response.code(), response.errorBody());
                    listener.onResponse(false, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                String args = watchlistId.toString() + "," + symbol;
                logOnFailureMessage("deleteSymbol", args,  t);

                String errorMsg = "** Error deleting symbol from list (API)";
                listener.onResponse(false, errorMsg);
            }
        });
    }

}
