package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.PaymentDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable;

@Database(entities = PaymentTable.class, version = 1, exportSchema = false)
public abstract class PaymentDataBasePersistence extends RoomDatabase {
    public abstract PaymentDao paymentDaoAccess();
}
