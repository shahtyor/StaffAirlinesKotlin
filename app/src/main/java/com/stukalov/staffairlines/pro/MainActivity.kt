package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.stukalov.staffairlines.pro.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //var StaffApp = StaffAirlines()

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        //GlobalStuff.navController = navController

        navView = binding.navView

        val cd = ColorDrawable(Color.parseColor("#3b3b3b"))

        getSupportActionBar()?.setBackgroundDrawable(cd)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favourites, R.id.navigation_history, R.id.navigation_settings
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val SM: StaffMethods = StaffMethods()

        lifecycleScope.launch {
            val jsonloc = withContext(Dispatchers.IO) { SM.LoadLocations() }
        }

        lifecycleScope.launch {
            val jsonair = withContext(Dispatchers.IO) { SM.LoadAirlines() }
        }

        GlobalStuff.activity = this.baseContext
        //GlobalStuff.mActivity = this

        GlobalStuff.navController = navController
        GlobalStuff.navView = navView
        GlobalStuff.StaffRes = resources
        GlobalStuff.supportFragManager = supportFragmentManager
        GlobalStuff.prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        //navController.navigate(R.id.main_fragment)

        //SM.SaveVoidFavourites()
        SM.GetOwnAC()
        SM.ReadPermit()
        SM.ReadFavorites()
        SM.ReadHistory()

        //GlobalStuff.navController.navigate(R.id.main_fragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.navigateUp()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}