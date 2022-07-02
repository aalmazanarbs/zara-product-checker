package com.gade.zaraproductcheckerapp.notifications;

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
import static com.gade.zaraproductcheckerapp.notifications.NotificationUtil.generateOpenMainActivityPendingIntent;
import static com.gade.zaraproductcheckerapp.notifications.NotificationUtil.getNotificationManager;
import static com.gade.zaraproductcheckerapp.util.UIUtil.DEFAULT_IMAGE_REQUEST_OPTIONS;
import static com.gade.zaraproductcheckerapp.util.UIUtil.copy;
import static com.gade.zaraproductcheckerapp.util.UIUtil.from;
import static com.gade.zaraproductcheckerapp.util.UIUtil.getCircleBitmap;
import static com.gade.zaraproductcheckerapp.notifications.ProductNotificationUtil.generatePriceChangedMessage;
import static com.gade.zaraproductcheckerapp.notifications.ProductNotificationUtil.generateSizeChangedMessage;
import static com.gade.zaraproductcheckerapp.notifications.ProductNotificationUtil.getNotificationsByGroup;

public class ProductNotificationNougat implements ProductNotify {

    @Override
    public void notify(@NonNull final Context context, @NonNull final ProductInfo productInfo) {
        final NotificationManagerCompat notificationManagerCompat = getNotificationManager(context);
        final int numberOfActiveNotifications = getNotificationsByGroup(context, getProductChangesGroup()).size();

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createOrUpdateNotificationChannel(notificationManagerCompat, context);
        }

        final Notification notification = new NotificationCompat.Builder(context, getProductChangesChannelId())
                .setGroupSummary(true)
                .setGroup(getProductChangesGroup())
                .setAutoCancel(true)
                .setContentIntent(generateOpenMainActivityPendingIntent(context, OPERATIONS_ID, PRODUCT_CHANGES_NOTIFICATIONS_INTENT_EXTRA_DATA))
                .setSubText(context.getString(R.string.detect_changes_products_check))
                .setSmallIcon(R.drawable.ic_notification)
                .setShowWhen(false)
                .build();

        notification.flags |= NotificationCompat.FLAG_ONLY_ALERT_ONCE | NotificationCompat.FLAG_AUTO_CANCEL;

        notificationManagerCompat.notify(OPERATIONS_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createOrUpdateNotificationChannel(final NotificationManagerCompat notificationManagerCompat, final Context context) {
        final NotificationChannel notificationChannel = new NotificationChannel(getProductChangesChannelId(),
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
        final Notification notification = new NotificationCompat.Builder(context, getProductChangesChannelId())
                .setGroup(getProductChangesGroup())
                .setSmallIcon(R.drawable.ic_notification)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setContentIntent(generateOpenMainActivityPendingIntent(context, OPERATIONS_ID, PRODUCT_CHANGES_NOTIFICATIONS_INTENT_EXTRA_DATA))
                .setWhen(System.currentTimeMillis())
                .setShowWhen(true)
                .setContentTitle(productInfo.getName())
                .setContentText(message)
                .setLargeIcon(getCircleBitmap(productImage))
                .build();

        notification.flags |= NotificationCompat.FLAG_ONLY_ALERT_ONCE | NotificationCompat.FLAG_AUTO_CANCEL;

        notificationManagerCompat.notify(notificationId, notification);
    }
}
