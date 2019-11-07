package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.PaymentDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable;

@Database(entities = PaymentTable.class, version = 1, exportSchema = false)
public abstract class PaymentDataBasePersistence extends RoomDatabase {
    public abstract PaymentDao paymentDaoAccess();
}
