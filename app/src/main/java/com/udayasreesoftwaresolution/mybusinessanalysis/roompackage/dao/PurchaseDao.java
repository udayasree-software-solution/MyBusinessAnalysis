package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PurchaseTable;

import java.util.List;

@Dao
public interface PurchaseDao {
    @Insert
    void insertPurchase(PurchaseTable purchaseTable);

    @Delete
    void deletePurchase(PurchaseTable purchaseTable);

    @Update
    void updatePurchase(PurchaseTable purchaseTable);

    @Query("SELECT * FROM PurchaseTable ORDER BY time_in_millis ASC")
    List<PurchaseTable> queryPurchaseList();
}
