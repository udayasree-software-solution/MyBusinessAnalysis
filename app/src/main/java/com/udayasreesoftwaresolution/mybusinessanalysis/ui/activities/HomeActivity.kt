package com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments.OutletFragment
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments.AddPurchaseFragment
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.notificationpackage.ConstantNotification
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.*
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments.*
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference

@SuppressLint("StaticFieldLeak")
class HomeActivity : AppCompatActivity(), PaymentFragment.PaymentInterface, AddPaymentFragment.AddPaymentInterface,
    BusinessListFragment.BusinessListInterface, PurchaseFragment.PurchaseInterface, AddBusinessFragmentNew.AddBusinessInterface {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navAppBar: AppBarLayout
    private lateinit var navToolbar: Toolbar

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var displayOptions: DisplayImageOptions
    private lateinit var roundDisplayOptions: DisplayImageOptions
    private lateinit var imageLoader: ImageLoader

    private lateinit var businessRepository: BusinessRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var clientRepository: ClientRepository
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var purchaseRepository: PurchaseRepository

    private lateinit var amountModelList: ArrayList<AmountModel>
    private var mFragmentPosition = 0

    private lateinit var appSharedPreference: AppSharedPreference
    private lateinit var progressBox: ProgressBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        navToolbar = findViewById(R.id.nav_appbar_toolbar_id)
        navToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(navToolbar)
        initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupMPermissions()
        }
    }

    private fun initView() {
        initRoomDBRepository()
        amountModelList = ArrayList()
        appSharedPreference = AppSharedPreference(this)
        AppUtils.isAdminStatus = appSharedPreference.getAdminStatus()
        AppUtils.OUTLET_NAME = appSharedPreference.getOutletName() ?: ""

        progressBox = ProgressBox(this)

        drawerLayout = findViewById(R.id.home_nav_drawer_id)
        navigationView = findViewById(R.id.home_nav_view_id)
        navAppBar = findViewById(R.id.nav_appbar_home_id)
        fragmentContainer = findViewById(R.id.nav_appbar_container_id)
        setupImageLoader()
        setupNavigationDrawer()
        setupNavigationHeader()

        if (!notificationIntent()) {
            HomeAsyncTask().execute()
        }
    }

    private fun notificationIntent() : Boolean {
        var isIntentFound = false
        val bundle = intent.extras
        if (bundle != null) {
            if (bundle.containsKey(ConstantNotification.NOTIFICATION_KEY)) {
                when(bundle.getString(ConstantNotification.NOTIFICATION_KEY)) {
                    ConstantNotification.NOTIFY_PAYMENT -> {
                        isIntentFound = true
                        mFragmentPosition = 1
                        payablePaidFragmentLaunch()
                    }
                    else -> {
                        isIntentFound = true
                    }
                }
            }
        }
        return isIntentFound
    }

    private fun initRoomDBRepository() {
        businessRepository = BusinessRepository(this)
        categoryRepository = CategoryRepository(this)
        clientRepository = ClientRepository(this)
        paymentRepository = PaymentRepository(this)
        purchaseRepository = PurchaseRepository(this)
    }

    private fun setupMPermissions() {
        val permissions: Array<String> = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionNeeded = ArrayList<String>()
        for (p in permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                permissionNeeded.add(p)
            }
        }
        if (permissionNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionNeeded.toArray(arrayOfNulls<String>(permissionNeeded.size)),
                ConstantUtils.PERMISSION_REQUESTED
            )
        }
    }

    private fun setupImageLoader() {
        roundDisplayOptions = DisplayImageOptions.Builder()
            .displayer(RoundedBitmapDisplayer(1000))
            .showImageOnLoading(R.drawable.ic_default)
            .showImageForEmptyUri(R.drawable.ic_default)
            .showImageOnFail(R.drawable.ic_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build()

        displayOptions = DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.ic_default)
            .showImageForEmptyUri(R.drawable.ic_default)
            .showImageOnFail(R.drawable.ic_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build()

        val config = ImageLoaderConfiguration.Builder(this)
            .threadPriority(Thread.NORM_PRIORITY - 2)
            .denyCacheImageMultipleSizesInMemory()
            .defaultDisplayImageOptions(displayOptions)
            .build()

        imageLoader = ImageLoader.getInstance()
        imageLoader.init(config)
    }

    private fun setupNavigationHeader() {
        val headerView = navigationView.getHeaderView(0)

        val headerBanner: ImageView = headerView.findViewById(R.id.nav_header_banner)
        val headerProfile: ImageView = headerView.findViewById(R.id.nav_header_profile)

        if (AppUtils.outlet_banner.isEmpty() || AppUtils.outlet_banner == "NA") {
            AppUtils.outlet_banner = FireBaseConstants.DEFAULT_BANNER
        }
        if (AppUtils.outlet_logo.isEmpty() || AppUtils.outlet_logo =="NA") {
            AppUtils.outlet_logo = FireBaseConstants.DEFAULT_LOGO
        }

        imageLoader.displayImage(AppUtils.outlet_banner, headerBanner, displayOptions)
        imageLoader.displayImage(AppUtils.outlet_logo, headerProfile, roundDisplayOptions)

        val nameHeader = headerView.findViewById<TextView>(R.id.nav_header_name)
        nameHeader.text = appSharedPreference.getUserName()
        nameHeader.typeface = AppUtils.getTypeFace(this, ConstantUtils.MONTSERRAT)

        val mobileHeader = headerView.findViewById<TextView>(R.id.nav_header_mobile)
        mobileHeader.text = appSharedPreference.getMobileNumber()
        mobileHeader.typeface = AppUtils.getTypeFace(this, ConstantUtils.MONTSERRAT)

        val outletHeader = headerView.findViewById<TextView>(R.id.nav_header_outletname)
        outletHeader.text = appSharedPreference.getOutletName()
        outletHeader.typeface = AppUtils.getTypeFace(this, ConstantUtils.MONTSERRAT)
    }

    private fun setupNavigationDrawer() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, navToolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.drawerArrowDrawable.color = ContextCompat.getColor(this, android.R.color.white)
        actionBarDrawerToggle.syncState()

        val navMenu = navigationView.menu
        if (!AppUtils.isAdminStatus) {
            navMenu.findItem(R.id.menu_outlet_analysis).isVisible = false
            navMenu.findItem(R.id.menu_drawable_users).isVisible = false
        }

        navigationView.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.menu_drawable_home -> {
                    if (mFragmentPosition != 0) {
                        mFragmentPosition = 0
                        HomeAsyncTask().execute()
                    }
                }

                R.id.menu_drawable_amount -> {
                    mFragmentPosition = 1
                    payablePaidFragmentLaunch()
                }

                R.id.menu_drawable_todaybusiness -> {
                    mFragmentPosition = 2
                    businessListFragmentLaunch()
                }

                R.id.menu_drawable_purchase -> {
                    mFragmentPosition = 3
                    purchaseListFragmentLaunch()
                }

                R.id.menu_outlet_setup_client -> {
                    mFragmentPosition = 4
                    supportActionBar?.title = "Outlet"
                    launchFragment(OutletFragment.newInstance())
                }

                R.id.menu_drawable_client -> {
                    mFragmentPosition = 5
                    supportActionBar?.title = "Clients"
                    launchFragment(ClientFragment.newInstance())
                }

                R.id.menu_drawable_users -> {
                    mFragmentPosition = 6
                    //supportActionBar?.title = "Employees"
                    startActivity(Intent(this@HomeActivity, EmployeeActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun launchFragment(fragment: Fragment?) {
        clearBackStack()
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit
                )
                .replace(R.id.nav_appbar_container_id, fragment)
                .addToBackStack(fragment::class.java.simpleName)
                .commit()
        }
    }

    inner class HomeAsyncTask : AsyncTask<Void, Void, ArrayList<AmountModel>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            mFragmentPosition = 0
            supportActionBar?.title = "Home"
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<AmountModel> {
            val businessTotalList = ArrayList<AmountModel>()
            val paymentList: ArrayList<Int> = paymentRepository.queryTotalPayAmount()
            if (paymentList.isEmpty()) {
                paymentList.add(0)
                paymentList.add(0)
            }
            val payableTotal = paymentList[0]
            val paidTotal = paymentList[1]
            val purchaseTotal = purchaseRepository.queryPurchaseTotalAmount()
            val businessTotal = businessRepository.queryBusinessAmount()
            if (businessTotal.isEmpty()) {
                businessTotal.add(0)
                businessTotal.add(0)
            }
            val expensesTotal = businessTotal[0]
            val netBusinessTotal = businessTotal[1]
            val grossBusinessTotal = (netBusinessTotal - expensesTotal)

            businessTotalList.add(AmountModel("Payable Amount", payableTotal))
            businessTotalList.add(AmountModel("Paid Amount", paidTotal))
            businessTotalList.add(AmountModel("Purchase Amount", purchaseTotal))
            businessTotalList.add(AmountModel("Expenses Amount", expensesTotal))
            businessTotalList.add(AmountModel("Net Business Amount", netBusinessTotal))
            businessTotalList.add(AmountModel("Gross Business Amount", grossBusinessTotal))
            return businessTotalList
        }

        override fun onPostExecute(result: ArrayList<AmountModel>?) {
            super.onPostExecute(result)
            progressBox.dismiss()
            if (result != null && result.isNotEmpty()) {
                launchFragment(HomeFragment.newInstance(result))
            }
        }
    }

    private fun payablePaidFragmentLaunch() {
        supportActionBar?.title = "Payable/Paid"
        launchFragment(PaymentFragment.newInstance())
    }

    private fun businessListFragmentLaunch() {
        supportActionBar?.title = "Business"
        launchFragment(BusinessListFragment.newInstance())
    }

    private fun purchaseListFragmentLaunch() {
        supportActionBar?.title = "Purchase"
        launchFragment(PurchaseFragment.newInstance())
    }

    override fun onSuccessfulModified() {
        payablePaidFragmentLaunch()
    }

    override fun paymentActionListener(slNo: Int) {
        supportActionBar?.title = "New Payment Details"
        mFragmentPosition = 101
        launchFragment(AddPaymentFragment.newInstance(slNo))
    }

    override fun addBusinessFragmentListener() {
        supportActionBar?.title = "New Business Details"
        mFragmentPosition = 102
        launchFragment(AddBusinessFragmentNew.newInstance())
    }

    override fun addPurchaseListener(clientsName: ArrayList<String>) {
        supportActionBar?.title = "New Purchase Details"
        mFragmentPosition = 103
        launchFragment(AddPurchaseFragment.newInstance(clientsName))
    }

    override fun addBusinessListener(selectedDate : String) {
        supportActionBar?.title = "Business"
        launchFragment(BusinessListFragment.newInstance())
    }

    private fun clearBackStack() {
        try {
            val fragments: Int = supportFragmentManager.backStackEntryCount
            for (i in fragments downTo 1) {
                supportFragmentManager.popBackStackImmediate()
            }
        } catch (e: Exception) {
        }
    }

    private fun onBackPressedAction(): Boolean {
        var backPress = true
        if (mFragmentPosition == 101) {
            mFragmentPosition = 1
            payablePaidFragmentLaunch()
            backPress = false
        } else if (mFragmentPosition == 102) {
            mFragmentPosition = 2
            businessListFragmentLaunch()
            backPress = false
        } else if (mFragmentPosition == 103) {
            mFragmentPosition = 3
            purchaseListFragmentLaunch()
            backPress = false
        } else if (mFragmentPosition > 0) {
            mFragmentPosition = 0
            HomeAsyncTask().execute()
            backPress = false
        }
        return backPress
    }

    override fun onBackPressed() {
        if (onBackPressedAction()) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            clearBackStack()
            startActivity(intent)
            finishAffinity()
            super.onBackPressed()
        }
    }
}
