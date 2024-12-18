package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.PointMode
import androidx.constraintlayout.widget.ConstraintLayout
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
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var StaffApp = StaffAirlines()

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        GlobalStuff.navController = navController

        val navView: BottomNavigationView = binding.navView

        val cd = ColorDrawable(Color.parseColor("#3b3b3b"))

        getSupportActionBar()?.setBackgroundDrawable(cd)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val SM: StaffMethods = StaffMethods()
        var jsonloc: String = ""
        lifecycleScope.launch {
            val jsonloc = withContext(Dispatchers.IO) { SM.LoadLocations() }
        }

        GlobalStuff.activity = this.baseContext
        GlobalStuff.navController = navController
        GlobalStuff.StaffRes = resources
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

    fun plusbut_click(view: View) {
        val tv = findViewById<TextView>(R.id.cntpass)
        val minbut = findViewById(R.id.minusbut) as ImageButton
        val plusbut = findViewById(R.id.plusbut) as ImageButton
        var cnt = tv.text.toString().toInt()
        if (cnt < 4)
        {
            cnt++
        }
        tv.text = cnt.toString()

        if (cnt < 4)
        {
            plusbut.setBackgroundResource(R.drawable.plus_button_on)
            plusbut.setImageResource(R.drawable.plus_white)
        }
        else
        {
            plusbut.setBackgroundResource(R.drawable.plus_button_off)
            plusbut.setImageResource(R.drawable.plus_blue)
        }

        if (cnt > 1)
        {
            minbut.setBackgroundResource(R.drawable.minus_button_on)
            minbut.setImageResource(R.drawable.minus_white)
        }
        else
        {
            minbut.setBackgroundResource(R.drawable.minus_button_off)
            minbut.setImageResource(R.drawable.minus_blue)
        }
    }

    fun minusbut_click(view: View) {
        val tv = findViewById<TextView>(R.id.cntpass)
        val minbut = findViewById<ImageButton>(R.id.minusbut)
        val plusbut = findViewById<ImageButton>(R.id.plusbut)
        var cnt = tv.text.toString().toInt()
        if (cnt > 1)
        {
            cnt--
        }
        tv.text = cnt.toString()

        if (cnt < 4)
        {
            plusbut.setBackgroundResource(R.drawable.plus_button_on)
            plusbut.setImageResource(R.drawable.plus_white)
        }
        else
        {
            plusbut.setBackgroundResource(R.drawable.plus_button_off)
            plusbut.setImageResource(R.drawable.plus_blue)
        }

        if (cnt > 1)
        {
            cnt--
            minbut.setBackgroundResource(R.drawable.minus_button_on)
            minbut.setImageResource(R.drawable.minus_white)
        }
        else
        {
            minbut.setBackgroundResource(R.drawable.minus_button_off)
            minbut.setImageResource(R.drawable.minus_blue)
        }
    }

    fun origin_click(view: View) {
        val bundle = Bundle()
        bundle.putString("PointMode", PointType.Origin.name)
        navController.navigate(R.id.sel_point, bundle)
    }

    fun destination_click(view: View) {
        val bundle = Bundle()
        bundle.putString("PointMode", PointType.Destination.name)
        navController.navigate(R.id.sel_point, bundle)
    }

    fun datebutton_click(view: View) {
        var dtSearch = findViewById<TextView>(R.id.datepicktv)
        GlobalStuff.dtSearch = dtSearch

        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager,"datePicker")
    }

    fun datepicktv_click(view: View) {
        var dtSearch = findViewById<TextView>(R.id.datepicktv)
        GlobalStuff.dtSearch = dtSearch

        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager,"datePicker")
    }

    fun search_click(view: View) {
        val SM: StaffMethods = StaffMethods()

        lifecycleScope.launch {
            val jsonloc = withContext(Dispatchers.IO) {
                var result = SM.ExtendedSearch(
                    "NYC",
                    "LAX",
                    LocalDate.now(),
                    "",
                    false,
                    GetNonDirectType.Off,
                    1,
                    "USD",
                    "EN",
                    "USA",
                    "",
                    false,
                    "3.0",
                    "--"
                )
                if (result == "OK")
                {
                    if (GlobalStuff.ExtResult != null)
                    {
                        Handler(Looper.getMainLooper()).postDelayed({
                        val bundle = Bundle()
                        bundle.putString("keyDashBoard", "No")
                        navController.navigate(R.id.resultlayout,bundle)
                        }, 1000)
                    }
                }
            }
        }
    }
}