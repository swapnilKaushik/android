package com.swapnil.foodcall.activity.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.activity.LoginActivity
import com.swapnil.foodcall.activity.fragment.FaqFragment
import com.swapnil.foodcall.activity.fragment.FavoriteFragment
import com.swapnil.foodcall.activity.fragment.OrderHistoryFragment
import com.swapnil.foodcall.activity.fragment.ProfileFragment
import com.swapnil.foodcall.fragment.RestaurantsFragment
import kotlinx.android.synthetic.main.drawer_header.view.*

class MainActivity : AppCompatActivity() {

    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var drawerLayout: DrawerLayout
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var navigationView: NavigationView

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPreferences: SharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        this.coordinatorLayout = findViewById(R.id.coordinatorLayout)
        this.drawerLayout = findViewById(R.id.drawerLayout)
        this.navigationView = findViewById(R.id.navigationView)
        this.toolbar = findViewById(R.id.toolbar)
        this.frameLayout = findViewById(R.id.frameLayout)

        setUpToolbar()

        openRestaurants()

        val headerView = navigationView.getHeaderView(0)
        headerView.txtUserName.text = sharedPreferences.getString("name", "UserName")
        headerView.txtUserMobile.text = "+91-" + sharedPreferences.getString("mobile", "9638527410")
        headerView.txtUserName.setOnClickListener {
            openProfile()
        }


        var actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout, R.string.open_drawer, R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener { it ->

            if( previousMenuItem!=null )
                previousMenuItem?.isChecked = false

            it.isCheckable = true
            it.isChecked = true

            previousMenuItem = it

            when(it.itemId) {
                R.id.menuHome -> {
                    openRestaurants()
                    drawerLayout.closeDrawers()
                }
                R.id.menuProfile -> {
                    openProfile()
                }
                R.id.menuFavourite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavoriteFragment())
                        .commit()

                    supportActionBar?.title = "Favourite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.menuOrder -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrderHistoryFragment())
                        .commit()

                    supportActionBar?.title = "My Previous Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.menuFaq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FaqFragment())
                        .commit()

                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.menuLogout -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Confirmation")
                    dialog.setMessage("Are you sure you want to logOut?")
                    dialog.setPositiveButton("YES") { text, listener ->
                        Toast.makeText(this@MainActivity, "Logged Out.", Toast.LENGTH_SHORT).show()

                        sharedPreferences.edit().clear().apply()
                        supportActionBar?.title = "LogOut"
                        drawerLayout.closeDrawers()

                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                    dialog.setNegativeButton("NO") { text, listener ->
                        drawerLayout.closeDrawers()
                        finish()
                        startActivity(Intent(this@MainActivity, MainActivity::class.java))
                    }
                    dialog.create()
                    dialog.show()
                }
            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if( id == android.R.id.home )
            drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }

    fun openRestaurants() {
        val fragment = RestaurantsFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()

        supportActionBar?.title = "All Restaurants"

        navigationView.setCheckedItem(R.id.menuHome)
    }

    fun openProfile() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, ProfileFragment())
            .commit()

        supportActionBar?.title = "My Profile"
        drawerLayout.closeDrawers()
        navigationView.setCheckedItem(R.id.menuProfile)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)

        when( frag ) {
            !is RestaurantsFragment -> openRestaurants()
            else -> super.onBackPressed()
        }
    }

}
