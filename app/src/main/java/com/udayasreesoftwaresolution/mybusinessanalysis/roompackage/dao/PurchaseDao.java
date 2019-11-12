package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;

import androidx.room.*;

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

    @Query("SELECT bill_amount FROM PurchaseTable ORDER BY time_in_millis ASC")
    List<String> queryPurchaseAmount();
}
