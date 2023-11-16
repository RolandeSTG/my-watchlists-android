package com.rolande.mywatchlists.api.quote;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import com.rolande.mywatchlists.R;
import com.rolande.mywatchlists.api.APIClient;
import com.rolande.mywatchlists.model.beans.Quote;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Client class responsible for all network interactions with the Quote Service.
 *
 * @author Rolande
 */
public class QuoteClient extends APIClient {
    final static String TAG = LOG_TAG_PREFIX + QuoteClient.class.getSimpleName();

    public QuoteClient(Context context) {
        super(context, TAG);
    }

    /**
     * Callback interface to pass on results of the getQuote request
     */
    public interface GetQuoteResponseListener {
        void onResponse(Quote latestQuote);
        void onFailure(String errorMsg);
    }

    /**
     * Obtains the latest quote for a given symbol from the Quote server.
     *
     * @param symbol Symbol of the security for which a quote is wanted
     * @param listener Object implementing the response's callback interface
     */
    public void getQuote(String symbol, GetQuoteResponseListener listener) {

        Call<Quote> call = new QuoteAPIBuilder().build().getQuote(symbol);

        call.enqueue(new Callback<Quote>() {

            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {

                // if body is valid, get new quote from response body
                if (response.body() != null) {
                    Quote latestQuote = response.body();
                    listener.onResponse(latestQuote);
                }
                else {
                    String errorMsg = getUIErrorMessage("Get Quote", response.code(), response.errorBody());
                    listener.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                logOnFailureMessage("getQuote", symbol, t);

                String errorMsg = context.getResources().getString(R.string.error_msg_quote_api);
                listener.onFailure(errorMsg);
            }
        });

    }

}
