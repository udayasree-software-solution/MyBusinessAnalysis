package com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage

import android.content.Context
import android.content.SharedPreferences

class VersionSharedPreference(val mContext: Context) {
    val sharedPreferences : SharedPreferences = mContext.getSharedPreferences("VERSION_PREFERENCE", Context.MODE_PRIVATE)

    private val payment_version = "PAYMENT_VERSION"
    private val business_version = "BUSINESS_VERSION"
    private val client_version = "CLIENT_VERSION"
    private val purchase_version = "PURCHASE_VERSION"
    private val business_name_version = "BUSINESS_NAME_VERSION"

    fun setPaymentVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(payment_version, version)?.apply()
    }

    fun getPaymentVersion() : Float? {
        return sharedPreferences.getFloat(payment_version, 0.0f)
    }

    fun setBusinessVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(business_version, version)?.apply()
    }

    fun getBusinessVersion() : Float? {
        return sharedPreferences.getFloat(business_version, 0.0f)
    }

    fun setClientVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(client_version, version)?.apply()
    }

    fun getClientVersion() : Float? {
        return sharedPreferences.getFloat(client_version, 0.0f)
    }

    fun setPurchaseVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(purchase_version, version)?.apply()
    }

    fun getPurchaseVersion() : Float? {
        return sharedPreferences.getFloat(purchase_version, 0.0f)
    }

    fun setBusinessNameVersion(version: Float) {
        sharedPreferences.edit()?.putFloat(business_name_version, version)?.apply()
    }

    fun getBusinessNameVersion() : Float? {
        return sharedPreferences.getFloat(business_name_version, 0.0f)
    }

    fun clearPreference() {
        sharedPreferences.edit()?.clear()?.apply()
    }
}