package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;

import androidx.room.*;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.TimeDataTable;

import java.util.List;

@Dao
public interface PaymentDao {
    @Insert
    void insertPaymentDetails(PaymentTable paymentTable);

    @Update
    void updateTask(PaymentTable taskDataTable);

    @Delete
    void deleteTask(PaymentTable taskDataTable);

    @Query("SELECT * FROM PaymentTable ORDER BY date_in_millis ASC")
    List<PaymentTable> queryAllPaymentDetails();

    @Query("SELECT * FROM PaymentTable WHERE payment_status =:isStatus ORDER BY date_in_millis ASC")
    List<PaymentTable> queryPaymentDetailsByStatus(boolean isStatus);

    @Query("SELECT * FROM PaymentTable WHERE slNo =:slNo")
    PaymentTable queryPaymentDetailsBySlNo(int slNo);

    @Query("SELECT slNo, date_in_millis, pre_days FROM PaymentTable WHERE payment_status =:isStatus ORDER BY date_in_millis ASC")
    List<TimeDataTable> queryPaymentDateByStatus(boolean isStatus);

    @Query("SELECT * FROM PaymentTable ORDER BY date_in_millis ASC")
    List<PaymentTable> queryPayAmount();
}
