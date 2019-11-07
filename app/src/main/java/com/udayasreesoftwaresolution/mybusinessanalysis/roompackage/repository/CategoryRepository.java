package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import androidx.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.CategoryDataBasePersistence;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable;

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
                categoryDataBasePersistence.categoryDaoAccess().insertClient(categoryTable);
                return null;
            }
        }.execute();
    }

    public void updateTask(final CategoryTable categoryTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                categoryDataBasePersistence.categoryDaoAccess().updateClient(categoryTable);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final CategoryTable categoryTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                categoryDataBasePersistence.categoryDaoAccess().deleteClient(categoryTable);
                return null;
            }
        }.execute();
    }

    public List<CategoryTable> queryClientNamesList() {
        return categoryDataBasePersistence.categoryDaoAccess().queryCategoryList();
    }

    public void clearDataBase() {
        categoryDataBasePersistence.isOpen();
        categoryDataBasePersistence.clearAllTables();
        categoryDataBasePersistence.close();
    }
}