package com.rolande.mywatchlists.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

public class APIClient {
    private final String loggerTag;

    protected Context context;

    protected APIClient(Context context, String loggerTag) {
        this.context = context;
        this.loggerTag = loggerTag;
    }

    /**
     * Get an error message suitable for UI display.  Will take server's error message, if one
     * was provided. If not, a default one will be generated.
     *
     * @param serviceCalled Name of the service that was called
     * @param httpCode HTTP response status code
     * @param errorBody See okhttp3.ResponseBody
     * @return error message suitable for end-user display
     */
    protected String getUIErrorMessage(String serviceCalled, int httpCode, okhttp3.ResponseBody errorBody) {
        String errorMsg = "";

        if (errorBody != null) {
            errorMsg = getServerErrorMessage(errorBody);
        }

        // if no error message from server, provide a default one...
        if (errorMsg.isEmpty()) {
            errorMsg = "** " + serviceCalled + " failed (HTTP " + httpCode + ")";
        }

        Log.w(loggerTag, "UI msg = " + errorMsg);

        return errorMsg;
    }

    /**
     * Extract the error message from the server's response, if any. This message is
     * intended for end-user and is in json.
     *
     * Message format: { "message":"error message"}
     *
     * @param errorBody See okhttp3.ResponseBody
     * @return error message extracted, if any.
     */
    protected String getServerErrorMessage(okhttp3.ResponseBody errorBody) {

        String errorMsg = "";

        try {
            String jsonString = errorBody.string();

            JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);

            // 'message' is the key...
            errorMsg = jsonObject.get("message").toString();
        }
        catch (JsonSyntaxException | NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return errorMsg;
    }

    /**
     * Format a Retrofit callback 'onFailure' message, so that they all have the same
     * format and structure in logs.
     *
     * @param method Name of the method that failed
     * @param args arguments of the method called, as a single string
     * @param t Exception object (throwable)
     * @return formatted 'onFailure' message
     */
    protected void logOnFailureMessage(String method, String args, Throwable t) {

        Log.e(loggerTag, String.format("%s(%s).onFailure(): %s", method, (args != null ? args : ""), t.getMessage() ));

    }

}
