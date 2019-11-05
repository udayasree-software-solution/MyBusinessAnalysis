package com.udayasreesoftwaresolution.mybusinessanalysis

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseInterface
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.ClientModel
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.PaymentModel
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.SingleEntityModel
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.ClientRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PaymentRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PurchaseRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ImageLoaderUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.SharedPreferenceUtils

class SplashActivity : AppCompatActivity(),
    FireBaseInterface {

    private lateinit var outletBanner: ImageView
    private lateinit var outletLogo: ImageView
    private lateinit var outletName: TextView
    private lateinit var progressLayout: CardView

    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var imageLoaderUtils: ImageLoaderUtils
    private lateinit var fireBaseUtils: FireBaseUtils

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

        progressLayout.visibility = View.VISIBLE

        AppUtils.OUTLET_NAME = sharedPreferenceUtils.getOutletName()!!
        imageLoaderUtils.displayImage(sharedPreferenceUtils.getOutletBannerUrl()!!, outletBanner)
        imageLoaderUtils.displayRoundImage(sharedPreferenceUtils.getOutletLogoUrl()!!, outletLogo)
        outletName.setText(AppUtils.OUTLET_NAME)

        if (sharedPreferenceUtils.getUserSignInStatus() && AppUtils.OUTLET_NAME.isNotEmpty()) {
            fireBaseUtils = FireBaseUtils(
                this@SplashActivity,
                this
            ).getInstance()
            fireBaseUtils.readValidityFromFireBase()
        } else {
            progressLayout.visibility = View.GONE
            /*TODO: Intent to SignIn Activity*/
        }
    }

    private fun exitDialog(title: String, message: String, posBtn: String) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posBtn, DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            })
        builder.create().show()
    }

    private fun saveDetailsToDB() {
        totalServerExecuted++
        if (totalServerExecuted >= totalServerConnected) {
            totalServerConnected = 0
            totalServerExecuted = 0
            Handler().postDelayed({
                /*TODO: Intent to HOME Activity*/
                progressLayout.visibility = View.GONE

            }, 7000)
        }
    }

    override fun validityListener(isValidityExpired: Boolean) {
        if (isValidityExpired) {
            exitDialog(
                "Renewal Premium",
                "Your premium to access this application has expired. Please Renewal your premium to continue",
                "Okay"
            )
        } else {
            fireBaseUtils.readVersionFromFireBase()
        }
    }

    /*TODO:  Interface Implementation*/
    override fun readVersionListener(dataSnapShot: DataSnapshot) {
        try {
            val paymentVersion = dataSnapShot.child(FireBaseConstants.PAYMENT_VERSION).getValue(Double::class.java)!!
            val businessVersion = dataSnapShot.child(FireBaseConstants.BUSINESS_VERSION).getValue(Double::class.java)!!
            val clientVersion = dataSnapShot.child(FireBaseConstants.CLIENT_VERSION).getValue(Double::class.java)!!
            val purchaseVersion = dataSnapShot.child(FireBaseConstants.PURCHASE_VERSION).getValue(Double::class.java)!!
            val businessNameVersion =
                dataSnapShot.child(FireBaseConstants.BUSINESS_CATEGORY_VERSION).getValue(Double::class.java)!!

            if (sharedPreferenceUtils.getPaymentVersion()!! > paymentVersion.toFloat()) {
                totalServerConnected++
                sharedPreferenceUtils.setPaymentVersion(paymentVersion.toFloat())
                fireBaseUtils.readPaymentFromFireBase()
            }

            if (sharedPreferenceUtils.getBusinessVersion()!! > businessVersion.toFloat()) {
                totalServerConnected++
                sharedPreferenceUtils.setBusinessVersion(businessVersion.toFloat())
                fireBaseUtils.readBusinessFromFireBase()
            }

            if (sharedPreferenceUtils.getClientVersion()!! > clientVersion.toFloat()) {
                totalServerConnected++
                sharedPreferenceUtils.setClientVersion(clientVersion.toFloat())
                fireBaseUtils.readClientFromFireBase()
            }

            if (sharedPreferenceUtils.getPurchaseVersion()!! > purchaseVersion.toFloat()) {
                totalServerConnected++
                sharedPreferenceUtils.setPurchaseVersion(purchaseVersion.toFloat())
                fireBaseUtils.readPurchaseFromFireBase()
            }

            if (sharedPreferenceUtils.getBusinessNameVersion()!! > businessNameVersion.toFloat()) {
                totalServerConnected++
                sharedPreferenceUtils.setBusinessNameVersion(businessNameVersion.toFloat())
                fireBaseUtils.readBusinessCategoryFromFireBase()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (totalServerConnected == 0) {
            saveDetailsToDB()
        }
    }

    override fun readPaymentDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Payment Room database*/
        try {
            val paymentRepository = PaymentRepository(this@SplashActivity)
            paymentRepository.clearDataBase()
            for (element in dataSnapShot.children) {
                val paymentModel = element.getValue(PaymentModel::class.java)
                if (paymentModel != null) {
                    with(paymentModel) {
                        paymentRepository.insertTask(
                            PaymentTable(
                                uniqueKey,
                                clientName,
                                payAmount,
                                chequeNumber,
                                dateInMillis,
                                payStatus,
                                preDays
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun readBusinessDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Business Room database*/
        try {

        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun readClientDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Client Room database*/
        try {
            val clientRepository = ClientRepository(this@SplashActivity)
            clientRepository.clearDataBase()
            for (element in dataSnapShot.children) {
                val clientModel = element.getValue(ClientModel::class.java)
                if (clientModel != null) {

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun readPurchaseDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Purchase Room database*/
        try {
            val purchaseRepository = PurchaseRepository(this@SplashActivity)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun readBusinessCategoryDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Business Name Version [KKC, Socks etc]*/
        try {
            val categoryRepository = CategoryRepository(this@SplashActivity)
            categoryRepository.clearDataBase()
            categoryRepository.insertTask(CategoryTable(AppUtils.OUTLET_NAME))
            for (element in dataSnapShot.children) {
                val entityModel = element.getValue(SingleEntityModel::class.java)
                if (entityModel != null) {
                    categoryRepository.insertTask(CategoryTable(entityModel.inputData))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun writeVersionListener(status: Boolean) {

    }
}
