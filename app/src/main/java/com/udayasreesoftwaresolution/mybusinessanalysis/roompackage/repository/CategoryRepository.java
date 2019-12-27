package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import androidx.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.CategoryModel;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.CategoryDataBasePersistence;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils;

import java.util.ArrayList;
import java.util.List;
@SuppressLint("StaticFieldLeak")
public class CategoryRepository {
    private CategoryDataBasePersistence categoryDataBasePersistence;

    public CategoryRepository(Context context) {
        categoryDataBasePersistence = Room.databaseBuilder(context, CategoryDataBasePersistence.class,
                "category_database")
                .build();
    }

    public void insertTask(final CategoryTable categoryTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                categoryDataBasePersistence.categoryDaoAccess().insertCategory(categoryTable);
                return null;
            }
        }.execute();
    }

    public void updateTask(final CategoryTable categoryTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                categoryDataBasePersistence.categoryDaoAccess().updateCategory(categoryTable);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final CategoryTable categoryTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                categoryDataBasePersistence.categoryDaoAccess().deleteCategory(categoryTable);
                return null;
            }
        }.execute();
    }

    public List<String> queryCategoryNamesList() {
        return categoryDataBasePersistence.categoryDaoAccess().queryCategoryList();
    }

    public List<CategoryTable> queryFullCategoryList() {
        return categoryDataBasePersistence.categoryDaoAccess().queryFullCategoryList();
    }

    public void clearDataBase() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                categoryDataBasePersistence.isOpen();
                categoryDataBasePersistence.clearAllTables();
                categoryDataBasePersistence.close();
                return null;
            }
        }.execute();
    }
}
