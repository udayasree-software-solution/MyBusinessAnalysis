package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage

import com.google.firebase.database.DataSnapshot

interface FireBaseInterface {
    fun onValiditySuccessListener(isValidityExpired : Boolean)
    fun onSuccessWriteVersionListener(status : Boolean)
    fun onSuccessReadVersionListener(dataSnapShot: DataSnapshot)

    fun onSuccessReadPaymentDataListener(dataSnapShot: DataSnapshot)
    fun onSuccessReadBusinessDataListener(dataSnapShot: DataSnapshot)
    fun onSuccessReadClientDataListener(dataSnapShot: DataSnapshot)
    fun onSuccessReadPurchaseDataListener(dataSnapShot: DataSnapshot)
    fun onSuccessReadBusinessCategoryDataListener(dataSnapShot: DataSnapshot)
}