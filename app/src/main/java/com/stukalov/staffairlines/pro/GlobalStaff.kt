package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.adapty.errors.AdaptyError
import com.adapty.models.AdaptyPaywallProduct
import com.adapty.ui.AdaptyUI
import com.amplitude.android.Amplitude
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.stukalov.staffairlines.pro.databinding.ActivityMainBinding
import com.stukalov.staffairlines.pro.ui.home.HomeFragment
import com.stukalov.staffairlines.pro.ui.setting.CredentialsFragment
import java.time.LocalDate

    @SuppressLint("StaticFieldLeak")
    object GlobalStuff {
        var density: Float = 1.0F
        lateinit var navController: NavController
        //var navView: BottomNavigationView? = null
        lateinit var Locations: List<Location>
        var actionBar: ActionBar? = null
        var Airlines: List<Airline0> = listOf()
        var Permitted: List<PermittedAC> = listOf()
        var FavoriteList = mutableListOf<FlightWithPax>()
        var HistoryList = mutableListOf<HistoryElement>()
        var NotificationData: Map<String, String> = mapOf()
        var HF: HomeFragment? = null
        var CF: CredentialsFragment? = null
        var Token: String? = null
        var Remain: Int = 5

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

        var AdaptyProfileID: String? = null
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
    }
//}
