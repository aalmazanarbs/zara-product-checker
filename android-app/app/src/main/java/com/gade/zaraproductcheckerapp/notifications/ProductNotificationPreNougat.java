package com.gade.zaraproductcheckerapp.notifications;

import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import java.util.List;

import static android.text.Html.FROM_HTML_MODE_LEGACY;
import static androidx.core.app.NotificationCompat.EXTRA_TEXT_LINES;
import static com.gade.zaraproductcheckerapp.notifications.NotificationUtil.generateOpenMainActivityPendingIntent;
import static com.gade.zaraproductcheckerapp.notifications.NotificationUtil.getNotificationManager;
import static com.gade.zaraproductcheckerapp.notifications.ProductNotificationUtil.generatePriceChangedMessage;
import static com.gade.zaraproductcheckerapp.notifications.ProductNotificationUtil.generateSizeChangedMessage;
import static com.gade.zaraproductcheckerapp.notifications.ProductNotificationUtil.getNotificationsByGroup;

public class ProductNotificationPreNougat implements ProductNotify {

    @Override
    public void notify(@NonNull final Context context, @NonNull final ProductInfo productInfo) {
        final NotificationCompat.InboxStyle notificationMessagesInboxStyle = new NotificationCompat.InboxStyle();
        int displayNumberOfNotifications = 0;

        if (getNotificationsByGroup(context, getProductChangesGroup()).size() == 1) {
            final CharSequence[] previousMessages = getPreviousMessages(context);
            if (previousMessages != null) {
                for (CharSequence previousMessage: previousMessages) {
                    notificationMessagesInboxStyle.addLine(previousMessage);
                    displayNumberOfNotifications += 1;
                }
            }
        }

        displayNumberOfNotifications /= 2;

        if (productInfo.hasSizeStatusChanged()) {
            notificationMessagesInboxStyle.addLine(generateHeaderNotificationLine(productInfo));
            notificationMessagesInboxStyle.addLine(generateSizeChangedMessage(context, productInfo));
            displayNumberOfNotifications += 1;
        }

        if (productInfo.hasPriceChanged()) {
            notificationMessagesInboxStyle.addLine(generateHeaderNotificationLine(productInfo));
            notificationMessagesInboxStyle.addLine(generatePriceChangedMessage(context, productInfo));
            displayNumberOfNotifications += 1;
        }

        final Notification notification = new NotificationCompat.Builder(context, getProductChangesChannelId())
                .setGroup(getProductChangesGroup())
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setShowWhen(false)
                .setContentTitle(context.getString(R.string.detect_changes_products_check))
                .setNumber(displayNumberOfNotifications)
                .setStyle(notificationMessagesInboxStyle)
                .setContentIntent(generateOpenMainActivityPendingIntent(context, OPERATIONS_ID, PRODUCT_CHANGES_NOTIFICATIONS_INTENT_EXTRA_DATA))
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL | NotificationCompat.FLAG_ONLY_ALERT_ONCE;

        getNotificationManager(context).notify(OPERATIONS_ID, notification);
    }

    private CharSequence[] getPreviousMessages(@NonNull final Context context) {
        final List<StatusBarNotification> activeNotifications = getNotificationsByGroup(context, getProductChangesGroup());
        return !activeNotifications.isEmpty() ? activeNotifications.get(0).getNotification().extras.getCharSequenceArray(EXTRA_TEXT_LINES) : null;
    }

    private Spanned generateHeaderNotificationLine(@NonNull final ProductInfo productInfo) {
        return Html.fromHtml("<b>" + productInfo.getName() + "</b>", FROM_HTML_MODE_LEGACY);
    }
}
