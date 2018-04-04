package com.gade.zaraproductcheckerapp.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.gade.zaraproductcheckerapp.db.daos.ProductInfoDao;
import com.gade.zaraproductcheckerapp.db.entities.ProductInfo;

@Database(entities = {ProductInfo.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ProductInfoDao productInfoDao();

    private static AppDatabase appDatabase;

    public static AppDatabase getDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                               AppDatabase.class,
                                               "com.gade.zaraproductcheckerapp.db").build();
        }
        return appDatabase;
    }
}