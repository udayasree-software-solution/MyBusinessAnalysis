package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable;

import java.util.List;

@Dao
public interface ClientDao {
    @Insert
    void insertClient(ClientsTable clientsTable);

    @Delete
    void deleteClient(ClientsTable clientsTable);

    @Update
    void updateClient(ClientsTable clientsTable);

    @Query("SELECT * FROM ClientsTable ORDER BY client ASC")
    List<ClientsTable> queryClientNamesList();
}