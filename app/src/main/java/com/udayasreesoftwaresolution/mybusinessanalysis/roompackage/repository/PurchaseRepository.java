package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.PurchaseDataBasePersistance;

@SuppressLint("StaticFieldLeak")
public class PurchaseRepository {
    private PurchaseDataBasePersistance purchaseDataBasePersistance;

    public PurchaseRepository(Context context) {
        purchaseDataBasePersistance = Room.databaseBuilder(context, PurchaseDataBasePersistance.class,
                "purchase_database")
                .build();
    }
}
