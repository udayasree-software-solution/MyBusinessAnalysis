package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.ValidityModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils

class FireBaseUtils(
    private val mContext: Context,
    private val mFireBaseInterface: FireBaseInterface
) {
    private var fireBaseUtils: FireBaseUtils? = null
    @Synchronized
    fun getInstance(): FireBaseUtils {
        if (fireBaseUtils == null) {
            fireBaseUtils =
                FireBaseUtils(
                    mContext.applicationContext,
                    mFireBaseInterface
                )
        }
        return fireBaseUtils as FireBaseUtils
    }

    fun readValidityFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VALIDITY)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        mContext,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        val validityModel = dataSnapShot.getValue(ValidityModel::class.java)
                        if (validityModel != null) {
                            AppUtils.getCurrentDate(true)
                            if (AppUtils.timeInMillis > validityModel.validityDate) {
                                mFireBaseInterface.validityListener(true)
                            } else {
                                mFireBaseInterface.validityListener(false)
                            }
                        }
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    fun readVersionFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        mContext,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.readVersionListener(dataSnapShot)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    fun writeVersionToFireBase(version: Int, child: String) {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(child)
                .setValue(version) { error, _ ->
                    mFireBaseInterface.writeVersionListener(error == null)
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
                        mFireBaseInterface.readPaymentDataListener(dataSnapShot)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        mContext,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
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
                    Toast.makeText(
                        mContext,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.readBusinessDataListener(dataSnapShot)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
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
                    Toast.makeText(
                        mContext,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.readClientDataListener(dataSnapShot)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
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
                    Toast.makeText(
                        mContext,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.readPurchaseDataListener(dataSnapShot)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    fun readBusinessNameFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.BUSINESS_NAME)
            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        mContext,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.readBusinessNameDataListener(dataSnapShot)
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }
    }

    fun writePaymentToFireBase() {

    }

    fun writeBusinessToFireBase(selectedDate: String) {

    }
}