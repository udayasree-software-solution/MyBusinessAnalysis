package com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseInterface
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.*
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.*
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.*
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.*

class SplashActivity : AppCompatActivity(), FireBaseInterface {

    private lateinit var outletBanner: ImageView
    private lateinit var outletLogo: ImageView
    private lateinit var outletName: TextView
    private lateinit var progressBox: ProgressBox

    private lateinit var appSharedPreference: AppSharedPreference
    private lateinit var versionSharedPreference: VersionSharedPreference
    private lateinit var imageLoaderUtils: ImageLoaderUtils
    private lateinit var fireBaseUtils: FireBaseUtils

    private var totalServerConnected = 0
    private var totalServerExecuted = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        screenSize()
        if (AppUtils.networkConnectivityCheck(this)) {
            initView()
        } else {
            exitDialog("No Internet Connection", "Please connect to internet and try again", "Exit")
        }
    }

    private fun screenSize() {
        val size = Point()
        val w = windowManager

        w.defaultDisplay.getSize(size)
        AppUtils.SCREEN_WIDTH = size.x
        AppUtils.SCREEN_HEIGHT = size.y
    }

    private fun initView() {
        outletBanner = findViewById(R.id.splash_banner_img_id)
        outletLogo = findViewById(R.id.splash_logo_img_id)
        outletName = findViewById(R.id.splash_outlet_name_id)

        outletName.typeface = AppUtils.getTypeFace(this, ConstantUtils.BLACKJACK)

        imageLoaderUtils = ImageLoaderUtils(this@SplashActivity)
        imageLoaderUtils.setupImageLoader()
        appSharedPreference = AppSharedPreference(this@SplashActivity)
        versionSharedPreference = VersionSharedPreference(this@SplashActivity)
        progressBox = ProgressBox(this)

        AppUtils.OUTLET_NAME = appSharedPreference.getOutletName()!!

        imageLoaderUtils.displayImage(FireBaseConstants.DEFAULT_BANNER, outletBanner)
        imageLoaderUtils.displayRoundImage(FireBaseConstants.DEFAULT_LOGO, outletLogo)
        outletName.text = AppUtils.OUTLET_NAME
        fireBaseUtils = FireBaseUtils(
            this@SplashActivity,
            this
        )
        val contact = appSharedPreference.getMobileNumber()!!

        if (AppUtils.networkConnectivityCheck(this) && AppUtils.OUTLET_NAME.isNotEmpty() && contact.isNotEmpty() && appSharedPreference.getUserSignInStatus()) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.USERS)
                .child(contact)
            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                    exitDialog("User Not Exist", "Please contact ADMIN for your account verification", "Okay")
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val userSignInModel = dataSnapshot.getValue(UserSignInModel::class.java)
                        if (appSharedPreference.getAdminStatus() || appSharedPreference.getLoginType() == ConstantUtils.isAdminClientAccess) {
                            if (userSignInModel != null && userSignInModel.deviceLoginCode == appSharedPreference.getLoginDeviceCode()) {
                                fireBaseUtils.readValidityFromFireBase()
                            } else {
                                appSharedPreference.clearPreference()
                                exitDialog("Account already used", "Your account is login in another device. Contact ADMIN", "Okay")
                            }
                        } else {
                            /*TODO: Launch activity for employees to fill details like - login and logout time, salary date, salary credited etc*/
                            progressBox.dismiss()
                        }
                    } else {
                        progressBox.dismiss()
                        exitDialog("User Not Exist", "Please contact ADMIN for your account verification", "Okay")
                    }
                }
            })
        } else {
            startActivityForResult(Intent(this@SplashActivity, SignInActivity::class.java), ConstantUtils.SIGNIN_REQUEST_CODE)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun exitDialog(title: String, message: String, posBtn: String) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(posBtn) { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            }
        builder.create().show()
    }

    private fun saveDetailsToDB() {
        totalServerExecuted++
        if (totalServerExecuted >= totalServerConnected) {
            totalServerConnected = 0
            totalServerExecuted = 0
            Handler().postDelayed({
                progressBox.dismiss()
                if (appSharedPreference.getOutletDetailsStatus()) {
                    startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, OutletSettingsActivity::class.java))
                }
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                finish()
            }, 7000)
        }
    }

    override fun onValiditySuccessListener(isValidityExpired: Boolean) {
        if (isValidityExpired) {
            progressBox.dismiss()
            exitDialog(
                "Renewal Premium",
                "Your premium to access the application has expired. Please Renewal your premium to continue",
                "Okay"
            )
        } else {
            fireBaseUtils.readOutletDetailsFromFireBase()
            fireBaseUtils.readVersionFromFireBase()
        }
    }

    /*TODO:  Interface Implementation*/
    override fun onSuccessReadVersionListener(dataSnapShot: DataSnapshot) {
        try {
            val paymentVersion = dataSnapShot.child(FireBaseConstants.PAYMENT_VERSION).getValue(Double::class.java)!!
            val businessVersion = dataSnapShot.child(FireBaseConstants.BUSINESS_VERSION).getValue(Double::class.java)!!
            val clientVersion = dataSnapShot.child(FireBaseConstants.CLIENT_VERSION).getValue(Double::class.java)!!
            val purchaseVersion = dataSnapShot.child(FireBaseConstants.PURCHASE_VERSION).getValue(Double::class.java)!!
            val businessNameVersion =
                dataSnapShot.child(FireBaseConstants.BUSINESS_CATEGORY_VERSION).getValue(Double::class.java)!!

            if (paymentVersion.toFloat() > versionSharedPreference.getPaymentVersion()!!) {
                totalServerConnected++
                versionSharedPreference.setPaymentVersion(paymentVersion.toFloat())
                fireBaseUtils.readPaymentFromFireBase()
            }

            if (businessVersion.toFloat() > versionSharedPreference.getBusinessVersion()!!) {
                totalServerConnected++
                versionSharedPreference.setBusinessVersion(businessVersion.toFloat())
                fireBaseUtils.readBusinessFromFireBase()
            }

            if (clientVersion.toFloat() > versionSharedPreference.getClientVersion()!!) {
                totalServerConnected++
                versionSharedPreference.setClientVersion(clientVersion.toFloat())
                fireBaseUtils.readClientFromFireBase()
            }

            if (purchaseVersion.toFloat() > versionSharedPreference.getPurchaseVersion()!!) {
                totalServerConnected++
                versionSharedPreference.setPurchaseVersion(purchaseVersion.toFloat())
                fireBaseUtils.readPurchaseFromFireBase()
            }

            if (businessNameVersion.toFloat() > versionSharedPreference.getBusinessNameVersion()!!) {
                totalServerConnected++
                versionSharedPreference.setBusinessNameVersion(businessNameVersion.toFloat())
                fireBaseUtils.readBusinessCategoryFromFireBase()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (totalServerConnected == 0) {
            saveDetailsToDB()
        }
    }

    override fun onSuccessReadPaymentDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Payment Room database*/
        try {
            val paymentRepository = PaymentRepository(this@SplashActivity)
            paymentRepository.clearDataBase()
            Handler().postDelayed({
                for (element in dataSnapShot.children) {
                    val paymentModel = element.getValue(PaymentModel::class.java)
                    if (paymentModel != null) {
                        with(paymentModel) {
                            paymentRepository.insertTask(
                                PaymentTable(
                                    uniqueKey,
                                    clientName,
                                    categoryName,
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
            },3000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun onSuccessReadBusinessDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Business Room database*/
        try {
            val businessRepository = BusinessRepository(this@SplashActivity)
            businessRepository.clearDataBase()
            Handler().postDelayed({
                for (element in dataSnapShot.children) {
                    for (i in 0 until element.childrenCount) {
                        val businessModel = element.child(i.toString()).getValue(BusinessModel::class.java)
                        if (businessModel != null) {
                            with(businessModel){
                                businessRepository.insertBusiness(BusinessTable(ascOrder, amount, businessName, selectedDate, timeInMillis))
                            }
                        }
                    }
                }
            }, 3000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun onSuccessReadClientDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Client Room database*/
        try {
            val clientRepository = ClientRepository(this@SplashActivity)
            clientRepository.clearDataBase()
            Handler().postDelayed({
                for (element in dataSnapShot.children) {
                    val clientModel = element.getValue(ClientModel::class.java)
                    if (clientModel != null) {
                        with(clientModel){
                            clientRepository.insertClient(ClientsTable(clientName, category))
                        }
                    }
                }
            }, 3000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun onSuccessReadPurchaseDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Delete Purchase Room database*/
        try {
            val purchaseRepository = PurchaseRepository(this@SplashActivity)
            purchaseRepository.clearDataBase()
            Handler().postDelayed({
                for (element in dataSnapShot.children) {
                    val purchaseModel = element.getValue(PurchaseModel::class.java)
                    if (purchaseModel != null) {
                        with(purchaseModel) {
                            purchaseRepository.insertPurchase(PurchaseTable(dateOfPurchase, clientName, billNo, billAmount, timeInMillis))
                        }
                    }
                }
            }, 3000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        saveDetailsToDB()
    }

    override fun onSuccessReadBusinessCategoryDataListener(dataSnapShot: DataSnapshot) {
        /*TODO: Business Name Version [KKC, Socks etc]*/
        try {
            val categoryRepository = CategoryRepository(this@SplashActivity)
            categoryRepository.clearDataBase()

            Handler().postDelayed({
                for (element in dataSnapShot.children) {
                    val entityModel = element.getValue(CategoryModel::class.java)
                    if (entityModel != null) {
                        with(entityModel) {
                            categoryRepository.insertTask(CategoryTable(category_key, category_name))
                        }
                    }
                }
                saveDetailsToDB()
            }, 3000)
        } catch (e: Exception) {
            e.printStackTrace()
            saveDetailsToDB()
        }
    }

    override fun onFailureFireBaseListener() {
        progressBox.dismiss()
        exitDialog("Server Connection Fail!","Unable to connect to server. Please try again later","Exit")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantUtils.SIGNIN_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            progressBox.show()
            outletName.text = AppUtils.OUTLET_NAME
            val sharedPreference = AppSharedPreference(this@SplashActivity)
            if (sharedPreference.getAdminStatus() || sharedPreference.getLoginType() == ConstantUtils.isAdminClientAccess) {
                fireBaseUtils.readValidityFromFireBase()
            } else {
                /*TODO: Launch activity for employees to fill details like - login and logout time, salary date, salary credited etc*/
                progressBox.dismiss()
            }
        }
    }
}
