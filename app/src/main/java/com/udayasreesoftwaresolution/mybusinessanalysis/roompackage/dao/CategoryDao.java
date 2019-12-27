package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;



import androidx.room.*;

import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.CategoryModel;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    void insertCategory(CategoryTable clientsTable);

    @Delete
    void deleteCategory(CategoryTable clientsTable);

    @Update
    void updateCategory(CategoryTable clientsTable);

    @Query("SELECT category_name FROM CategoryTable ORDER BY category_name ASC")
    List<String> queryCategoryList();

    @Query("SELECT * FROM CategoryTable")
    List<CategoryTable> queryFullCategoryList();
}
