package com.gade.zaraproductcheckerapp.notifications;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;

import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

public class ProductNotificationManager {

    private static final ProductNotify productNotify;

    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.N) {
            productNotify = new ProductNotificationNougat();
        } else {
            productNotify = new ProductNotificationPreNougat();
        }
    }

    public static void notify(@NonNull final Context context, @NonNull final ProductInfo productInfo) {
        productNotify.notify(context, productInfo);
    }
}
