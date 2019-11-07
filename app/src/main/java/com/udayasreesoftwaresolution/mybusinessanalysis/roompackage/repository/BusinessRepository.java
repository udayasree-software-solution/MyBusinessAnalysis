package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import androidx.room.Room;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.BusinessDataBasePersistence;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable;

import java.util.List;

@SuppressLint("StaticFieldLeak")
public class BusinessRepository {
    private BusinessDataBasePersistence businessDataBasePersistence;

    public BusinessRepository(Context context) {
        businessDataBasePersistence = Room.databaseBuilder(context, BusinessDataBasePersistence.class,
                "category_business")
                .build();
    }

    public void insertBusiness(final BusinessTable businessTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                businessDataBasePersistence.businessDaoAccess().insertBusiness(businessTable);
                return null;
            }
        }.execute();
    }

    public void updateBusiness(final BusinessTable businessTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                businessDataBasePersistence.businessDaoAccess().updateBusiness(businessTable);
                return null;
            }
        }.execute();
    }

    public void deleteBusiness(final BusinessTable businessTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                businessDataBasePersistence.businessDaoAccess().deleteBusiness(businessTable);
                return null;
            }
        }.execute();
    }

    public List<BusinessTable> queryBusinessByDate(String selectedDate) {
        return businessDataBasePersistence.businessDaoAccess().queryBusinessListByDate(selectedDate);
    }

    public void clearDataBase() {
        businessDataBasePersistence.isOpen();
        businessDataBasePersistence.clearAllTables();
        businessDataBasePersistence.close();
    }
}
