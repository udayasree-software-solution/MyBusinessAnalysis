package com.udayasreesoftwaresolution.mybusinessanalysis

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseInterface
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseUtils
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (AppUtils.networkConnectivityCheck(this)) {
            initView()
        } else {
            exitDialog()
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

        AppUtils.OUTLET_NAME = sharedPreferenceUtils.getOutletName()!!
        imageLoaderUtils.displayImage(sharedPreferenceUtils.getOutletBannerUrl()!!, outletBanner)
        imageLoaderUtils.displayRoundImage(sharedPreferenceUtils.getOutletLogoUrl()!!, outletLogo)
        outletName.setText(AppUtils.OUTLET_NAME)

        fireBaseUtils = FireBaseUtils(
            this@SplashActivity,
            this
        ).getInstance()
        fireBaseUtils.readVersionFromFireBase()
    }

    private fun exitDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("No Internet Connection")
            .setMessage("Please connect to internet and try again")
            .setPositiveButton("Exit", DialogInterface.OnClickListener{ dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            })
        builder.create().show()
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
                sharedPreferenceUtils.setPaymentVersion(paymentVersion)
                fireBaseUtils.readPaymentFromFireBase()
            }

            if (sharedPreferenceUtils.getBusinessVersion()!! > businessVersion) {
                sharedPreferenceUtils.setBusinessVersion(businessVersion)
                fireBaseUtils.readBusinessFromFireBase()
            }

            if (sharedPreferenceUtils.getClientVersion()!! > clientVersion) {
                sharedPreferenceUtils.setClientVersion(clientVersion)
                fireBaseUtils.readClientFromFireBase()
            }

            if (sharedPreferenceUtils.getPurchaseVersion()!! > purchaseVersion) {
                sharedPreferenceUtils.setPurchaseVersion(purchaseVersion)
                fireBaseUtils.readPurchaseFromFireBase()
            }

            if (sharedPreferenceUtils.getBusinessNameVersion()!! > businessNameVersion) {
                sharedPreferenceUtils.setBusinessNameVersion(businessNameVersion)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun readPaymentDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Payment Room database*/
    }

    override fun readBusinessDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Business Room database*/
    }

    override fun readClientDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Client Room database*/
    }

    override fun readPurchaseDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Purchase Room database*/
    }

    override fun readBusinessNameDataListener(dataSnapShot: DataSnapshot) {

    }

    override fun writeVersionListener(status: Boolean) {

    }

    override fun errorListener(message: String) {
        /*TODO: pop-up dialog*/
    }
}
