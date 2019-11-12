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
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments.HomeFragment
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.*
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountViewModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.SharedPreferenceUtils

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navAppBar: AppBarLayout
    private lateinit var navToolbar: Toolbar

    private lateinit var fragmentContainer: FrameLayout
    private lateinit var displayOptions: DisplayImageOptions
    private lateinit var roundDisplayOptions: DisplayImageOptions
    private lateinit var imageLoader: ImageLoader

    private lateinit var businessRepository : BusinessRepository
    private lateinit var categoryRepository : CategoryRepository
    private lateinit var clientRepository : ClientRepository
    private lateinit var paymentRepository : PaymentRepository
    private lateinit var purchaseRepository : PurchaseRepository

    private lateinit var amountViewModelList: ArrayList<AmountViewModel>
    private var FRAGMENT_POSITION = 0
    private var isOneTime = true

    private var isPaid = false
    private var isPurchaseAdd = false

    private lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private lateinit var progressBox: ProgressBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initView()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupMPermissions()
        }
    }

    private fun initView() {
        initRoomDBRepository()
        amountViewModelList = ArrayList<AmountViewModel>()
        sharedPreferenceUtils = SharedPreferenceUtils(this)
        AppUtils.isAdminStatus = sharedPreferenceUtils.getAdminStatus()
        AppUtils.OUTLET_NAME = sharedPreferenceUtils.getOutletName() ?: ""

        progressBox = ProgressBox.create(this)

        drawerLayout = findViewById(R.id.home_nav_drawer_id)
        navigationView = findViewById(R.id.home_nav_view_id)
        navAppBar = findViewById(R.id.nav_appbar_home_id)
        fragmentContainer = findViewById(R.id.nav_appbar_container_id)
        navToolbar = findViewById(R.id.nav_appbar_toolbar_id)
        navToolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(navToolbar)
        setupImageLoader()
        setupNavigationDrawer()
        setupNavigationHeader()

        /*TODO: calculate total from Room DB*/
        CalculateTotalTask().execute()
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

        imageLoader.displayImage(sharedPreferenceUtils.getOutletBannerUrl(), headerBanner, displayOptions)
        imageLoader.displayImage(sharedPreferenceUtils.getOutletLogoUrl(), headerProfile, roundDisplayOptions)

        val nameHeader = headerView.findViewById<TextView>(R.id.nav_header_name)
        nameHeader.text = sharedPreferenceUtils.getUserName()
        nameHeader.typeface = AppUtils.getTypeFace(this, ConstantUtils.BLACKJACK)

        val mobileHeader = headerView.findViewById<TextView>(R.id.nav_header_mobile)
        mobileHeader.text = sharedPreferenceUtils.getMobileNumber()
        mobileHeader.typeface = AppUtils.getTypeFace(this, ConstantUtils.BLACKJACK)

        val outletHeader = headerView.findViewById<TextView>(R.id.nav_header_outletname)
        outletHeader.text = sharedPreferenceUtils.getOutletName()
        outletHeader.typeface = AppUtils.getTypeFace(this, ConstantUtils.BLACKJACK)
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
            navMenu.findItem(R.id.menu_outlet_setup_client).isVisible = false
        }

        navigationView.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.menu_drawable_home -> {
                    FRAGMENT_POSITION = 0
                    CalculateTotalTask().execute()
                }

                R.id.menu_drawable_amount -> {
                    FRAGMENT_POSITION = 1
                    isPaid = false
                }

                R.id.menu_drawable_todaybusiness -> {
                    FRAGMENT_POSITION = 2
                }

                R.id.menu_drawable_purchase -> {
                    FRAGMENT_POSITION = 3
                }

                R.id.menu_outlet_setup_client -> {
                    FRAGMENT_POSITION = 4
                }

                R.id.menu_drawable_client -> {
                    FRAGMENT_POSITION = 5
                }

                R.id.menu_drawable_users -> {
                    FRAGMENT_POSITION = 6
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun launchFragment(fragment: Fragment?) {
        clearBackStack()
        when (FRAGMENT_POSITION) {
            0 -> {
                navToolbar.title = "Home"
            }

            1 -> {
                navToolbar.title = "Payable/Paid"
            }

            2 -> {
                navToolbar.title = "Business"
            }

            3 -> {
                navToolbar.title = "Purchase"
            }

            4 -> {
                navToolbar.title = "Outlet"
            }

            5 -> {
                navToolbar.title = "Clients"
            }

            6 -> {
                navToolbar.title = "Users"
            }
        }

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

    @SuppressLint("StaticFieldLeak")
    inner class CalculateTotalTask : AsyncTask<Void, Void, ArrayList<AmountViewModel>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<AmountViewModel> {
            val payableTotal = paymentRepository.queryTotalPayAmount(false)
            val paidTotal = paymentRepository.queryTotalPayAmount(true)
            val purchaseTotal = purchaseRepository.queryPurchaseTotalAmount()
            val businessTotal = businessRepository.queryBusinessAmount()
            val expensesTotal = businessTotal[0]
            val netBusinessTotal = businessTotal[1]
            val grossBusinessTotal = (netBusinessTotal - expensesTotal)

            val businessTotalList = ArrayList<AmountViewModel>()

            businessTotalList.add(AmountViewModel("Payable Amount",payableTotal))
            businessTotalList.add(AmountViewModel("Paid Amount",paidTotal))
            businessTotalList.add(AmountViewModel("Purchase Amount",purchaseTotal))
            businessTotalList.add(AmountViewModel("Expenses Amount",expensesTotal))
            businessTotalList.add(AmountViewModel("Net Business Amount",netBusinessTotal))
            businessTotalList.add(AmountViewModel("Gross Business Amount",grossBusinessTotal))

            return businessTotalList
        }

        override fun onPostExecute(result: ArrayList<AmountViewModel>?) {
            super.onPostExecute(result)
            progressBox.dismiss()
            if (result != null && result.isNotEmpty()){
                launchFragment(HomeFragment.newInstance(result))
            }
        }

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

    override fun onBackPressed() {
        if (FRAGMENT_POSITION > 0) {
            FRAGMENT_POSITION = 0
            clearBackStack()
            CalculateTotalTask().execute()
        } else {
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
