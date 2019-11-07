package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables;


import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity
public class TimeDataTable {

    @ColumnInfo(name = "date_in_millis")
    private long date;

    @ColumnInfo(name = "pre_days")
    private int days;

    public TimeDataTable(long date, int days) {
        this.date = date;
        this.days = days;
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
