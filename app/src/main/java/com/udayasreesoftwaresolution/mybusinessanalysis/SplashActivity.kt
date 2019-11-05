package com.udayasreesoftwaresolution.mybusinessanalysis

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseInterface
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PaymentRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ImageLoaderUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.SharedPreferenceUtils

class SplashActivity : AppCompatActivity(),
    FireBaseInterface {

    private lateinit var outletBanner : ImageView
    private lateinit var outletLogo : ImageView
    private lateinit var outletName : TextView
    private lateinit var progressLayout : CardView

    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var imageLoaderUtils: ImageLoaderUtils
    private lateinit var fireBaseUtils : FireBaseUtils

    private var totalServerConnected = 0
    private var totalServerExecuted = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (AppUtils.networkConnectivityCheck(this)) {
            initView()
        } else {
            exitDialog("No Internet Connection", "Please connect to internet and try again", "Exit")
        }
    }

    private fun initView() {
        outletBanner = findViewById(R.id.splash_banner_img_id)
        outletLogo = findViewById(R.id.splash_logo_img_id)
        outletName = findViewById(R.id.splash_outlet_name_id)
        progressLayout = findViewById(R.id.splash_progress_layout_id)

        imageLoaderUtils = ImageLoaderUtils(
            this@SplashActivity
        ).getInstance()
        imageLoaderUtils.setupImageLoader()
        sharedPreferenceUtils = SharedPreferenceUtils(this@SplashActivity).getInstance()

        progressVisibility(true)
        AppUtils.OUTLET_NAME = sharedPreferenceUtils.getOutletName()!!
        imageLoaderUtils.displayImage(sharedPreferenceUtils.getOutletBannerUrl()!!, outletBanner)
        imageLoaderUtils.displayRoundImage(sharedPreferenceUtils.getOutletLogoUrl()!!, outletLogo)
        outletName.setText(AppUtils.OUTLET_NAME)

        fireBaseUtils = FireBaseUtils(
            this@SplashActivity,
            this
        ).getInstance()
        fireBaseUtils.readValidityFromFireBase()
    }

    private fun exitDialog(title : String, message : String, posBtn : String) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posBtn, DialogInterface.OnClickListener{ dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            })
        builder.create().show()
    }

    private fun progressVisibility(isShow : Boolean) {
        if (isShow) {
            progressLayout.visibility = View.VISIBLE
        } else {
            totalServerExecuted++
            if (totalServerExecuted >= totalServerConnected) {
                progressLayout.visibility = View.GONE
                totalServerConnected = 0
                totalServerExecuted = 0
            }
        }
    }

    override fun validityListener(isValidityExpired: Boolean) {
        if (isValidityExpired) {
            exitDialog("Renewal Premium", "Your premium to access this application has expired. Please Renewal your premium to continue", "Okay")
        } else {
            fireBaseUtils.readVersionFromFireBase()
        }
    }

    /*TODO:  Interface Implementation*/
    override fun readVersionListener(dataSnapShot: DataSnapshot) {
        try {
            val paymentVersion = dataSnapShot.child(FireBaseConstants.PAYMENT_VERSION).getValue(Float::class.java)!!
            val businessVersion = dataSnapShot.child(FireBaseConstants.BUSINESS_VERSION).getValue(Float::class.java)!!
            val clientVersion = dataSnapShot.child(FireBaseConstants.CLIENT_VERSION).getValue(Float::class.java)!!
            val purchaseVersion = dataSnapShot.child(FireBaseConstants.PURCHASE_VERSION).getValue(Float::class.java)!!
            val businessNameVersion = dataSnapShot.child(FireBaseConstants.BUSINESS_NAME_VERSION).getValue(Float::class.java)!!

            if (sharedPreferenceUtils.getPaymentVersion()!! > paymentVersion) {
                totalServerConnected++
                sharedPreferenceUtils.setPaymentVersion(paymentVersion)
                fireBaseUtils.readPaymentFromFireBase()
            }

            if (sharedPreferenceUtils.getBusinessVersion()!! > businessVersion) {
                totalServerConnected++
                sharedPreferenceUtils.setBusinessVersion(businessVersion)
                fireBaseUtils.readBusinessFromFireBase()
            }

            if (sharedPreferenceUtils.getClientVersion()!! > clientVersion) {
                totalServerConnected++
                sharedPreferenceUtils.setClientVersion(clientVersion)
                fireBaseUtils.readClientFromFireBase()
            }

            if (sharedPreferenceUtils.getPurchaseVersion()!! > purchaseVersion) {
                totalServerConnected++
                sharedPreferenceUtils.setPurchaseVersion(purchaseVersion)
                fireBaseUtils.readPurchaseFromFireBase()
            }

            if (sharedPreferenceUtils.getBusinessNameVersion()!! > businessNameVersion) {
                totalServerConnected++
                sharedPreferenceUtils.setBusinessNameVersion(businessNameVersion)
                fireBaseUtils.readBusinessNameFromFireBase()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun readPaymentDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Payment Room database*/
        try {
            val paymentRepository = PaymentRepository(this)
            paymentRepository.clearDataBase()
            for (element in dataSnapShot.children) {
                
            }
        } catch (e : Exception){
            e.printStackTrace()
        }
        progressVisibility(false)
    }

    override fun readBusinessDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Business Room database*/
        try {

        } catch (e : Exception){
            e.printStackTrace()
        }
        progressVisibility(false)
    }

    override fun readClientDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Client Room database*/
        try {

        } catch (e : Exception){
            e.printStackTrace()
        }
        progressVisibility(false)
    }

    override fun readPurchaseDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Purchase Room database*/
        try {

        } catch (e : Exception){
            e.printStackTrace()
        }
        progressVisibility(false)
    }

    override fun readBusinessNameDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Business Name Version [KKC, Socks etc]*/
        try {

        } catch (e : Exception){
            e.printStackTrace()
        }
        progressVisibility(false)
    }

    override fun writeVersionListener(status: Boolean) {

    }
}
