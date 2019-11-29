package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;

import androidx.room.*;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable;

import java.util.List;

@Dao
public interface BusinessDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBusiness(BusinessTable businessTable);

    @Delete
    void deleteBusiness(BusinessTable businessTable);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateBusiness(BusinessTable businessTable);

    @Query("SELECT * FROM BusinessTable ORDER BY time_in_millis ASC")
    List<BusinessTable> queryBusinessList();

    @Query("SELECT * FROM BusinessTable WHERE selected_date =:selectedDate ORDER BY slNo ASC")
    List<BusinessTable> queryBusinessListByDate(String selectedDate);
}
