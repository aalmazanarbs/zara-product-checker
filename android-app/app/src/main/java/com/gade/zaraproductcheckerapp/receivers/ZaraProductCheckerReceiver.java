package com.gade.zaraproductcheckerapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gade.zaraproductcheckerapp.services.ZaraProductCheckerJobIntentService;

public class ZaraProductCheckerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ZaraProductCheckerJobIntentService.enqueueWork(context, intent);
    }
}
