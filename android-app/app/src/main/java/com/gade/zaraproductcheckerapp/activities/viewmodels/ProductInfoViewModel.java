package com.gade.zaraproductcheckerapp.activities.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.annotation.NonNull;

import com.gade.zaraproductcheckerapp.db.AppDatabase;
import com.gade.zaraproductcheckerapp.db.daos.ProductInfoDao;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;

public class ProductInfoViewModel extends AndroidViewModel {

    private final ProductInfoDao productInfoDao;

    public ProductInfoViewModel(@NonNull Application application) {
        super(application);
        this.productInfoDao = AppDatabase.getDatabase(application).productInfoDao();
    }

    public Completable addProductsInfo(final List<ProductInfo> productsInfo) {
        return Completable.fromAction(() -> {
            for (final ProductInfo productInfo: productsInfo) {
                productInfo.setAdded(new Date());
                final Long insertedId = productInfoDao.insert(productInfo);
                if (insertedId != -1) {
                    productInfo.setId(insertedId);
                }
            }
        });
    }

    public Completable removeProductInfo(final ProductInfo productInfo) {
        return Completable.fromAction(() -> productInfoDao.delete(productInfo));
    }
}
