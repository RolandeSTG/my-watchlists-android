package com.rolande.mywatchlists.api;

import static com.rolande.mywatchlists.Constants.LOG_TAG_PREFIX;
import static com.rolande.mywatchlists.Constants.TCP_CONNECT_TIMEOUT;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Generic API Builder class for the APIs used in this app (using Okhttp & Retrofit helper libraries).
 *
 * @author Rolande
 */
public class APIBuilder {
    private final String loggerTag;
    private String serviceIPAddress;
    private int servicePort;
    private String servicePath;

    private static Gson myGson;              // Singleton, only needing 1 instance
    private static OkHttpClient httpClient;  // Same, only needing 1 instance

    /**
     * Default constructor.
     */
    public APIBuilder() {
        this.loggerTag = LOG_TAG_PREFIX + APIBuilder.class.getSimpleName();
    }

    /**
     * Initializes the builder's IP address, port & service path.
     *
     * @param serviceIPAddress IP address of the server
     * @param servicePort TCP Port used by the server
     * @param servicePath Path of the service on the remote host.
     */
    public APIBuilder(String serviceIPAddress, int servicePort, String servicePath, String loggerTag) {
        this.serviceIPAddress = serviceIPAddress;
        this.servicePort = servicePort;
        this.servicePath = servicePath;
        this.loggerTag = loggerTag;
    }

    /**
     * Overrides the setting's IP address & port using new values.
     *
     * @param ipAddress The IP Address to use in connecting to the service.
     * @param port The TCP/IP Port to use to connect
     * @return updated builder instance.
     */
    public APIBuilder setUrl(String ipAddress, int port) {
        serviceIPAddress = ipAddress;
        servicePort = port;

        return this;
    }

    /**
     * Get a new Retrofit instance.
     *
     * @return a new Retrofit instance
     */
    public Retrofit getRetrofit() {

        OkHttpClient httpClient = getOkHttpClient();
        Gson myGson = getMyGson();
        String baseUrl = getBaseUrl();

        return getRetrofit(httpClient, myGson, baseUrl);        // private one
    }

    /**
     * Get a singleton Http Client object to use with a Retrofit builder. Notice that we changed
     * the connect timeout to 5 seconds (Retrofit default is 10).
     *
     * @return a new instance of HttpClient
     */
    private OkHttpClient getOkHttpClient() {

        if (httpClient == null) {
            // Log HTTP Requests/Responses...
            HttpLoggingInterceptor logger = new HttpLoggingInterceptor();

            // logger.setLevel(HttpLoggingInterceptor.Level.BASIC);
            logger.setLevel(HttpLoggingInterceptor.Level.BODY);

            httpClient = new OkHttpClient.Builder()
                    .addInterceptor(logger)                              // add logger
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            return chain.proceed(
                                    chain.request().newBuilder().build()
                            );
                        }
                    })
                    .connectTimeout(TCP_CONNECT_TIMEOUT, TimeUnit.SECONDS)       // 4 seconds, instead of default 10
                    .build();
        }

        return httpClient;
    }

    /**
     *  Define a singleton custom Gson serializer/deserializer for Dates, as we want the java
     *  Date/Timestamp objects to be serialized using the "yyy-MM-dd'T'HH:mm:ssZ" pattern.
     *
     *  Note: Pattern provided must abide by the SimpleDateFormat class convention.
     *
     * @return a new instance of Gson.
     */
    private Gson getMyGson() {
        if (myGson == null) {
            myGson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                    .create();
        }

        return myGson;
    }

    /**
     * Get the resulting base URL string from internal properties (ip & port) currently set.
     * May be overriden by subclasses.
     *
     * Note: Regular pattern is 'http://{ipAddress}:{port}/'
     *
     * @return Resulting base URL string
     */
    private String getBaseUrl() {
        // Server's current IP = 192.168.1.146
        // - quote microservice = port 8500; baseUrl("http://192.168.1.146:8500/")
        // - watchlist service = port 8080, baseUrl("http://192.168.1.146:8080/watchlist-service/")

        String baseUrlString = "http://" + serviceIPAddress + ":" + servicePort + servicePath + "/";

        //Log.i(loggerTag, "API baseUrl = " + baseUrlString);

        return baseUrlString;
    }

    /**
     * Get a Retrofit instance.
     *
     * @param httpClient OkHttpClient instance to use
     * @param myGson Gson converter factory object to use
     * @param baseUrlString Base URL String to use with the service implementation
     *
     * @return new Retrofit instance.
     */
    private Retrofit getRetrofit(OkHttpClient httpClient, Gson myGson, String baseUrlString) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrlString)
                .addConverterFactory(GsonConverterFactory.create(myGson))
                .client(httpClient)
                .build();

        return retrofit;
    }

}