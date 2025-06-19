package com.stukalov.staffairlines.pro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.adapty.Adapty
import com.adapty.models.AdaptyConfig
import com.adapty.utils.AdaptyLogLevel
import com.adapty.utils.AdaptyResult
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {

    val SM: StaffMethods = StaffMethods()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        GlobalStuff.Pax = 1
        GlobalStuff.prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        GlobalStuff.DeviceID = SM.GetGUID()

        Adapty.logLevel = AdaptyLogLevel.VERBOSE

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        SM.GetCustomerID()
        SM.GetUsePermitted()
        SM.GetFeatureData()

        Adapty.activate(
            applicationContext,
            AdaptyConfig.Builder("public_live_acbEIggW.S6BV02NUN0hqnxiqZLGS")
                .withObserverMode(true) //default false
                .withCustomerUserId(GlobalStuff.customerID) //default null
                .withIpAddressCollectionDisabled(false) //default false
                .withAdIdCollectionDisabled(false) // default false
                .build()
        )

        AdaptyGetProfile()

        ExtrasProcess(intent.extras)

        var ok = ""
        val intent = Intent(this, MainActivity::class.java)

        lifecycleScope.launch {
            val jsonloc = withContext(Dispatchers.IO) { SM.LoadLocations() }

            if (jsonloc.isNotEmpty())
            {
                ok += "1"
                if (ok == "11") {
                    startActivity(intent)
                    finish()
                }
            }
        }

        lifecycleScope.launch {
            val jsonair = withContext(Dispatchers.IO) { SM.LoadAirlines() }

            if (jsonair.isNotEmpty())
            {
                ok += "1"
                if (ok == "11") {
                    startActivity(intent)
                    finish()
                }
            }
        }

        /*Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)*/
    }

    fun AdaptyGetProfile()
    {
        Log.d("AdaptyGetProfile", "Start")

        // отправляем событие «getProfile» в амплитуд
        val event = GlobalStuff.GetBaseEvent("getProfile", false, true)
        GlobalStuff.amplitude?.track(event)

        Adapty.getProfile { result ->
            when (result) {
                is AdaptyResult.Success -> {
                    Log.d("AdaptyGetProfile", "Success")
                    val profile = result.value
                    GlobalStuff.aProfile = profile
                    GlobalStuff.AdaptyProfileID = profile.profileId
                    val premium = profile.accessLevels["premium"]

                    if (premium?.isActive == true)
                    {
                        GlobalStuff.premiumAccess = true
                        GlobalStuff.subscriptionId = premium.vendorProductId
                        OneSignal.User.addTag("active_subscription", "true")
                    }
                    else
                    {
                        GlobalStuff.premiumAccess = false
                        GlobalStuff.subscriptionId = null
                        OneSignal.User.addTag("active_subscription", "false")
                    }
                    GlobalStuff.GetProfileCompleted = true
                }
                is AdaptyResult.Error -> {
                    val error = result.error
                    Log.d("AdaptyGetProfile", error.message!!)
                }
            }
        }
    }

    private fun ExtrasProcess(extras: Bundle?) {
        Log.d("ExtrasProcess", "Start")

        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val data: NotificationData = NotificationData("", "", "", LocalDateTime.now(), 0, "", "")

            Log.d("ExtrasProcess", "Prepare")

            if (extras != null) {
                Log.d("ExtrasProcess", "extras != null")

                extras.keySet().forEach { key ->
                    if (key != null) {
                        if (key == "type") {
                            data.type = intent.extras?.getString(key)!!
                        } else if (key == "origin") {
                            data.origin = intent.extras?.getString(key)!!
                        } else if (key == "destination") {
                            data.destination = intent.extras?.getString(key)!!
                        } else if (key == "departureDateTime") {
                            data.departureDateTime =
                                LocalDateTime.parse(intent.extras?.getString(key)!!, formatter)
                        } else if (key == "paxAmount") {
                            data.paxAmount = intent.extras?.getString(key)!!.toInt()
                        } else if (key == "marketingCarrier") {
                            data.marketingCarrier = intent.extras?.getString(key)!!
                        } else if (key == "flightNumber") {
                            data.flightNumber = intent.extras?.getString(key)!!
                        }
                    }
                }

                Log.d("ExtrasProcess", "End")

                GlobalStuff.NotiData = data
                //OpenNotificationFlight(data)
            }
            else {
                Log.d("ExtrasProcess", "Null")
            }

        } catch (ex: Exception) {
            Log.d("ExtrasProcess", ex.message + "..." + ex.stackTrace)
        }
    }
}