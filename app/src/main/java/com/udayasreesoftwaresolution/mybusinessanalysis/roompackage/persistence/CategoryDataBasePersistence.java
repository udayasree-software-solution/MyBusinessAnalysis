package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.CategoryDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable;

@Database(entities = CategoryTable.class, version = 1, exportSchema = false)
public abstract class CategoryDataBasePersistence extends RoomDatabase {
    public abstract CategoryDao categoryDaoAccess();
}
