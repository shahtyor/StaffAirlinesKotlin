package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.time.LocalDate

    @SuppressLint("StaticFieldLeak")
    object GlobalStuff {
        lateinit var navController: NavController
        lateinit var navView: BottomNavigationView
        lateinit var Locations: List<Location>
        var Airlines: List<Airline0> = listOf()
        var Permitted: List<PermittedAC> = listOf()
        var FavoriteList = mutableListOf<FlightWithPax>()
        var HistoryList = mutableListOf<HistoryElement>()
        lateinit var activity: Context
        var OriginPoint: SelectedPoint? = null
        var DestinationPoint: SelectedPoint? = null
        var SearchDT: LocalDate? = null
        var Pax: Int = 1
        lateinit var dtSearch: TextView
        var ExtResult: ExtendedResult? = null
        var FlInfo: FlightInfo? = null
        var OneResult: Flight? = null
        var OwnAC: Airline0? = null
        lateinit var StaffRes: Resources
        lateinit var supportFragManager: androidx.fragment.app.FragmentManager
        lateinit var prefs: SharedPreferences
    }
//}
