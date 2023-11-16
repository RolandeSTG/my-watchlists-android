package com.rolande.mywatchlists.api.quote;

import com.rolande.mywatchlists.model.beans.Quote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Defines the Quote Service API Interface.
 *
 * @author Rolande
 */
public interface QuoteAPI {

    // Get quote for symbol
    @GET("quote/{symbol}")
    Call<Quote> getQuote(@Path("symbol") String symbol);

}
