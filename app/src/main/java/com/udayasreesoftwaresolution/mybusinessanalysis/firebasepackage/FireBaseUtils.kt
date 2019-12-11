package com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage

import android.content.Context
import android.widget.QuickContactBadge
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.*
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.CompanyModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.math.BigDecimal

class FireBaseUtils(private val mContext: Context, private val mFireBaseInterface: FireBaseInterface) {
    fun readValidityFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VALIDITY)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    mFireBaseInterface.onFailureFireBaseListener()
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
                        mFireBaseInterface.onFailureFireBaseListener()
                    }
                }
            })
        }
    }

    fun readOutletDetailsFromFireBase() {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()){
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.OUTLET_PROFILE)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    mFireBaseInterface.onFailureFireBaseListener()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val companyModel = dataSnapshot.getValue(CompanyModel::class.java)
                        if (companyModel != null) {
                            with(companyModel) {
                                AppUtils.outlet_address = outletAddress
                                AppUtils.outlet_banner = outletBanner
                                AppUtils.outlet_logo = outletLogo
                                AppUtils.outlet_contact = outletContact
                            }
                        }
                    } else {
                        mFireBaseInterface.onFailureFireBaseListener()
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
                    mFireBaseInterface.onFailureFireBaseListener()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.onSuccessReadVersionListener(dataSnapShot)
                    } else {
                        mFireBaseInterface.onFailureFireBaseListener()
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
                    mFireBaseInterface.onFailureFireBaseListener()
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
                        mFireBaseInterface.onFailureFireBaseListener()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    mFireBaseInterface.onFailureFireBaseListener()
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
                    mFireBaseInterface.onFailureFireBaseListener()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.onSuccessReadBusinessDataListener(dataSnapShot)
                    } else {
                        mFireBaseInterface.onFailureFireBaseListener()
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
                    mFireBaseInterface.onFailureFireBaseListener()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.onSuccessReadClientDataListener(dataSnapShot)
                    } else {
                        mFireBaseInterface.onFailureFireBaseListener()
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
                    mFireBaseInterface.onFailureFireBaseListener()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.onSuccessReadPurchaseDataListener(dataSnapShot)
                    } else {
                        mFireBaseInterface.onFailureFireBaseListener()
                    }
                }
            })
        }
    }

    fun writePurchaseToFireBase(purchaseModel: PurchaseModel) {
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
                    mFireBaseInterface.onFailureFireBaseListener()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        mFireBaseInterface.onSuccessReadBusinessCategoryDataListener(dataSnapShot)
                    } else {
                        mFireBaseInterface.onFailureFireBaseListener()
                    }
                }
            })
        }
    }
}