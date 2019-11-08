package com.udayasreesoftwaresolution.admin.firebasepackage

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.udayasreesoftwaresolution.admin.firebasepackage.models.ValidityModel
import com.udayasreesoftwaresolution.admin.utilspackage.AppUtils

class FireBaseUtils(val mContext: Context, val fireBaseInterface : FireBaseInterface) {
    private var fireBaseUtils: FireBaseUtils? = null

    @Synchronized
    fun getInstance(): FireBaseUtils {
        if (fireBaseUtils == null) {
            fireBaseUtils = FireBaseUtils(mContext, fireBaseInterface)
        }
        return fireBaseUtils as FireBaseUtils
    }

    fun writeValidityToFireBase(outletName : String, validityModel: ValidityModel) {
        if (AppUtils.networkConnectivityCheck(mContext) && outletName.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(outletName)
                .child(FireBaseConstants.VALIDITY)
                .setValue(validityModel) {error, _ ->
                    if (error == null) {
                        fireBaseInterface.onValiditySuccessListener()
                    } else {
                        Toast.makeText(
                            mContext,
                            "Server connection failed. Please try again",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
    }

    fun initVersionToFireBase(outletName: String) {
        if (AppUtils.networkConnectivityCheck(mContext) && outletName.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(outletName)
                .child(FireBaseConstants.VERSION)

            fireBaseReference.child(FireBaseConstants.PAYMENT_VERSION)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.BUSINESS_VERSION)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.CLIENT_VERSION)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.PURCHASE_VERSION)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.BUSINESS_CATEGORY_VERSION)
                .setValue(0)
        }
    }

    fun initTotalToFireBase(outletName: String) {
        if (AppUtils.networkConnectivityCheck(mContext) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(outletName)
                .child(FireBaseConstants.TOTAL_AMOUNT)

            fireBaseReference.child(FireBaseConstants.PAYABLE_AMOUNT)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.PAID_AMOUNT)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.EXPENSES_AMOUNT)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.GROSS_AMOUNT)
                .setValue(0)

            fireBaseReference.child(FireBaseConstants.PURCHASE_AMOUNT)
                .setValue(0)
        }
    }
}