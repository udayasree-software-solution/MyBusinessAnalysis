package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class PaymentTable {

    @PrimaryKey(autoGenerate = true)
    private int slNo;

    @ColumnInfo(name = "unique_key")
    private String uniqueKey;

    @ColumnInfo(name = "client_name")
    private String clientName;

    @ColumnInfo(name = "pay_amount")
    private String payAmount;

    @ColumnInfo(name = "Cheque_no")
    private String chequeNumber;

    @ColumnInfo(name = "date_in_millis")
    private long dateInMillis;

    @ColumnInfo(name = "payment_status")
    private boolean paymentStatus;

    @ColumnInfo(name = "pre_days")
    private int preDays;

    public PaymentTable(String uniqueKey, String clientName, String payAmount,
                        String chequeNumber, long dateInMillis, boolean paymentStatus, int preDays) {
        this.uniqueKey = uniqueKey;
        this.clientName = clientName;
        this.payAmount = payAmount;
        this.chequeNumber = chequeNumber;
        this.dateInMillis = dateInMillis;
        this.paymentStatus = paymentStatus;
        this.preDays = preDays;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(String payAmount) {
        this.payAmount = payAmount;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public long getDateInMillis() {
        return dateInMillis;
    }

    public void setDateInMillis(long dateInMillis) {
        this.dateInMillis = dateInMillis;
    }

    public boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public int getPreDays() {
        return preDays;
    }

    public void setPreDays(int preDays) {
        this.preDays = preDays;
    }
}
