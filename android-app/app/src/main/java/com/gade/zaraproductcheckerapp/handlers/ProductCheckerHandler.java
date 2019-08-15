package com.gade.zaraproductcheckerapp.handlers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.services.ZaraProductCheckerService;

public class ProductCheckerHandler {

    public static final String PRODUCTS_INFO_BROADCAST = "com.gade.zaraproductchecker.broadcastproductsinfo";
    public static final String PRODUCTS_INFO_BROADCAST_LIST = "com.gade.zaraproductchecker.broadcastproductsinfo.list";
    public static final String PRODUCTS_INFO_BROADCAST_LIST_ALWAYS = "com.gade.zaraproductchecker.broadcastproductsinfo.list.always";

    public static void startStopCheckProductsService(@NonNull Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean checkProductsInBackgroundPreference = sharedPreferences.getBoolean(context.getString(R.string.check_products_in_background), true);

        final Intent zaraProductCheckerIntent = new Intent(context, ZaraProductCheckerService.class);
        final PendingIntent zaraProductCheckerPendingIntent = PendingIntent.getService(context,  0, zaraProductCheckerIntent,
                (checkProductsInBackgroundPreference) ? PendingIntent.FLAG_CANCEL_CURRENT : PendingIntent.FLAG_UPDATE_CURRENT);

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            final int inexactFrequencyFiveMinutes = 5 * 60 * 1000;

            if (checkProductsInBackgroundPreference) {
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + inexactFrequencyFiveMinutes,
                        inexactFrequencyFiveMinutes,
                        zaraProductCheckerPendingIntent);
            } else {
                zaraProductCheckerPendingIntent.cancel();
                alarmManager.cancel(zaraProductCheckerPendingIntent);
            }
        }
    }
}
