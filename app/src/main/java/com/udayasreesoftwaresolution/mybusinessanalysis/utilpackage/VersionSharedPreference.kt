package com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage

import android.content.Context
import android.content.SharedPreferences
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants

class VersionSharedPreference(val mContext: Context) {
    val sharedPreferences : SharedPreferences = mContext.getSharedPreferences("VERSION_PREFERENCE", Context.MODE_PRIVATE)

    fun setPaymentVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(FireBaseConstants.PAYMENT_VERSION, version)?.apply()
    }

    fun getPaymentVersion() : Float? {
        return sharedPreferences.getFloat(FireBaseConstants.PAYMENT_VERSION, 0.0f)
    }

    fun setBusinessVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(FireBaseConstants.BUSINESS_VERSION, version)?.apply()
    }

    fun getBusinessVersion() : Float? {
        return sharedPreferences.getFloat(FireBaseConstants.BUSINESS_VERSION, 0.0f)
    }

    fun setClientVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(FireBaseConstants.CLIENT_VERSION, version)?.apply()
    }

    fun getClientVersion() : Float? {
        return sharedPreferences.getFloat(FireBaseConstants.CLIENT_VERSION, 0.0f)
    }

    fun setPurchaseVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(FireBaseConstants.PURCHASE_VERSION, version)?.apply()
    }

    fun getPurchaseVersion() : Float? {
        return sharedPreferences.getFloat(FireBaseConstants.PURCHASE_VERSION, 0.0f)
    }

    fun setBusinessNameVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(FireBaseConstants.BUSINESS_CATEGORY_VERSION, version)?.apply()
    }

    fun getBusinessNameVersion() : Float? {
        return sharedPreferences.getFloat(FireBaseConstants.BUSINESS_CATEGORY_VERSION, 0.0f)
    }

    fun clearPreference() {
        sharedPreferences.edit()?.clear()?.apply()
    }
}