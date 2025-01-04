package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
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
        lateinit var activity: Context
        var OriginPoint: SelectedPoint? = null
        var DestinationPoint: SelectedPoint? = null
        var SearchDT: LocalDate? = null
        lateinit var dtSearch: TextView
        var ExtResult: ExtendedResult? = null
        var OneResult: Flight? = null
        lateinit var StaffRes: Resources
        lateinit var supportFragManager: androidx.fragment.app.FragmentManager
    }
//}
