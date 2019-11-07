package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables;

import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BusinessTable {
    @PrimaryKey(autoGenerate = true)
    private int slNo;

    @ColumnInfo(name = "asc_order")
    private int ascOrder;

    @ColumnInfo(name = "business_amount")
    private int amount;

    @ColumnInfo(name = "business_name")
    private String businessName;

    @ColumnInfo(name = "selected_date")
    private String selectedDate;

    @ColumnInfo(name = "time_in_millis")
    private long timeInMillis;

    public BusinessTable(int ascOrder, int amount, String businessName, String selectedDate, long timeInMillis) {
        this.ascOrder = ascOrder;
        this.amount = amount;
        this.businessName = businessName;
        this.selectedDate = selectedDate;
        this.timeInMillis = timeInMillis;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public int getAscOrder() {
        return ascOrder;
    }

    public void setAscOrder(int ascOrder) {
        this.ascOrder = ascOrder;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }
}
