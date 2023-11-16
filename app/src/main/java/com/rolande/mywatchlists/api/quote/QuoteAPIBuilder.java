package com.rolande.mywatchlists.api.quote;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;

import com.rolande.mywatchlists.api.APIBuilder;
import com.rolande.mywatchlists.model.Settings;

/**
 * Builder class for the Quote API (using Okhttp & Retrofit helper libraries).
 *
 * @author Rolande
 */
public class QuoteAPIBuilder extends APIBuilder {
    final static String TAG = LOG_TAG_PREFIX + QuoteAPIBuilder.class.getSimpleName();

    /**
     * Initialize the builder with IP Address & Port specified in settings and set the service's path.
     */
    public QuoteAPIBuilder() {
        super(Settings.read().getQuoteIpAddress(), Settings.read().getQuotePort(), "", TAG);
    }

    /**
     * Build a new Retrofit QuoteAPI Proxy instance.
     *
     * @return a new proxy instance for the Quote API
     */
    public QuoteAPI build(){

        return getRetrofit().create(QuoteAPI.class);
    }

}