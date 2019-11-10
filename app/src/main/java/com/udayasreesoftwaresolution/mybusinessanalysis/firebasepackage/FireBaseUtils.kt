package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.*
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import java.math.BigDecimal

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
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
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
                            AppUtils.getCurrentDate(false)
                            if (AppUtils.timeInMillis > validityModel.validityDate) {
                                mFireBaseInterface.onValiditySuccessListener(true)
                            } else {
                                mFireBaseInterface.onValiditySuccessListener(false)
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
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
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
                        mFireBaseInterface.onSuccessReadVersionListener(dataSnapShot)
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

    private fun readVersionOfChildFromFireBase(child: String) {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(child)

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
                        var version = dataSnapShot.getValue(Double::class.java)!!
                        version += 0.001
                        val bigDecimal = BigDecimal(version).setScale(3, BigDecimal.ROUND_HALF_UP)

                        FirebaseDatabase.getInstance()
                            .getReference(AppUtils.OUTLET_NAME)
                            .child(FireBaseConstants.VERSION)
                            .child(child)
                            .setValue(bigDecimal.toDouble()) { error, _ ->
                                mFireBaseInterface.onSuccessWriteVersionListener(error == null)
                            }
                    }
                }
            })
        }
    }

    fun readPaymentFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PAYMENT)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.onSuccessReadPaymentDataListener(dataSnapShot)
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

    fun writePaymentToFireBase(paymentModel: PaymentModel) {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PAYMENT)
                .child(paymentModel.uniqueKey)
                .setValue(paymentModel) {error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase(FireBaseConstants.PAYMENT_VERSION)
                    }
                }
        }
    }

    fun readBusinessFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
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
                        mFireBaseInterface.onSuccessReadBusinessDataListener(dataSnapShot)
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

    fun writeBusinessToFireBase(businessModel: BusinessModel) {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.BUSINESS)
                .child(businessModel.selectedDate)
                .setValue(businessModel) {error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase(FireBaseConstants.BUSINESS_VERSION)
                    }
                }
        }
    }

    fun readClientFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
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
                        mFireBaseInterface.onSuccessReadClientDataListener(dataSnapShot)
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

    fun writeClientToFireBase(clientModel: ClientModel) {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.CLIENT)
                .push()
                .setValue(clientModel) {error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase(FireBaseConstants.CLIENT_VERSION)
                    }
                }
        }
    }

    fun readPurchaseFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
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
                        mFireBaseInterface.onSuccessReadPurchaseDataListener(dataSnapShot)
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

    fun writePurchaseTOFireBase(purchaseModel: PurchaseModel) {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PURCHASE)
                .child(purchaseModel.clientName)
                .child(purchaseModel.timeInMillis.toString())
                .setValue(purchaseModel) {error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase(FireBaseConstants.PURCHASE_VERSION)
                    }
                }
        }
    }

    fun readBusinessCategoryFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.BUSINESS_CATEGORY)
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
                        mFireBaseInterface.onSuccessReadBusinessCategoryDataListener(dataSnapShot)
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

    fun writeBusinessCategoryToFireBase(category : SingleEntityModel) {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.BUSINESS_CATEGORY)
                .push()
                .setValue(category) {error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase(FireBaseConstants.BUSINESS_CATEGORY_VERSION)
                    }
                }
        }
    }
}