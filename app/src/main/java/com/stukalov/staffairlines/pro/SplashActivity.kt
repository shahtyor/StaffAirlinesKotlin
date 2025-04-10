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

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

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

    private fun ExtrasProcess(extras: Bundle?) {
        Log.d("ExtrasProcess", "Start")
        GlobalStuff.TestMessage += "ExtrasProcess-Start"

        try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val data: NotificationData = NotificationData("", "", "", LocalDateTime.now(), 0, "", "")

            Log.d("ExtrasProcess", "Prepare")
            GlobalStuff.TestMessage += "-Prepare"

            if (extras != null) {
                Log.d("ExtrasProcess", "extras != null")
                GlobalStuff.TestMessage += "-Not null"

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
                GlobalStuff.TestMessage += "-End"

                GlobalStuff.NotiData = data
                //OpenNotificationFlight(data)
            }
            else {
                Log.d("ExtrasProcess", "Null")
                GlobalStuff.TestMessage += "-Null"
            }

        } catch (ex: Exception) {
            Log.d("ExtrasProcess", ex.message + "..." + ex.stackTrace)
        }
    }
}