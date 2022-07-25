package com.gade.zaraproductcheckerapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.notifications.CheckProductsNotificationManager;
import com.gade.zaraproductcheckerapp.receivers.ZaraProductCheckerReceiver;

import java.util.Optional;

import static com.gade.zaraproductcheckerapp.services.ZaraProductCheckerJobIntentService.PRODUCTS_INFO_REQUEST_RESULT_DATA_ALWAYS;
import static java.lang.Boolean.TRUE;

public class ZaraProductCheckerJobIntentServiceHandler {

    private static final long CHECKER_INTERVAL_MS = 5 * 60 * 1000; // First value indicate the minutes
    private static final CheckProductsNotificationManager checkProductsNotificationManager = new CheckProductsNotificationManager();

    public static void startNowBackground(@NonNull final Context context) {
        final Intent zaraProductCheckerIntent = new Intent(context, ZaraProductCheckerJobIntentService.class);
        zaraProductCheckerIntent.putExtra(PRODUCTS_INFO_REQUEST_RESULT_DATA_ALWAYS, TRUE);
        ZaraProductCheckerJobIntentService.enqueueWork(context, zaraProductCheckerIntent);
    }

    public static void startOrStopPeriodicallyBackground(@NonNull final Context context) {
        getAlarmManager(context).ifPresent(alarmManager -> processRequest(alarmManager, context));
    }

    private static void processRequest(final AlarmManager alarmManager, final Context context) {
        stop(alarmManager, context);

        if (checkProductsInBackground(context)) {
            final long triggerAtMillis = System.currentTimeMillis() + CHECKER_INTERVAL_MS;
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, createPendingIntent(context));
            checkProductsNotificationManager.display(context);
        } else {
            checkProductsNotificationManager.remove(context);
        }
    }

    private static Optional<AlarmManager> getAlarmManager(final Context context) {
        return Optional.ofNullable((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
    }

    private static void stop(final AlarmManager alarmManager, final Context context) {
        alarmManager.cancel(createPendingIntent(context));
    }

    private static PendingIntent createPendingIntent(final Context context) {
        final Intent intent = new Intent(context, ZaraProductCheckerReceiver.class);
        intent.setAction("com.gade.zaraproductcheckerapp.intent.action.PRODUCT_CHECKER");
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private static boolean checkProductsInBackground(final Context context) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.check_products_in_background), true);
    }
}
