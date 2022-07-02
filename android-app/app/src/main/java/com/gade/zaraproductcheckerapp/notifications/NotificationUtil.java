package com.gade.zaraproductcheckerapp.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.gade.zaraproductcheckerapp.activities.MainActivity;

public class NotificationUtil {

    public static final String NOTIFICATIONS_INTENT_EXTRA_DATA_NAME = "notificationsExtraDataName";

    public static NotificationManagerCompat getNotificationManager(@NonNull Context context) {
        return NotificationManagerCompat.from(context);
    }

    static PendingIntent generateOpenMainActivityPendingIntent(@NonNull final Context context, final int requestCode, final Integer extraData) {
        final Intent mainActivityIntent = new Intent(context, MainActivity.class);
        if (extraData != null) {
            mainActivityIntent.putExtra(NOTIFICATIONS_INTENT_EXTRA_DATA_NAME, extraData);
        }
        return PendingIntent.getActivity(context, requestCode, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }
}
