package com.udayasreesoftwaresolution.admin.ui.activities

import android.content.Intent
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.udayasreesoftwaresolution.admin.R
import com.udayasreesoftwaresolution.admin.ui.fragments.AddAdminFragment
import com.udayasreesoftwaresolution.admin.utilspackage.AppUtils

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navigationView : NavigationView
    private lateinit var navAppBar : AppBarLayout
    private lateinit var navToolbar : Toolbar

    private lateinit var fragmentContainer : FrameLayout

    private var SELECTED_FRAGMENT = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        screenSize()
        initView()
    }

    private fun screenSize() {
        val size = Point()
        val w = windowManager

        w.defaultDisplay.getSize(size)
        AppUtils.SCREEN_WIDTH = size.x
        AppUtils.SCREEN_HEIGHT = size.y
    }

    private fun initView() {

        drawerLayout = findViewById(R.id.admin_home_drawer_id)
        navigationView = findViewById(R.id.admin_navigation_view)
        navAppBar = findViewById(R.id.admin_nav_appbar_home_id)
        fragmentContainer = findViewById(R.id.admin_nav_appbar_container_id)
        navToolbar = findViewById(R.id.admin_nav_appbar_toolbar_id)
        setSupportActionBar(navToolbar)

        setupNavigationDrawer()
//        setupNavigationHeader()
        launchFragment()
    }

    private fun setupNavigationDrawer() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, navToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener { menu ->
            when(menu.itemId) {

                R.id.menu_drawable_home_admin -> {
                    SELECTED_FRAGMENT = 0
                    launchFragment()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun launchFragment() {
        var fragment : Fragment? = null
        when(SELECTED_FRAGMENT) {
            0 -> {
                navToolbar.title = "Add Admin"
                fragment = AddAdminFragment.newInstance()
            }

            1 -> {

            }
        }

        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.fragment_slide_left_enter,
                    R.anim.fragment_slide_left_exit,
                    R.anim.fragment_slide_right_enter,
                    R.anim.fragment_slide_right_exit
                )
                .replace(
                    R.id.admin_nav_appbar_container_id,
                    fragment
                )
                .addToBackStack(fragment::class.java.simpleName)
                .commit()
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
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        clearBackStack()
        startActivity(intent)
    }
}
