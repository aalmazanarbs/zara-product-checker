package com.gade.zaraproductcheckerapp.util.notifications;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;

import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

public class ProductNotificationManager {

    private static final IProductNotify notificationImpl;

    static {
        final int version = Build.VERSION.SDK_INT;
        if (version >= Build.VERSION_CODES.N) {
            notificationImpl = new ProductNotificationNougat();
        } else {
            notificationImpl = new ProductNotificationPreNougat();
        }
    }

    public static void notify(@NonNull Context context, @NonNull ProductInfo productInfo) {
        notificationImpl.notify(context, productInfo);
    }
}
