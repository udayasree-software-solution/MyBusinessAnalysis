package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.ClientDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable;

@Database(entities = ClientsTable.class, version = 1, exportSchema = false)
public abstract class ClientDataBasePersistence extends RoomDatabase {
    public abstract ClientDao clientDaoAccess();
}
