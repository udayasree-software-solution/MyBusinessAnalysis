package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.PurchaseDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PurchaseTable;

@Database(entities = PurchaseTable.class, version = 2, exportSchema = false)
public abstract class PurchaseDataBasePersistance extends RoomDatabase {
    public abstract PurchaseDao purchaseDaoAccess();
}
