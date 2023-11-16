package com.rolande.mywatchlists.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Utility class for this app.
 *
 * @author Rolande
 */
public class MyUtils {

    /**
     * Do a 'toast' surrounded by a try-catch clause to prevent app from crashing if context
     * is null. Possible to have this situation when toasting in callbacks (activity
     * and/or fragment might be gone). If context is null, notifies the situation in the log.
     *
     * @param context Context of the toast
     * @param msg Message to toast
     * @param loggerTag Tag to use if need to log instead
     * @param inMethod Name of the method requesting to toast
     */

    public static void doSafeToast(Context context, String msg, String loggerTag, String inMethod) {
        try {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
        catch (NullPointerException e) {
            Log.e(loggerTag, "** " + inMethod + ": Cannot Toast, context is NULL");

        }
    }
}
