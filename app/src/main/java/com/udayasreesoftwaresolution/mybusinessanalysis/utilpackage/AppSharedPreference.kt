package com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage

import android.content.Context
import android.content.SharedPreferences
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants

class AppSharedPreference(val mContext: Context) {

    private var sharedPreferences : SharedPreferences = mContext.getSharedPreferences("APP_PREFERENCE", Context.MODE_PRIVATE)

    private val alarm_manager_ids = "ALARM_MANAGER_IDS"
    private val user_signin_status = "USER_SIGN_IN_STATUS"
    private val user_firebase_id = "USER_FIRE_BASE_CHILD_ID"
    private val user_outlet_name = "FIRE_BASE_USER_OUTLET"
    private val user_name_firebase = "FIRE_BASE_USER_NAME"
    private val user_mobile_number = "USER_MOBILE_NUMBER"
    private val user_signin_code_firebase = "FIRE_BASE_CODE"
    private val user_login_type = "USER_LOGIN_TYPE"
    private val user_admin_status = "USER_ADMIN_STATUS"
    private val device_login_code = "DEVICE_LOGIN_CODE"


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

    fun setLoginType(loginType : String) {
        sharedPreferences.edit()?.putString(user_login_type, loginType)?.apply()
    }

    fun getLoginType() : String? {
        return sharedPreferences.getString(user_login_type,"")
    }

    fun setLoginDeviceCode(code : String) {
        sharedPreferences.edit()?.putString(device_login_code, code)?.apply()
    }

    fun getLoginDeviceCode() : String? {
        return sharedPreferences.getString(device_login_code,"")
    }

    fun clearAlarmIDPreference() {
        setAlarmIds("[]")
    }

    fun clearPreference() {
        sharedPreferences.edit()?.clear()?.apply()
    }
}