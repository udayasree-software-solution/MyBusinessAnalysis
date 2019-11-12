package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import androidx.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.PaymentDataBasePersistence;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.TimeDataTable;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("StaticFieldLeak")
public class PaymentRepository {

    private PaymentDataBasePersistence paymentDataBasePersistence;

    public PaymentRepository(Context context) {
        paymentDataBasePersistence = Room.databaseBuilder(context, PaymentDataBasePersistence.class,
                "Payment_database")
                .build();
    }

    public void insertTask(final PaymentTable paymentTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                paymentDataBasePersistence.paymentDaoAccess().insertPaymentDetails(paymentTable);
                return null;
            }
        }.execute();
    }

    public void updateTask(final PaymentTable paymentTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                paymentDataBasePersistence.paymentDaoAccess().updateTask(paymentTable);
                return null;
            }
        }.execute();
    }

    public void deleteTask(final PaymentTable paymentTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                paymentDataBasePersistence.paymentDaoAccess().deleteTask(paymentTable);
                return null;
            }
        }.execute();
    }

    public List<PaymentTable> queryAllPaymentDetails() {
        return paymentDataBasePersistence.paymentDaoAccess().queryAllPaymentDetails();
    }

    public List<PaymentTable> queryPaymentDetail(final boolean isStatus) {
        return paymentDataBasePersistence.paymentDaoAccess().queryPaymentDetailsByStatus(isStatus);
    }

    public PaymentTable queryPaymentBySlNo(final int slNo) {
        return paymentDataBasePersistence.paymentDaoAccess().queryPaymentDetailsBySlNo(slNo);
    }

    public List<TimeDataTable> queryDateInMillis(boolean isStatus) {
        return paymentDataBasePersistence.paymentDaoAccess().queryPaymentDateByStatus(isStatus);
    }

    public ArrayList<Integer> queryTotalPayAmount() {
        List<PaymentTable> table = paymentDataBasePersistence.paymentDaoAccess().queryPayAmount();
        ArrayList<Integer> totals = new ArrayList<Integer>();
        int payableTotal = 0;
        int paidTotal = 0;
        for (PaymentTable element : table) {
            if (element.getPaymentStatus()) {
                paidTotal += Integer.parseInt(element.getPayAmount());
            } else {
                payableTotal += Integer.parseInt(element.getPayAmount());
            }
        }
        return totals;
    }

    public void clearDataBase() {
        paymentDataBasePersistence.isOpen();
        paymentDataBasePersistence.clearAllTables();
        paymentDataBasePersistence.close();
    }
}
