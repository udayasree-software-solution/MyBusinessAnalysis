package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class TimeDataTable {

    @PrimaryKey(autoGenerate = true)
    private int slNo;

    @ColumnInfo(name = "date_in_millis")
    private long date;

    @ColumnInfo(name = "pre_days")
    private int days;

    public TimeDataTable(long date, int days) {
        this.date = date;
        this.days = days;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
