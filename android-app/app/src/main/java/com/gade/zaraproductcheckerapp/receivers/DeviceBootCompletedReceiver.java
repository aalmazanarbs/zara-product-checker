package com.gade.zaraproductcheckerapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.gade.zaraproductcheckerapp.handlers.ProductCheckerHandler;

public class DeviceBootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            ProductCheckerHandler.startStopCheckProductsService(context);
        }
    }
}
