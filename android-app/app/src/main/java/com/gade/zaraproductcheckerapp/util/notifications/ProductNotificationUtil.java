package com.gade.zaraproductcheckerapp.util.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductchecker.UIFormatter;
import com.gade.zaraproductcheckerapp.activities.MainActivity;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

public class ProductNotificationUtil {

    public static final int NOTIFICATIONS_INTENT_CODE = 69;
    public static final String NOTIFICATIONS_EXTRA_INTENT = "requestCode";

    public static NotificationManagerCompat getNotificationManager(@NonNull Context context) {
        return NotificationManagerCompat.from(context);
    }

    static StatusBarNotification[] getActiveNotificationsFromNotificationManager(@NonNull Context context) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager != null ? notificationManager.getActiveNotifications() : null;
    }

    static int numberOfActiveNotifications(@NonNull Context context) {
        final StatusBarNotification[] activeNotifications = getActiveNotificationsFromNotificationManager(context);
        return activeNotifications != null ? activeNotifications.length : 0;
    }

    static String generateSizeChangedMessage(@NonNull Context context, @NonNull ProductInfo productInfo) {
        return generateProductInfo(productInfo) + " " +
                context.getString(R.string.is_now) + " " +
                UIFormatter.productAvailability(productInfo.getAvailability());
    }

    static String generatePriceChangedMessage(@NonNull Context context, @NonNull ProductInfo productInfo) {
        return generateProductInfo(productInfo) + " " +
                context.getString(R.string.has_changed_price_to) + " " +
                UIFormatter.productPrice(productInfo.getPrice());
    }

    private static String generateProductInfo(@NonNull ProductInfo productInfo) {
        return productInfo.getDesiredSize() + " " + productInfo.getDesiredColor();
    }

    static PendingIntent generateOpenMainActivityPendingIntent(@NonNull Context context) {
        return PendingIntent.getActivity(context,
                NOTIFICATIONS_INTENT_CODE,
                new Intent(context, MainActivity.class).putExtra(NOTIFICATIONS_EXTRA_INTENT, NOTIFICATIONS_INTENT_CODE),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
