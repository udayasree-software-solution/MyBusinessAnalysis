package com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage

import android.content.Context
import android.content.SharedPreferences
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants

class SharedPreferenceUtils(val mContext: Context) {
    private var sharedPreferenceUtils: SharedPreferenceUtils? = null

    private var sharedPreferences : SharedPreferences = mContext.getSharedPreferences("REMINDER_PREFERENCE", Context.MODE_PRIVATE)

    private val alarm_manager_ids = "ALARM_MANAGER_IDS"
    private val user_signin_status = "USER_SIGN_IN_STATUS"
    private val user_firebase_id = "USER_FIRE_BASE_CHILD_ID"
    private val user_outlet_name = "FIRE_BASE_USER_OUTLET"
    private val user_name_firebase = "FIRE_BASE_USER_NAME"
    private val user_mobile_number = "USER_MOBILE_NUMBER"
    private val user_signin_code_firebase = "FIRE_BASE_CODE"
    private val user_confirmation_status = "USER_CONFIRMATION_STATUS"
    private val user_admin_status = "USER_ADMIN_STATUS"
    private val outlet_logo_url = "OUTLET_LOGO_URL"
    private val outlet_banner_url = "OUTLET_BANNER_URL"

    private val payment_version = "PAYMENT_VERSION"
    private val business_version = "BUSINESS_VERSION"
    private val client_version = "CLIENT_VERSION"
    private val purchase_version = "PURCHASE_VERSION"
    private val business_name_version = "BUSINESS_NAME_VERSION"

    @Synchronized
    fun getInstance() : SharedPreferenceUtils {
        if (sharedPreferenceUtils == null){
            sharedPreferenceUtils =
                SharedPreferenceUtils(
                    mContext.applicationContext
                )
        }
        return sharedPreferenceUtils as SharedPreferenceUtils
    }



    fun setAlarmIds(ids : String){
        sharedPreferences.edit()?.putString(alarm_manager_ids, ids)?.apply()
    }

    fun getAlarmIds() : String? {
        return sharedPreferences.getString(alarm_manager_ids, "[]")
    }

    fun setUserSignInStatus(status : Boolean) {
        sharedPreferences.edit()?.putBoolean(user_signin_status, status)?.apply()
    }

    fun getUserSignInStatus() : Boolean {
        return sharedPreferences.getBoolean(user_signin_status, false)
    }

    fun setUserFireBaseChildId(childId : String) {
        sharedPreferences.edit()?.putString(user_firebase_id, childId)?.apply()
    }

    fun getUserFireBaseChildId() : String? {
        return sharedPreferences.getString(user_firebase_id, "")
    }

    fun setUserName(userName : String) {
        sharedPreferences.edit()?.putString( user_name_firebase, userName)?.apply()
    }

    fun getUserName() : String? {
        return sharedPreferences.getString(user_name_firebase, "")
    }

    fun setOutletName(outLet : String) {
        sharedPreferences.edit()?.putString(user_outlet_name, outLet)?.apply()
    }

    fun getOutletName() : String? {
        return sharedPreferences.getString(user_outlet_name, "")
    }

    fun setSignInCode(code : String) {
        sharedPreferences.edit()?.putString(user_signin_code_firebase, code)?.apply()
    }

    fun getSignInCode() : String? {
        return sharedPreferences.getString(user_signin_code_firebase, "")
    }

    fun setMobileNumber(mobile : String) {
        sharedPreferences.edit()?.putString(user_mobile_number, mobile)?.apply()
    }

    fun getMobileNumber() : String? {
        return sharedPreferences.getString(user_mobile_number, "")
    }

    fun setAdminStatus(isAdmin : Boolean) {
        sharedPreferences.edit()?.putBoolean(user_admin_status, isAdmin)?.apply()
    }

    fun getAdminStatus() : Boolean {
        return sharedPreferences.getBoolean(user_admin_status, false)
    }

    fun setOutletLogoUrl(url : String) {
        sharedPreferences.edit()?.putString(outlet_logo_url, url)?.apply()
    }

    fun getOutletLogoUrl() : String? {
        return sharedPreferences.getString(outlet_logo_url, FireBaseConstants.DEFAULT_LOGO)
    }

    fun setOutletBannerUrl(url : String) {
        sharedPreferences.edit()?.putString(outlet_banner_url, url)?.apply()
    }

    fun getOutletBannerUrl() : String? {
        return sharedPreferences.getString(outlet_banner_url, FireBaseConstants.DEFAULT_BANNER)
    }

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

    fun clearAlarmIDPreference() {
        setAlarmIds("[]")
    }

    fun clearPreference() {
        sharedPreferences.edit()?.clear()?.apply()
    }
}