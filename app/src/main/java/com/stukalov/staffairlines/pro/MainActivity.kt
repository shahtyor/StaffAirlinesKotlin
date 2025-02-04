package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.PointMode
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.stukalov.staffairlines.pro.databinding.ActivityMainBinding
import com.stukalov.staffairlines.pro.ui.home.DatePickerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.text.SimpleDateFormat
import java.time.Clock
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navView: BottomNavigationView

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var StaffApp = StaffAirlines()

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        GlobalStuff.navController = navController

        navView = binding.navView

        val cd = ColorDrawable(Color.parseColor("#3b3b3b"))

        getSupportActionBar()?.setBackgroundDrawable(cd)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favourites, R.id.navigation_history, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val SM: StaffMethods = StaffMethods()

        lifecycleScope.launch {
            val jsonloc = withContext(Dispatchers.IO) { SM.LoadLocations() }
        }

        lifecycleScope.launch {
            val jsonair = withContext(Dispatchers.IO) { SM.LoadAirlines() }
        }

        GlobalStuff.activity = this.baseContext
        GlobalStuff.navController = navController
        GlobalStuff.navView = navView
        GlobalStuff.StaffRes = resources
        GlobalStuff.supportFragManager = supportFragmentManager
        GlobalStuff.prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        //SM.SaveVoidFavourites()
        SM.GetOwnAC()
        SM.ReadPermit()
        SM.ReadFavorites()
        SM.ReadHistory()
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