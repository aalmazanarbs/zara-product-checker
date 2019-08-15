package com.gade.zaraproductcheckerapp.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.preference.PreferenceManager;

import com.gade.zaraproductchecker.APIHelper;
import com.gade.zaraproductchecker.ProductAPI;
import com.gade.zaraproductchecker.ProductJSONHelper;
import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductchecker.model.ProductStatus;
import com.gade.zaraproductcheckerapp.db.AppDatabase;
import com.gade.zaraproductcheckerapp.db.daos.ProductInfoDao;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;
import com.gade.zaraproductcheckerapp.handlers.ProductCheckerHandler;
import com.gade.zaraproductcheckerapp.util.NetUtil;
import com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.gade.zaraproductcheckerapp.util.NetUtil.hasNetworkConnection;

public class ZaraProductCheckerService extends IntentService {

    public ZaraProductCheckerService() {
        super("ZaraProductCheckerService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        boolean sendAlwaysBroadcast = intent != null && intent.getAction() != null && intent.getAction().equals(ProductCheckerHandler.PRODUCTS_INFO_BROADCAST_LIST_ALWAYS);
        if (!hasNetworkConnection(getApplicationContext()) && !sendAlwaysBroadcast) {
            return;
        }

        boolean thereAreChangesInProducts = false;

        final ProductInfoDao productInfoDao = AppDatabase.getDatabase(getApplicationContext()).productInfoDao();
        final List<ProductInfo> productsInfo = productInfoDao.getAll();

        if (productsInfo.size() > 0 &&
            hasNetworkConnection(getApplicationContext()) &&
            canCheckProducts(getApplicationContext(), sendAlwaysBroadcast)) {

            thereAreChangesInProducts = detectChangesUpdateProductsAndNotify(productsInfo, productInfoDao);
        }

        if (thereAreChangesInProducts || sendAlwaysBroadcast) {
            sendLocalBroadcastProductsInfo(productsInfo);
        }
    }

    private boolean canCheckProducts(@NonNull Context context, boolean sendAlwaysBroadcast) {
        if (sendAlwaysBroadcast) {
            return true;
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean checkProductsInBackgroundOnlyWifiPreference = sharedPreferences.getBoolean(context.getString(R.string.check_products_in_background_only_wifi), false);

        if (!checkProductsInBackgroundOnlyWifiPreference) {
            return true;
        }

        return NetUtil.isConnectedToWifi(context);
    }

    private boolean detectChangesUpdateProductsAndNotify(@NonNull final List<ProductInfo> productsInfo,
                                                         @NonNull final ProductInfoDao productInfoDao) {

        HashSet<String> productIDs = new HashSet<>();
        for (ProductInfo productInfo : productsInfo) {
            productIDs.add(productInfo.getAPIId());
        }

        final List<ProductStatus> productStatuses = ProductJSONHelper.getProductStatuses(ProductAPI.doCall(productIDs));

        if (productStatuses == null || productStatuses.isEmpty()) {
            return false;
        }

        return analyzeChangesAndUpdateProductsDB(productsInfo, productStatuses, productInfoDao);
    }

    private boolean analyzeChangesAndUpdateProductsDB(final List<ProductInfo> productsInfo,
                                                      final List<ProductStatus> productStatuses,
                                                      final ProductInfoDao productInfoDao) {

        boolean someProductHasChanged = false;

        for (int i = 0; i < productsInfo.size(); i++) {
            final ProductStatus productStatus = APIHelper.searchProductStatus(productStatuses, productsInfo.get(i).getAPIId(), productsInfo.get(i).getDesiredSize(), productsInfo.get(i).getDesiredColor());
            if (productStatus != null) {
                // Check availability
                if (productStatus.getAvailability() != null &&
                    !productStatus.getAvailability().isEmpty() &&
                    !productsInfo.get(i).getAvailability().equals(productStatus.getAvailability())) {

                    productsInfo.get(i).setAvailability(productStatus.getAvailability());
                    productsInfo.get(i).setSizeStatusChanges(true);
                }

                // Check price
                if (productStatus.getPrice() != null &&
                    !productStatus.getPrice().isEmpty() &&
                    !productsInfo.get(i).getPrice().equals(productStatus.getPrice())) {

                    productsInfo.get(i).setPrice(productStatus.getPrice());
                    productsInfo.get(i).setPriceChanges(true);
                }

                if (productsInfo.get(i).hasSizeStatusChanged() || productsInfo.get(i).hasPriceChanged()) {
                    if (productInfoDao.update(productsInfo.get(i)) == 1) {
                        someProductHasChanged = true;
                        ProductNotificationManager.notify(getApplicationContext(), productsInfo.get(i));
                    }
                }
            } else {
                productsInfo.get(i).setNotFound(true);
            }
        }

        return someProductHasChanged;
    }

    private void sendLocalBroadcastProductsInfo(List<ProductInfo> productsInfo) {
        Intent productsInfoIntent = new Intent(ProductCheckerHandler.PRODUCTS_INFO_BROADCAST);
        productsInfoIntent.putParcelableArrayListExtra(ProductCheckerHandler.PRODUCTS_INFO_BROADCAST_LIST, (ArrayList) productsInfo);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(productsInfoIntent);
    }
}
