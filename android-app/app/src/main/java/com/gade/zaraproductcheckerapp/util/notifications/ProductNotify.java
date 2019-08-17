package com.gade.zaraproductcheckerapp.util.notifications;

import android.content.Context;
import androidx.annotation.NonNull;

import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

interface ProductNotify {

    void notify(@NonNull Context context, @NonNull ProductInfo productInfo);
}
