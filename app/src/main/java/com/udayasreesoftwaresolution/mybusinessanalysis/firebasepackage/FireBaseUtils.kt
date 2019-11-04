package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils

class FireBaseUtils(private val mContext : Context, private val mFireBaseInterface: FireBaseInterface) {
    private var fireBaseUtils: FireBaseUtils? = null
    private val firebaseInterface = mFireBaseInterface
    @Synchronized
    fun getInstance() : FireBaseUtils {
        if (fireBaseUtils == null){
            fireBaseUtils =
                FireBaseUtils(
                    mContext.applicationContext,
                    mFireBaseInterface
                )
        }
        return fireBaseUtils as FireBaseUtils
    }

    fun readVersionFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    firebaseInterface.errorListener("Server connection failed. Please try again")
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        firebaseInterface.readVersionListener(dataSnapShot)
                    }
                }
            })
        }
    }

    fun writeVersionToFireBase(version : Int, child : String) {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(child)
                .setValue(version) {error, _ ->
                    firebaseInterface.writeVersionListener(error == null)
                }
        }
    }

    fun readPaymentFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PAYMENT)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        firebaseInterface.readPaymentDataListener(dataSnapShot)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    firebaseInterface.errorListener("Server connection failed. Please try again")
                }
            })
        }
    }

    fun readBusinessFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.BUSINESS)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    firebaseInterface.errorListener("Server connection failed. Please try again")
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        firebaseInterface.readBusinessDataListener(dataSnapShot)
                    }
                }
            })
        }
    }

    fun readClientFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.CLIENT)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    firebaseInterface.errorListener("Server connection failed. Please try again")
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        firebaseInterface.readClientDataListener(dataSnapShot)
                    }
                }
            })
        }
    }

    fun readPurchaseFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PURCHASE)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    firebaseInterface.errorListener("Server connection failed. Please try again")
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        firebaseInterface.readPurchaseDataListener(dataSnapShot)
                    }
                }
            })
        }
    }

    fun writePaymentToFireBase() {

    }

    fun writeBusinessToFireBase(selectedDate : String) {

    }
}