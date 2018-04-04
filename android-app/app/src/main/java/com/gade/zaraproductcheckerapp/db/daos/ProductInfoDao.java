package com.gade.zaraproductcheckerapp.db.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.FAIL;

@Dao
public interface ProductInfoDao {

    @Query("SELECT * FROM ProductInfo ORDER BY datetime(ProductInfo.added)")
    List<ProductInfo> getAll();

    @Insert(onConflict = FAIL)
    Long insert(final ProductInfo productInfo);

    @Update
    int update(final ProductInfo productInfo);

    @Delete
    void delete(final ProductInfo productInfo);
}
