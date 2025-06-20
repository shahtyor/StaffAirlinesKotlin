package com.stukalov.staffairlines.pro.ui.favourites

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.FavouritesAdapter
import com.stukalov.staffairlines.pro.FlightInfo
import com.stukalov.staffairlines.pro.FlightWithPax
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentFavouritesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset

class FavouritesFragment : Fragment() {

    private var _binding: FragmentFavouritesBinding? = null
    lateinit var favadapter: FavouritesAdapter
    lateinit var fav_lv: ListView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val SM: StaffMethods = StaffMethods()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val dashboardViewModel =
        //    ViewModelProvider(this).get(FavouritesViewModel::class.java)

        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fav_lv = view.findViewById<ListView>(R.id.favlistview)
        val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_favour)

        SM.ReadFavorites()
        favadapter = FavouritesAdapter(view.context, GlobalStuff.FavoriteList)

        fav_lv.setAdapter(favadapter)

        fav_lv.setOnItemClickListener { parent, view, position, id ->

            spin_layout.isVisible = true

            val fl = parent.getItemAtPosition(position) as FlightWithPax

            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    SM.GetFlightInfo(
                        fl.Fl.Origin,
                        fl.Fl.Destination,
                        fl.Fl.DepDateTime,
                        fl.pax,
                        fl.Fl.MarketingCarrier,
                        fl.Fl.FlightNumber,
                        null,
                        GlobalStuff.customerID
                    )
                }

                if (result == "OK" && GlobalStuff.FlInfo != null && GlobalStuff.FlInfo?.Alert.isNullOrEmpty()) {

                    val filt = GlobalStuff.FavoriteList.filter { it.Fl.DepartureDateTime == fl.Fl.DepartureDateTime && it.Fl.FlightNumber == fl.Fl.FlightNumber && it.Fl.MarketingCarrier == fl.Fl.MarketingCarrier }
                    if (filt.size > 0)
                    {
                        var Fav = filt[0]
                        Fav.Dt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                        SM.SaveFavorites()
                    }

                    GlobalStuff.OneResult = GlobalStuff.FlInfo?.Flight
                    GlobalStuff.navController.navigate(R.id.result_one)
                }
                else
                {
                    spin_layout.isVisible = false

                    AlertDialog.Builder(view.context)
                        .setTitle("Alert!")
                        .setMessage(GlobalStuff.FlInfo?.Alert)
                        .setNegativeButton("ok") { dialog, id -> DelFav(dialog, fl, view.context) }
                        .show()
                    /*var serr: String = ""
                    if (GlobalStuff.ExtResult == null)
                    {
                        serr = " , er=null"
                    }
                    val toast = Toast.makeText(context, result + serr, Toast.LENGTH_LONG)
                    toast.show()*/

                }
            }
        }
    }

    fun DelFav(dialog: DialogInterface, fl: FlightWithPax, cont: Context)
    {
        dialog.cancel()

        val filt = GlobalStuff.FavoriteList.filter { it.Fl.DepartureDateTime == fl.Fl.DepartureDateTime && it.Fl.FlightNumber == fl.Fl.FlightNumber && it.Fl.MarketingCarrier == fl.Fl.MarketingCarrier }
        if (filt.size > 0)
        {
            GlobalStuff.FavoriteList.remove(filt[0])
            SM.SaveFavorites()

            favadapter = FavouritesAdapter(cont, GlobalStuff.FavoriteList)
            fav_lv.setAdapter(favadapter)
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalStuff.setActionBar(true, false, "Favourites")

        GlobalStuff.FirstHomeOpen = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}