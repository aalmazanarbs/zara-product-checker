package com.gade.zaraproductcheckerapp.util.notifications;

import android.app.Notification;

import android.app.NotificationChannel;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import static android.app.NotificationManager.IMPORTANCE_HIGH;
import static com.gade.zaraproductcheckerapp.util.UIUtil.DEFAULT_IMAGE_REQUEST_OPTIONS;
import static com.gade.zaraproductcheckerapp.util.UIUtil.copy;
import static com.gade.zaraproductcheckerapp.util.UIUtil.from;
import static com.gade.zaraproductcheckerapp.util.UIUtil.getCircleBitmap;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.generateOpenMainActivityPendingIntent;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.generatePriceChangedMessage;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.generateSizeChangedMessage;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.getNotificationManager;
import static com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationUtil.numberOfActiveNotifications;

public class ProductNotificationNougat implements ProductNotify {

    private final static String PRODUCT_CHANGES_NOTIFICATIONS_CHANNEL_ID = "PRODUCT_CHANGES_NOTIFICATIONS_CHANNEL_ID";
    private final static String PRODUCT_CHANGES_NOTIFICATIONS_GROUP_KEY = "PRODUCT_CHANGES_NOTIFICATIONS_GROUP_KEY";

    @Override
    public void notify(@NonNull Context context, @NonNull ProductInfo productInfo) {
        final NotificationManagerCompat notificationManagerCompat = getNotificationManager(context);
        final int numberOfActiveNotifications = numberOfActiveNotifications(context);

        if (numberOfActiveNotifications == 0) {
            showStackerNotification(notificationManagerCompat, context);
        }

        int notificationId = numberOfActiveNotifications;
        if (productInfo.hasSizeStatusChanged()) {
            notificationId++;
            showStackedNotification(notificationManagerCompat, context, productInfo, generateSizeChangedMessage(context, productInfo), notificationId);
        }

        if (productInfo.hasPriceChanged()) {
            notificationId++;
            showStackedNotification(notificationManagerCompat, context, productInfo, generatePriceChangedMessage(context, productInfo), notificationId);
        }
    }

    private void showStackerNotification(final NotificationManagerCompat notificationManagerCompat, final Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            createOrUpdateNotificationChannel(notificationManagerCompat, context);
        }

        final NotificationCompat.Builder notificationCompatStackerBuilder = new NotificationCompat.Builder(context, PRODUCT_CHANGES_NOTIFICATIONS_CHANNEL_ID)
                .setGroupSummary(true)
                .setGroup(PRODUCT_CHANGES_NOTIFICATIONS_GROUP_KEY)
                .setAutoCancel(true)
                .setContentIntent(generateOpenMainActivityPendingIntent(context))
                .setSubText(context.getString(R.string.detect_changes_products_check))
                .setSmallIcon(R.drawable.ic_notification)
                .setShowWhen(false);

        final Notification notificationStacker = notificationCompatStackerBuilder.build();
        notificationStacker.flags |= NotificationCompat.FLAG_ONLY_ALERT_ONCE | NotificationCompat.FLAG_AUTO_CANCEL;

        notificationManagerCompat.notify(0, notificationStacker);
    }

    @RequiresApi(26)
    private void createOrUpdateNotificationChannel(final NotificationManagerCompat notificationManagerCompat, final Context context) {
        final NotificationChannel notificationChannel = new NotificationChannel(PRODUCT_CHANGES_NOTIFICATIONS_CHANNEL_ID,
                                                                                context.getString(R.string.products_changes_notifications_channel_name),
                                                                                IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setShowBadge(true);
        notificationChannel.setBypassDnd(true);
        notificationManagerCompat.createNotificationChannel(notificationChannel);
    }

    private void showStackedNotification(final NotificationManagerCompat notificationManagerCompat,
                                         final Context context,
                                         final ProductInfo productInfo,
                                         final String message,
                                         final int notificationId) {
        Glide.with(context)
             .asBitmap()
             .load(productInfo.getImageUrl())
             .apply(DEFAULT_IMAGE_REQUEST_OPTIONS)
             .into(new CustomTarget<Bitmap>() {
                 @Override
                 public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                     showStackedNotification(notificationManagerCompat, context, productInfo, message, notificationId, copy(resource));
                 }

                 @Override
                 public void onLoadFailed(@Nullable Drawable errorDrawable) {
                     super.onLoadFailed(errorDrawable);
                     showStackedNotification(notificationManagerCompat, context, productInfo, message, notificationId, copy(from(errorDrawable)));
                 }

                 @Override
                 public void onLoadCleared(@Nullable Drawable placeholder) { }
             });
    }

    private void showStackedNotification(final NotificationManagerCompat notificationManagerCompat,
                                         final Context context,
                                         final ProductInfo productInfo,
                                         final String message,
                                         final int notificationId,
                                         final Bitmap productImage) {
        final NotificationCompat.Builder notificationCompatStackedBuilder = new NotificationCompat.Builder(context, PRODUCT_CHANGES_NOTIFICATIONS_CHANNEL_ID)
                .setGroup(PRODUCT_CHANGES_NOTIFICATIONS_GROUP_KEY)
                .setSmallIcon(R.drawable.ic_notification)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(generateOpenMainActivityPendingIntent(context))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentTitle(productInfo.getName())
                .setContentText(message)
                .setLargeIcon(getCircleBitmap(productImage));

        final Notification notificationStacked = notificationCompatStackedBuilder.build();
        notificationStacked.flags |= NotificationCompat.FLAG_ONLY_ALERT_ONCE | NotificationCompat.FLAG_AUTO_CANCEL;

        notificationManagerCompat.notify(notificationId, notificationStacked);
    }
}
