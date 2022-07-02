package com.gade.zaraproductcheckerapp.notifications;

import android.content.Context;
import androidx.annotation.NonNull;

import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

public interface ProductNotify {

    int PRODUCT_CHANGES_NOTIFICATIONS_INTENT_EXTRA_DATA = 69;
    int OPERATIONS_ID = 0;

    void notify(@NonNull final Context context, @NonNull final ProductInfo productInfo);

    default String getProductChangesChannelId() {
        return "PRODUCT_CHANGES_NOTIFICATIONS_CHANNEL_ID";
    }

    default String getProductChangesGroup() {
        return "PRODUCT_CHANGES_NOTIFICATIONS_GROUP";
    }
}
