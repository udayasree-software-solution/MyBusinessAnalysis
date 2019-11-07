package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao.CategoryDao;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable;

@Database(entities = CategoryTable.class, version = 1, exportSchema = false)
public abstract class CategoryDataBasePersistence extends RoomDatabase {
    public abstract CategoryDao categoryDaoAccess();
}
