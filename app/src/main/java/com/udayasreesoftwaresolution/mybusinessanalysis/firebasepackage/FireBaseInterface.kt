package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage

import com.google.firebase.database.DataSnapshot

interface FireBaseInterface {
    fun validityListener(isValidityExpired : Boolean)

    fun writeVersionListener(status : Boolean)
    fun readVersionListener(dataSnapShot: DataSnapshot)

    fun readPaymentDataListener(dataSnapShot: DataSnapshot)
    fun readBusinessDataListener(dataSnapShot: DataSnapshot)
    fun readClientDataListener(dataSnapShot: DataSnapshot)
    fun readPurchaseDataListener(dataSnapShot: DataSnapshot)
    fun readBusinessCategoryDataListener(dataSnapShot: DataSnapshot)
}