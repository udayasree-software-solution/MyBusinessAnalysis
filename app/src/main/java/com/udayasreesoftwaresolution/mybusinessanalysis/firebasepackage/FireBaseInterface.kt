package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage

import com.google.firebase.database.DataSnapshot

interface FireBaseInterface {
    fun writeVersionListener(status : Boolean)
    fun readVersionListener(dataSnapShot: DataSnapshot)
    fun errorListener(message : String)

    fun readPaymentDataListener(dataSnapShot: DataSnapshot)
    fun readBusinessDataListener(dataSnapShot: DataSnapshot)
    fun readClientDataListener(dataSnapShot: DataSnapshot)
    fun readPurchaseDataListener(dataSnapShot: DataSnapshot)
    fun readBusinessNameDataListener(dataSnapShot: DataSnapshot)
}