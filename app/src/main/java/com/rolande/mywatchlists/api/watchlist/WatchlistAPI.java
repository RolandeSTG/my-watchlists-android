package com.rolande.mywatchlists.api.watchlist;

import com.rolande.mywatchlists.model.beans.Security;
import com.rolande.mywatchlists.model.beans.Watchlist;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Defines the Watchlist Service API Interface.
 *
 * @author Rolande
 */
public interface WatchlistAPI {

    // Get all watchlists
    @GET("watchlists")
    Call<WatchlistsAPIResponse> getWatchlists();

    // Get a specific watchlist
    @GET("watchlists/{id}")
    Call<WatchlistDetailAPIResponse> getWatchlist(@Path("id") long id);

    // Add a watchlist
    @POST("watchlists")
    @Headers("Content-Type: application/json")
    Call<Watchlist> addWatchlist(@Body Watchlist watchlist);

    // Update a watchlist's name
    @PUT("watchlists")
    @Headers("Content-Type: application/json")
    Call<Void> updateWatchlist(@Body Watchlist watchlist);

    // Delete a watchlist
    @DELETE("watchlists/{id}")
    Call<Void> deleteWatchlist(@Path("id") long id);

    // Add a symbol/security to a watchlist
    @POST("watchlists/{id}/symbol/{symbol}")
    Call<Security> addSecurity(@Path("id") long id, @Path("symbol") String symbol);

    // Delete a symbol/security from a watchlist
    @DELETE("watchlists/{id}/symbol/{symbol}")
    Call<Void> deleteSecurity(@Path("id") long id, @Path("symbol") String symbol);
}
