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
import com.adapty.Adapty
import com.adapty.models.AdaptyConfig
import com.adapty.models.AdaptyProfile
import com.adapty.utils.AdaptyLogLevel
import com.adapty.utils.AdaptyResult
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
import com.stukalov.staffairlines.pro.databinding.ActivityMainBinding
import com.stukalov.staffairlines.pro.ui.home.HomeFragment
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

        Adapty.logLevel = AdaptyLogLevel.VERBOSE

        GlobalStuff.Pax = 1

        Adapty.activate(
            applicationContext,
            AdaptyConfig.Builder("public_live_acbEIggW.S6BV02NUN0hqnxiqZLGS")
                .withObserverMode(true) //default false
                .withCustomerUserId(null) //default null
                .withIpAddressCollectionDisabled(false) //default false
                .withAdIdCollectionDisabled(false) // default false
                .build()
        )

        Adapty.getProfile { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    val profile = result.value
                    GlobalStuff.AdaptyProfileID = profile.profileId
                    val premium = profile.accessLevels["premium"]

                    if (premium?.isActive == true)
                    {
                        GlobalStuff.premiumAccess = true
                        GlobalStuff.subscriptionId = premium.vendorProductId
                    }
                    else
                    {
                        GlobalStuff.premiumAccess = false;
                        GlobalStuff.subscriptionId = null;
                    }
                    if (GlobalStuff.HF != null) {
                        GlobalStuff.HF!!.SetPlan()
                    }
                }
                is AdaptyResult.Error -> {
                    val error = result.error
                    // handle the error
                }
            }
        }

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
        GlobalStuff.mActivity = this

        GlobalStuff.navController = navController
        GlobalStuff.navView = navView
        GlobalStuff.StaffRes = resources
        GlobalStuff.supportFragManager = supportFragmentManager
        GlobalStuff.prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)

        /*val fireinst = FirebaseMessaging.getInstance()
        if (fireinst.token.isSuccessful)
        {
            val strt = fireinst.token.result
        }*/

        SM.GetAppToken()

        if (GlobalStuff.Token.isNullOrEmpty()) {
            lifecycleScope.launch {
                val token = withContext(Dispatchers.IO) {
                    FirebaseMessaging.getInstance().token
                }

                if (token.isSuccessful) {
                    GlobalStuff.Token = token.result
                    SM.SaveAppToken()
                }
            }
        }

        //navController.navigate(R.id.main_fragment)

        //SM.SaveVoidFavourites()
        SM.GetOwnAC()
        SM.ReadPermit()
        SM.ReadFavorites()
        SM.ReadHistory()

        if (GlobalStuff.Token.isNullOrEmpty())
        {
            SM.GetAppToken()
        }

        lifecycleScope.launch {
            val rem = withContext(Dispatchers.IO) { SM.RemainSubscribe(GlobalStuff.Token!!) }

            if (rem != null) {
                GlobalStuff.Remain = rem.count
            }
        }
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