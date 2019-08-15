package com.gade.zaraproductcheckerapp.util.notifications;

import android.app.Notification;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import static com.gade.zaraproductcheckerapp.util.UIUtil.base64StringToBitmap;
import static com.gade.zaraproductcheckerapp.util.UIUtil.getCircleBitmap;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.generateOpenMainActivityPendingIntent;

public class ProductNotificationNougat implements IProductNotify {

    private final String NOTIFICATIONS_GROUP_KEY = "PRODUCT_CHANGES_NOTIFICATIONS";

    @Override
    public void notify(@NonNull Context context, @NonNull ProductInfo productInfo) {
        NotificationManagerCompat notificationManagerCompat = ProductNotificationUtil.getNotificationManager(context);
        int numberOfActiveNotifications = ProductNotificationUtil.numberOfActiveNotifications(context);

        if (numberOfActiveNotifications == 0) {
            showStackerNotification(notificationManagerCompat, context);
        }

        int notificationID = numberOfActiveNotifications;
        if (productInfo.hasSizeStatusChanged()) {
            notificationID++;
            showStackedNotification(notificationManagerCompat, context, productInfo, ProductNotificationUtil.generateSizeChangedMessage(context, productInfo), notificationID);
        }

        if (productInfo.hasPriceChanged()) {
            notificationID++;
            showStackedNotification(notificationManagerCompat, context, productInfo, ProductNotificationUtil.generatePriceChangedMessage(context, productInfo), notificationID);
        }
    }

    private void showStackerNotification(@NonNull NotificationManagerCompat notificationManagerCompat, @NonNull Context context) {
        NotificationCompat.Builder notificationCompatStackerBuilder = new NotificationCompat.Builder(context)
                .setGroupSummary(true)
                .setGroup(NOTIFICATIONS_GROUP_KEY)
                .setAutoCancel(true)
                .setContentIntent(generateOpenMainActivityPendingIntent(context))
                .setSubText(context.getString(R.string.detect_changes_products_check))
                .setSmallIcon(R.drawable.ic_notification)
                .setShowWhen(false);

        Notification notificationStacker = notificationCompatStackerBuilder.build();
        notificationStacker.flags |= NotificationCompat.FLAG_ONLY_ALERT_ONCE | NotificationCompat.FLAG_AUTO_CANCEL;

        notificationManagerCompat.notify(0, notificationStacker);
    }

    private void showStackedNotification(@NonNull NotificationManagerCompat notificationManagerCompat, @NonNull Context context, @NonNull ProductInfo productInfo, String message, int notificationID) {

        NotificationCompat.Builder notificationCompatStackedBuilder = new NotificationCompat.Builder(context)
                .setGroup(NOTIFICATIONS_GROUP_KEY)
                .setSmallIcon(R.drawable.ic_notification)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(generateOpenMainActivityPendingIntent(context))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentTitle(productInfo.getName())
                .setContentText(message)
                .setLargeIcon(getCircleBitmap(getProductInfoNotificationImage(productInfo, context)));

        Notification notificationStacked = notificationCompatStackedBuilder.build();
        notificationStacked.flags |= NotificationCompat.FLAG_ONLY_ALERT_ONCE | NotificationCompat.FLAG_AUTO_CANCEL;

        notificationManagerCompat.notify(notificationID, notificationStacked);
    }

    private Bitmap getProductInfoNotificationImage(ProductInfo productInfo, Context context) {
        final Bitmap bitmapProductImage = base64StringToBitmap(productInfo.getImageBase64());
        if (bitmapProductImage != null) {
            return bitmapProductImage;
        }

        return BitmapFactory.decodeResource(context.getResources(), R.drawable.no_product_image);
    }
}
