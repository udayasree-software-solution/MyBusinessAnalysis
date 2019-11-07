package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.BusinessDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable;

@Database(entities = BusinessTable.class, version = 1, exportSchema = false)
public abstract class BusinessDataBasePersistence extends RoomDatabase {
    public abstract BusinessDao businessDaoAccess();
}
