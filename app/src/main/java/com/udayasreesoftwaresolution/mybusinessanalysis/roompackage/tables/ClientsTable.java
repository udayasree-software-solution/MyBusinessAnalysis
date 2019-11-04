package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class ClientsTable {
    @PrimaryKey(autoGenerate = true)
    private int slNo;

    @ColumnInfo(name = "client")
    private String client;

    @ColumnInfo(name = "category")
    private String category;

    public ClientsTable(int slNo, String client, String category) {
        this.slNo = slNo;
        this.client = client;
        this.category = category;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
