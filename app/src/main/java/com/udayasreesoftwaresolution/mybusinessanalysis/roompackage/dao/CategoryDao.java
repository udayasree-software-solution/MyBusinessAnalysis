package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insertClient(CategoryTable clientsTable);

    @Delete
    void deleteClient(CategoryTable clientsTable);

    @Update
    void updateClient(CategoryTable clientsTable);

    @Query("SELECT * FROM CategoryTable ORDER BY category_name ASC")
    List<CategoryTable> queryCategoryList();
}
