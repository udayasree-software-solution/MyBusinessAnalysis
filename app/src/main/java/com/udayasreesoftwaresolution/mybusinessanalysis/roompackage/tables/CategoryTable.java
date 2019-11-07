package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class CategoryTable {
    @PrimaryKey(autoGenerate = true)
    private int slNo;

    @ColumnInfo(name = "category_name")
    private String category_name;

    public CategoryTable(String category_name) {
        this.category_name = category_name;
    }

    public int getSlNo() {
        return slNo;
    }

    public void setSlNo(int slNo) {
        this.slNo = slNo;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }
}
