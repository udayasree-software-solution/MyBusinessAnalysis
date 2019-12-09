package com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.navigation.NavigationView
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments.AddEmployeeFragment
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments.EmployeesListFragment
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils

class EmployeeActivity : AppCompatActivity(), EmployeesListFragment.EmployeesListFragmentInterface {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navAppBar: AppBarLayout
    private lateinit var navToolbar: Toolbar

    private var mFragmentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        initView()
    }

    private fun initView() {
        navToolbar = findViewById(R.id.nav_appbar_toolbar_id)
        navToolbar.setTitleTextColor(Color.WHITE)
        navToolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_orange_dark))
        setSupportActionBar(navToolbar)

        drawerLayout = findViewById(R.id.employee_nav_drawer_id)
        navigationView = findViewById(R.id.employee_nav_view_id)
        navAppBar = findViewById(R.id.nav_appbar_home_id)

        setupNavigationDrawer()
        employeeFragment()
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
                R.id.menu_employee_employees -> {
                    if (mFragmentPosition != 0) {
                        employeeFragment()
                    }
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

    private fun clearBackStack() {
        try {
            val fragments: Int = supportFragmentManager.backStackEntryCount
            for (i in fragments downTo 1) {
                supportFragmentManager.popBackStackImmediate()
            }
        } catch (e: Exception) {
        }
    }

    private fun employeeFragment() {
        mFragmentPosition = 0
        supportActionBar?.title = "Employees List"
        launchFragment(EmployeesListFragment.newInstance())
    }

    override fun employeesListListener() {
        mFragmentPosition = 201
        supportActionBar?.title = "Add Employee"
        launchFragment(AddEmployeeFragment.newInstance())
    }

    private fun onBackPressedAction(): Boolean {
        var backPress = true
        if (mFragmentPosition == 201) {
            employeeFragment()
            backPress = false
        }
        return backPress
    }

    override fun onBackPressed() {
        if (onBackPressedAction()){
            finish()
            super.onBackPressed()
        }
    }
}
