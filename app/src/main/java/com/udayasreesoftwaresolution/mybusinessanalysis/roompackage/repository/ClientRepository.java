package com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository;

import android.annotation.SuppressLint;
import androidx.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.persistence.ClientDataBasePersistence;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable;
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable;

import java.util.List;

@SuppressLint("StaticFieldLeak")
public class ClientRepository {

    private ClientDataBasePersistence clientDataBasePersistence;

    public ClientRepository(Context context) {
        clientDataBasePersistence = Room.databaseBuilder(context, ClientDataBasePersistence.class,
                "client_database")
                .build();
    }

    public void insertClient(final ClientsTable clientsTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clientDataBasePersistence.clientDaoAccess().insertClient(clientsTable);
                return null;
            }
        }.execute();
    }

    public void updateClient(final ClientsTable clientsTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clientDataBasePersistence.clientDaoAccess().updateClient(clientsTable);
                return null;
            }
        }.execute();
    }

    public void deleteClient(final ClientsTable clientsTable) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clientDataBasePersistence.clientDaoAccess().deleteClient(clientsTable);
                return null;
            }
        }.execute();
    }

    public List<String> queryClientNamesList() {
        return clientDataBasePersistence.clientDaoAccess().queryClientNamesList();
    }

    public List<ClientsTable> queryClientList() {
        return clientDataBasePersistence.clientDaoAccess().queryClientList();
    }

    public void clearDataBase() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                clientDataBasePersistence.isOpen();
                clientDataBasePersistence.clearAllTables();
                clientDataBasePersistence.close();
                return null;
            }
        }.execute();
    }
}
