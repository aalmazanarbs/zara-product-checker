package com.gade.zaraproductcheckerapp.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import android.webkit.URLUtil;

import com.gade.zaraproductcheckerapp.R;

final public class NetUtil {

    public static void shouldOpenURLInChromeCustomTabs(@NonNull Activity activity, @NonNull String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
                .setShowTitle(true)
                .setStartAnimations(activity, android.R.anim.slide_in_left, android.R.anim.slide_in_left)
                .setExitAnimations(activity, android.R.anim.slide_out_right, android.R.anim.slide_out_right)
                .build();

        customTabsIntent.launchUrl(activity, Uri.parse(url));
    }

    public static boolean isValidURL(String url) {
        return URLUtil.isValidUrl(url);
    }

    public static boolean hasNetworkConnection(@NonNull final Context context) {
        try {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }

            final Network[] networks = connectivityManager.getAllNetworks();
            if (networks != null) {
                for (Network network : networks) {
                    if (network!= null && connectivityManager.getNetworkInfo(network).isConnected()) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return true;
        }

        return false;
    }

    public static boolean isConnectedToWifi(@NonNull Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }

            Network[] networks = connectivityManager.getAllNetworks();
            if (networks != null) {
                for (Network network : networks) {
                    if (network != null &&
                            connectivityManager.getNetworkInfo(network).getType() == ConnectivityManager.TYPE_WIFI &&
                            connectivityManager.getNetworkInfo(network).isConnected()) {

                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public final static class NetworkStateReceiver extends BroadcastReceiver {

        private NetworkStateReceiverListener networkStateReceiverListener;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (networkStateReceiverListener != null) {
                networkStateReceiverListener.onNetworkStateChanged(hasNetworkConnection(context));
            }
        }

        public void setNetworkStateReceiverListener (@NonNull NetworkStateReceiverListener networkStateReceiverListener){
            this.networkStateReceiverListener = networkStateReceiverListener;
        }

        public interface NetworkStateReceiverListener {
            void onNetworkStateChanged(boolean isConnected);
        }
    }
}
