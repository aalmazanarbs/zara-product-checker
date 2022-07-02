package com.gade.zaraproductcheckerapp.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.gade.zaraproductcheckerapp.R;

import static android.app.NotificationManager.IMPORTANCE_LOW;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.gade.zaraproductcheckerapp.notifications.NotificationUtil.generateOpenMainActivityPendingIntent;
import static com.gade.zaraproductcheckerapp.notifications.NotificationUtil.getNotificationManager;

public class CheckProductsNotificationManager {

    private static final int OPERATIONS_ID = -1;

    private final static String CHECK_PRODUCTS_NOTIFICATIONS_CHANNEL_ID = "CHECK_PRODUCTS_NOTIFICATIONS_CHANNEL_ID";
    private final static String CHECK_PRODUCTS_PRODUCT_NOTIFICATIONS_GROUP_KEY = "CHECK_PRODUCTS_PRODUCT_NOTIFICATIONS_GROUP_KEY";

    public void display(@NonNull final Context context) {
        final NotificationManagerCompat notificationManagerCompat = getNotificationManager(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createOrUpdateNotificationChannel(notificationManagerCompat, context);
        }

        final Notification notification = new NotificationCompat.Builder(context, CHECK_PRODUCTS_NOTIFICATIONS_CHANNEL_ID)
                .setGroup(CHECK_PRODUCTS_PRODUCT_NOTIFICATIONS_GROUP_KEY)
                .setOngoing(false)
                .setAutoCancel(false)
                .setContentIntent(generateOpenMainActivityPendingIntent(context, OPERATIONS_ID, null))
                .setContentText(context.getString(R.string.checking_changes_products))
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(PRIORITY_MIN)
                .setShowWhen(false)
                .build();

        notification.flags |= NotificationCompat.FLAG_NO_CLEAR | NotificationCompat.FLAG_ONGOING_EVENT;

        notificationManagerCompat.notify(OPERATIONS_ID, notification);
    }

    public void remove(@NonNull final Context context) {
        getNotificationManager(context).cancel(OPERATIONS_ID);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createOrUpdateNotificationChannel(final NotificationManagerCompat notificationManagerCompat, final Context context) {
        final NotificationChannel notificationChannel = new NotificationChannel(CHECK_PRODUCTS_NOTIFICATIONS_CHANNEL_ID,
                                                                                context.getString(R.string.check_products_notifications_channel_name),
                                                                                IMPORTANCE_LOW);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(false);
        notificationChannel.setShowBadge(false);
        notificationChannel.setBypassDnd(false);
        notificationManagerCompat.createNotificationChannel(notificationChannel);
    }
}
