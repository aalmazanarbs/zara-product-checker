package com.gade.zaraproductcheckerapp.notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import androidx.annotation.NonNull;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductchecker.UIFormatter;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class ProductNotificationUtil {

    static List<StatusBarNotification> getNotificationsByGroup(@NonNull final Context context, @NonNull final String group) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null || notificationManager.getActiveNotifications() == null) {
            return emptyList();
        }

        return Arrays.stream(notificationManager.getActiveNotifications())
                .filter(sbn -> group.equals(sbn.getNotification().getGroup()))
                .collect(toList());
    }

    static String generateSizeChangedMessage(@NonNull final Context context, @NonNull final ProductInfo productInfo) {
        return generateProductInfo(productInfo) + " " +
                context.getString(R.string.is_now) + " " +
                UIFormatter.productAvailability(productInfo.getAvailability());
    }

    static String generatePriceChangedMessage(@NonNull final Context context, @NonNull final ProductInfo productInfo) {
        return generateProductInfo(productInfo) + " " +
                context.getString(R.string.has_changed_price_to) + " " +
                UIFormatter.productPrice(productInfo.getPrice());
    }

    private static String generateProductInfo(@NonNull final ProductInfo productInfo) {
        return productInfo.getDesiredSize() + " " + productInfo.getDesiredColor();
    }
}
