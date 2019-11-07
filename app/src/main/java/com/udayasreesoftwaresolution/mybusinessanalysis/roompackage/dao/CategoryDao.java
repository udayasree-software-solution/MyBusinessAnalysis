package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;



import androidx.room.*;

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
