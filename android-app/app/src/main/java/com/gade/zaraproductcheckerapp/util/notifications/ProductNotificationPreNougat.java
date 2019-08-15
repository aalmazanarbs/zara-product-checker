package com.gade.zaraproductcheckerapp.util.notifications;

import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.Spanned;

import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

public class ProductNotificationPreNougat implements IProductNotify {

    @Override
    @SuppressWarnings("deprecation")
    public void notify(@NonNull Context context, @NonNull ProductInfo productInfo) {
        NotificationCompat.InboxStyle notificationMessagesInboxStyle = new NotificationCompat.InboxStyle();
        int displayNumberOfNotifications = 0;

        if (ProductNotificationUtil.numberOfActiveNotifications(context) == 1) {
            CharSequence[] previousMessages = getPreviousMessages(context);
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
            notificationMessagesInboxStyle.addLine(ProductNotificationUtil.generateSizeChangedMessage(context, productInfo));
            displayNumberOfNotifications += 1;
        }

        if (productInfo.hasPriceChanged()) {
            notificationMessagesInboxStyle.addLine(generateHeaderNotificationLine(productInfo));
            notificationMessagesInboxStyle.addLine(ProductNotificationUtil.generatePriceChangedMessage(context, productInfo));
            displayNumberOfNotifications += 1;
        }

        NotificationCompat.Builder notificationCompatStackedBuilder = new NotificationCompat.Builder(context)
                .setDefaults(NotificationCompat.DEFAULT_LIGHTS | NotificationCompat.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true)
                .setShowWhen(false)
                .setContentTitle(context.getString(R.string.detect_changes_products_check))
                .setNumber(displayNumberOfNotifications)
                .setStyle(notificationMessagesInboxStyle)
                .setContentIntent(ProductNotificationUtil.generateOpenMainActivityPendingIntent(context));

        Notification notificationStacked = notificationCompatStackedBuilder.build();
        notificationStacked.flags |= Notification.FLAG_AUTO_CANCEL | NotificationCompat.FLAG_ONLY_ALERT_ONCE;

        ProductNotificationUtil.getNotificationManager(context).notify(0, notificationStacked);
    }

    private CharSequence[] getPreviousMessages(@NonNull Context context) {
        StatusBarNotification[] activeNotifications = ProductNotificationUtil.getActiveNotificationsFromNotificationManager(context);
        return (activeNotifications != null) ? activeNotifications[0].getNotification().extras.getCharSequenceArray(NotificationCompat.EXTRA_TEXT_LINES) : null;
    }

    @SuppressWarnings("deprecation")
    private Spanned generateHeaderNotificationLine(@NonNull ProductInfo productInfo) {
        return Html.fromHtml("<b>" + productInfo.getName() + "</b>");
    }
}
