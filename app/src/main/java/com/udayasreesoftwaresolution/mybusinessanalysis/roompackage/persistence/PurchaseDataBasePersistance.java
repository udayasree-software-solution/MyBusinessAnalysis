package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.PurchaseDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PurchaseTable;

@Database(entities = PurchaseTable.class, version = 1, exportSchema = false)
public abstract class PurchaseDataBasePersistance extends RoomDatabase {
    public abstract PurchaseDao purchaseDaoAccess();
}
