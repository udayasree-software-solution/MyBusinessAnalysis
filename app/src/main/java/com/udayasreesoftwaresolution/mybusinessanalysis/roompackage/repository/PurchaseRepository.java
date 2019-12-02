package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import android.content.Context;

import android.os.AsyncTask;
import androidx.room.Room;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.PurchaseDataBasePersistance;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PurchaseTable;

import java.util.List;

@SuppressLint("StaticFieldLeak")
public class PurchaseRepository {
    private PurchaseDataBasePersistance purchaseDataBasePersistance;

    public PurchaseRepository(Context context) {
        purchaseDataBasePersistance = Room.databaseBuilder(context, PurchaseDataBasePersistance.class,
                "purchase_database")
                .build();
    }

    public void insertPurchase(final PurchaseTable purchaseTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                purchaseDataBasePersistance.purchaseDaoAccess().insertPurchase(purchaseTable);
                return null;
            }
        }.execute();
    }

    public void updatePurchase(final PurchaseTable purchaseTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                purchaseDataBasePersistance.purchaseDaoAccess().updatePurchase(purchaseTable);
                return null;
            }
        }.execute();
    }

    public void deletePurchase(final PurchaseTable purchaseTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                purchaseDataBasePersistance.purchaseDaoAccess().deletePurchase(purchaseTable);
                return null;
            }
        }.execute();
    }

    public List<PurchaseTable> queryPurchaseList(String clientName) {
        return purchaseDataBasePersistance.purchaseDaoAccess().queryPurchaseListByClient(clientName);
    }

    public int queryPurchaseTotalAmount() {
        List<String> amount = purchaseDataBasePersistance.purchaseDaoAccess().queryPurchaseAmount();
        int total = 0;
        for (String value : amount) {
            total += Integer.parseInt(value);
        }
        return total;
    }

    public void clearDataBase() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                purchaseDataBasePersistance.isOpen();
                purchaseDataBasePersistance.clearAllTables();
                purchaseDataBasePersistance.close();
                return null;
            }
        }.execute();
    }
}
