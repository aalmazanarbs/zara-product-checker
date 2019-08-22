package com.gade.zaraproductcheckerapp.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.preference.PreferenceManager;

import com.gade.zaraproductchecker.ProductApi;
import com.gade.zaraproductchecker.ProductJsonHelper;
import com.gade.zaraproductchecker.model.ProductStatus;
import com.gade.zaraproductcheckerapp.R;
import com.gade.zaraproductcheckerapp.db.AppDatabase;
import com.gade.zaraproductcheckerapp.db.daos.ProductInfoDao;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;
import com.gade.zaraproductcheckerapp.util.notifications.ProductNotificationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.gade.zaraproductchecker.ApiHelper.searchProductStatus;
import static com.gade.zaraproductchecker.util.OptionalUtil.ifPresentOrElse;
import static com.gade.zaraproductchecker.util.StringUtil.isNotEmpty;
import static com.gade.zaraproductcheckerapp.services.ZaraProductCheckerJobIntentServiceHandler.PRODUCTS_INFO_REQUEST_RESULT_DATA_ALWAYS;
import static com.gade.zaraproductcheckerapp.util.NetUtil.hasNetworkConnection;
import static com.gade.zaraproductcheckerapp.util.NetUtil.isConnectedToWifi;
import static java.lang.Boolean.FALSE;

public class ZaraProductCheckerJobIntentService extends JobIntentService {

    public static final String PRODUCTS_INFO_REQUEST_RESULT_BROADCAST = "com.gade.zaraproductchecker.productsinfo.result.broadcast";
    public static final String PRODUCTS_INFO_REQUEST_RESULT_DATA = "com.gade.zaraproductchecker.productsinfo.result.data";

    private static final int JOB_ID = 69;

    public static void enqueueWork(final Context context, final Intent intent) {
        enqueueWork(context, ZaraProductCheckerJobIntentService.class, JOB_ID, intent);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        final boolean alwaysResultProductsInfo = intent.getBooleanExtra(PRODUCTS_INFO_REQUEST_RESULT_DATA_ALWAYS, FALSE);
        if (!hasNetworkConnection(getApplicationContext()) && !alwaysResultProductsInfo) {
            return;
        }

        boolean thereAreChangesInProducts = false;

        final ProductInfoDao productInfoDao = AppDatabase.getDatabase(getApplicationContext()).productInfoDao();
        final List<ProductInfo> productsInfo = productInfoDao.getAll();

        if (productsInfo.size() > 0 &&
            hasNetworkConnection(getApplicationContext()) &&
            canCheckProducts(getApplicationContext(), alwaysResultProductsInfo)) {

            thereAreChangesInProducts = detectChangesUpdateProductsAndNotify(productsInfo, productInfoDao);
        }

        if (thereAreChangesInProducts || alwaysResultProductsInfo) {
            sendLocalBroadcastProductsInfo(productsInfo);
        }

        ZaraProductCheckerJobIntentServiceHandler.startOrStopPeriodicallyBackground(getApplicationContext());
        stopSelf();
    }

    private boolean canCheckProducts(@NonNull final Context context, final boolean alwaysResultProductsInfo) {
        if (alwaysResultProductsInfo) {
            return true;
        }

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final boolean checkProductsInBackgroundOnlyWifiPreference = sharedPreferences.getBoolean(context.getString(R.string.check_products_in_background_only_wifi), false);

        if (!checkProductsInBackgroundOnlyWifiPreference) {
            return true;
        }

        return isConnectedToWifi(context);
    }

    private boolean detectChangesUpdateProductsAndNotify(@NonNull final List<ProductInfo> productsInfo,
                                                         @NonNull final ProductInfoDao productInfoDao) {
        final Set<String> productIDs = productsInfo.stream().map(ProductInfo::getApiId).collect(Collectors.toSet());

        return ProductApi.doCall(productIDs)
                .map(ProductJsonHelper::getProductStatuses)
                .map(productStatuses -> analyzeChangesAndUpdateProductsDB(productsInfo, productStatuses, productInfoDao))
                .orElse(FALSE);
    }

    private boolean analyzeChangesAndUpdateProductsDB(final List<ProductInfo> productsInfo,
                                                      final List<ProductStatus> productStatuses,
                                                      final ProductInfoDao productInfoDao) {

        final AtomicBoolean someProductHasChanged = new AtomicBoolean(false);

        productsInfo.forEach(productInfo ->
                ifPresentOrElse(
                        searchProductStatus(productStatuses, productInfo.getApiId(), productInfo.getDesiredSize(), productInfo.getDesiredColor()),
                        productStatus -> {
                            // Check availability
                            if (isNotEmpty(productStatus.getAvailability()) &&
                                    !productInfo.getAvailability().equals(productStatus.getAvailability())) {

                                productInfo.setAvailability(productStatus.getAvailability());
                                productInfo.setSizeStatusChanges(true);
                            }

                            // Check price
                            if (isNotEmpty(productStatus.getPrice()) &&
                                    !productInfo.getPrice().equals(productStatus.getPrice())) {

                                productInfo.setPrice(productStatus.getPrice());
                                productInfo.setPriceChanges(true);
                            }

                            if (productInfo.hasSizeStatusChanged() || productInfo.hasPriceChanged()) {
                                if (productInfoDao.update(productInfo) == 1) {
                                    someProductHasChanged.set(true);
                                    ProductNotificationManager.notify(getApplicationContext(), productInfo);
                                }
                            }
                        },
                        () -> productInfo.setNotFound(true))
        );

        return someProductHasChanged.get();
    }

    private void sendLocalBroadcastProductsInfo(final List<ProductInfo> productsInfo) {
        final Intent productsInfoResultIntent = new Intent(PRODUCTS_INFO_REQUEST_RESULT_BROADCAST);
        productsInfoResultIntent.putParcelableArrayListExtra(PRODUCTS_INFO_REQUEST_RESULT_DATA, (ArrayList) productsInfo);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(productsInfoResultIntent);
    }
}
