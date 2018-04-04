package com.gade.zaraproductcheckerapp.util.notifications;

import android.content.Context;
import android.support.annotation.NonNull;

import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

interface IProductNotify {

    void notify(@NonNull Context context, @NonNull ProductInfo productInfo);
}
