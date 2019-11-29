package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import androidx.room.Room;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.BusinessDataBasePersistence;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils;

import java.util.ArrayList;
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

    public void insertBusinessList(final ArrayList<BusinessTable> businessTableList) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                for (BusinessTable businessTable : businessTableList) {
                    businessDataBasePersistence.businessDaoAccess().insertBusiness(businessTable);
                }
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

    public void deleteBusiness(final String selectedDate) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                List<BusinessTable> businessTables = businessDataBasePersistence.businessDaoAccess().queryBusinessListByDate(selectedDate);
                for (BusinessTable tables : businessTables) {
                    businessDataBasePersistence.businessDaoAccess().deleteBusiness(tables);
                }
                return null;
            }
        }.execute();
    }

    public List<BusinessTable> queryBusinessByDate(String selectedDate) {
        return businessDataBasePersistence.businessDaoAccess().queryBusinessListByDate(selectedDate);
    }

    public List<Integer> queryBusinessAmount() {
        List<Integer> totals = new ArrayList<>();
        int expenses = 0;
        int total = 0;
        List<BusinessTable> amount = businessDataBasePersistence.businessDaoAccess().queryBusinessList();
        for (BusinessTable element : amount) {
            switch (element.getBusinessName()) {
                case ConstantUtils.EXPENSES:
                    expenses += element.getAmount();
                    break;
                default:
                        total += element.getAmount();
                        break;
            }
        }
        totals.add(expenses);
        totals.add(total);
        return totals;
    }

    public void clearDataBase() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                businessDataBasePersistence.isOpen();
                businessDataBasePersistence.clearAllTables();
                businessDataBasePersistence.close();
                return null;
            }
        }.execute();
    }
}
