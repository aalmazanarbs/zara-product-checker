package com.gade.zaraproductcheckerapp.db.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

import java.util.List;

import static androidx.room.OnConflictStrategy.FAIL;

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
