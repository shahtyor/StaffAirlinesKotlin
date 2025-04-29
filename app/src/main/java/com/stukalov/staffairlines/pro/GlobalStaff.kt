package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.viewpager2.widget.ViewPager2
import com.adapty.Adapty
import com.adapty.errors.AdaptyError
import com.adapty.models.AdaptyPaywallProduct
import com.adapty.models.AdaptyProfileParameters
import com.adapty.ui.AdaptyUI
import com.amplitude.android.Amplitude
import com.amplitude.core.events.BaseEvent
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.tabs.TabLayout
import com.onesignal.OneSignal
import com.stukalov.staffairlines.pro.databinding.ActivityMainBinding
import com.stukalov.staffairlines.pro.ui.home.HomeFragment
import com.stukalov.staffairlines.pro.ui.setting.CredentialsFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@SuppressLint("StaticFieldLeak")
    object GlobalStuff {
        var density: Float = 1.0F
        lateinit var navController: NavController
        lateinit var Locations: List<Location>
        var actionBar: ActionBar? = null
        var Airlines: List<Airline0> = listOf()
        var Permitted: List<PermittedAC> = listOf()
        var FavoriteList = mutableListOf<FlightWithPax>()
        var HistoryList = mutableListOf<HistoryElement>()
        var NotificationData: Map<String, String> = mapOf()
        var NotiData: NotificationData? = null
        var HF: HomeFragment? = null
        var CF: CredentialsFragment? = null
        var Token: String? = null
        var FirstLaunch: Boolean = true
        var HomeFromSelect: Boolean = false
        var Remain: Int = 5
        var CarouselShowed = false

        lateinit var activity: Context
        lateinit var mActivity: MainActivity
        lateinit var binding: ActivityMainBinding
        var OriginPoint: SelectedPoint? = null
        var DestinationPoint: SelectedPoint? = null
        var ChangePoint: String? = null
        var SearchDT: LocalDate? = null
        var Pax: Int = 1
        lateinit var dtSearch: TextView
        var ExtResult: ExtendedResult? = null
        lateinit var ResType: ResultType
        var BackResType: ResultType? = null
        var FlInfo: FlightInfo? = null
        var OneResult: Flight? = null
        var FirstSegment: Flight? = null
        var SecondSegment: Flight? = null
        var OwnAC: Airline0? = null
        lateinit var StaffRes: Resources
        lateinit var supportFragManager: androidx.fragment.app.FragmentManager
        lateinit var prefs: SharedPreferences

        var AdaptyProducts: List<AdaptyPaywallProduct> = listOf()
        var AdaptyConfig: AdaptyUI.LocalizedViewConfiguration? = null
        var AdaptyErr: AdaptyError? = null
        var AdaptyPurchaseProcess: Boolean = false
        var ExitPurchase: Boolean = false

        var AdaptyProfileID: String? = null
        var AdaptyPaywallID: String? = null
        var AdaptyPaywallRev: Int? = null
        var PointOfShow: String? = null
        var premiumAccess: Boolean = false
        var subscriptionId: String? = null
        var googleInClient: GoogleSignInClient? = null
        var amplitude: Amplitude? = null

        var customerID: String? = null
        var customerEmail: String? = null
        var customerFirstName: String? = null
        var customerLastName: String? = null
        var customerProfile: ProfileTokens? = null
        var adAction: AdaptyAction = AdaptyAction.None

        var FirstSearchForm: Boolean = true
        var DeviceID: String? = null

        fun setActionBar(visibility: Boolean, upEnable: Boolean, title: String)
        {
            val colorwhite = ColorDrawable(Color.parseColor("#FFFFFF"))
            val colorgray =  ColorDrawable(Color.parseColor("#3b3b3b"))
            var actionBar = this.actionBar

            if (actionBar == null)
            {
                actionBar = (activity as AppCompatActivity?)!!.supportActionBar
                this.actionBar = actionBar
            }

            actionBar?.setDisplayHomeAsUpEnabled(upEnable)
            actionBar?.title = title
            if (visibility) {
                if (actionBar?.isShowing != true)
                {
                    actionBar?.show()
                }

                actionBar?.setBackgroundDrawable(colorgray)
                actionBar?.elevation = 2f
            }
            else {
                actionBar?.setBackgroundDrawable(colorwhite)
                actionBar?.elevation = 0f
            }
        }

        fun SaveOneSignalToAdapty()
        {
            if (FirstLaunch) {
                amplitude?.track("First launch")

                CoroutineScope(Dispatchers.IO).launch {
                    val accepted = OneSignal.Notifications.requestPermission(true)

                    if (accepted)
                    {
                        val id = OneSignal.User.pushSubscription.id
                        val builder = AdaptyProfileParameters.Builder().withCustomAttribute("oneSignalSubscriptionId", id)
                        Adapty.updateProfile(builder.build()) { error ->
                            if (error != null) {
                                Log.d("Adapty.updateProfile", error.toString())
                            }
                        }
                    }
                }

            } else {
                val id = OneSignal.User.pushSubscription.id
                val builder = AdaptyProfileParameters.Builder().withCustomAttribute("oneSignalSubscriptionId", id)

                Adapty.updateProfile(builder.build()) { error ->
                    if (error != null) {
                        Log.d("Adapty.updateProfile", error.toString())
                    }
                }
            }
        }

        fun GetTitle(): String
        {
            var result = "..."
            if (OriginPoint != null && DestinationPoint != null) {

                val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val orig = if (OriginPoint?.Code == null) OriginPoint?.Name?.substring(0, 3) else OriginPoint?.Code
                val dest = if (DestinationPoint?.Code == null) DestinationPoint?.Name?.substring(0, 3) else DestinationPoint?.Code
                result = orig + " - " + dest + ", " + SearchDT?.format(formatter0)
            }
            return result
        }

        fun GetBaseEvent(eType: String, userAircompany: Boolean = false, eventUserID: Boolean = false): BaseEvent
        {
            val event = BaseEvent()
            event.eventType = eType
            event.deviceId = DeviceID
            event.platform = "Android"

            if (userAircompany)
            {
                event.userProperties = mutableMapOf<String, Any?>("Aircompany" to OwnAC?.Code)
            }

            if (eventUserID && !customerID.isNullOrEmpty())
            {
                event.eventProperties = mutableMapOf<String, Any?>("UserID" to customerID)
            }

            return event
        }
    }
