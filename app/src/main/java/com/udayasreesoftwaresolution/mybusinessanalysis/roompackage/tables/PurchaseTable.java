package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables;



import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PurchaseTable {
    @PrimaryKey(autoGenerate = true)
    private int slNo;

    @ColumnInfo(name = "date_of_purchase")
    private String dateOfPurchase;

    @ColumnInfo(name = "client_name")
    private String clientName;

    @ColumnInfo(name = "bill_number")
    private String billNo;

    @ColumnInfo(name = "bill_amount")
    private String billAmount;

    @ColumnInfo(name = "time_in_millis")
    private long timeInMillis;

    public PurchaseTable(int slNo, String dateOfPurchase, String clientName, String billNo, String billAmount, long timeInMillis) {
        this.slNo = slNo;
        this.dateOfPurchase = dateOfPurchase;
        this.clientName = clientName;
        this.billNo = billNo;
        this.billAmount = billAmount;
        this.timeInMillis = timeInMillis;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getDateOfPurchase() {
        return dateOfPurchase;
    }

    public void setDateOfPurchase(String dateOfPurchase) {
        this.dateOfPurchase = dateOfPurchase;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillAmount() {
        return billAmount;
    }

    public void setBillAmount(String billAmount) {
        this.billAmount = billAmount;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}
