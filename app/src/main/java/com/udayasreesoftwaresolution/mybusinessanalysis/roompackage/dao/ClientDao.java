package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.dao;

import androidx.room.*;

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