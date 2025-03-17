package com.stukalov.staffairlines.pro.ui.flight

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.Airline0
import com.stukalov.staffairlines.pro.FlightWithPax
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.RType
import com.stukalov.staffairlines.pro.ResultType
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.ui.paywall.AdaptyController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class FlightFragment : Fragment() {

    lateinit var olbsublost: TextView
    val SM: StaffMethods = StaffMethods()
    val AdControl: AdaptyController = AdaptyController()

    companion object {
        fun newInstance() = FlightFragment()
    }

    private val viewModel: FlightViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_flight, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stf = DateTimeFormatter.ofPattern("HH:mm")
        val sdf = DateTimeFormatter.ofPattern("DD MMM")

        val alogo = view.findViewById<ImageView>(R.id.aclogo_one)
        val aname = view.findViewById<TextView>(R.id.acname_one)
        val onumfl = view.findViewById<TextView>(R.id.numflight_one)
        val oengine = view.findViewById<TextView>(R.id.engine_one)
        val otimedep = view.findViewById<TextView>(R.id.timedep_one)
        val odatedep = view.findViewById<TextView>(R.id.datedep_one)
        val onamedep = view.findViewById<TextView>(R.id.namedep_one)
        val oduration = view.findViewById<TextView>(R.id.duration_one)
        val otimearr = view.findViewById<TextView>(R.id.timearr_one)
        val odatearr = view.findViewById<TextView>(R.id.datearr_one)
        val onamearr = view.findViewById<TextView>(R.id.namearr_one)
        val orat = view.findViewById<TextView>(R.id.rating_one)
        val oclasses = view.findViewById<TextView>(R.id.classes_one)
        val oflyzed = view.findViewById<TextView>(R.id.flyzed_one)
        val osearch = view.findViewById<Button>(R.id.btSearch_one)
        val ofav = view.findViewById<ImageButton>(R.id.fav_one)
        val obtsubscr = view.findViewById<ImageButton>(R.id.btSubscribe_one)
        olbsublost = view.findViewById<TextView>(R.id.lbSubLost)

        if (GlobalStuff.BackResType != null)
        {
            GlobalStuff.ResType = GlobalStuff.BackResType!!
        }

        if (GlobalStuff.ResType == ResultType.First)
        {
            GlobalStuff.BackResType = ResultType.First
        }
        else if (GlobalStuff.ResType == ResultType.Second)
        {
            GlobalStuff.BackResType = ResultType.Second
        }
        else if (GlobalStuff.ResType == ResultType.Final)
        {
            GlobalStuff.BackResType = ResultType.Final
        }

        osearch.setOnClickListener()
        {
            search_click_one(view)
        }

        ofav.setOnClickListener()
        {
            fav_click(view)
        }

        oflyzed.setOnClickListener()
        {
            flyzed_click(view)
        }

        obtsubscr.setOnClickListener()
        {
            btSubscribe_Click(view)
        }

        var f = GlobalStuff.OneResult!!

        val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val title = f.Origin + " - " + f.Destination + ", " + f.DepDateTime.format(formatter0)
        (activity as AppCompatActivity).supportActionBar?.title = title

        val mc = "_" + f.MarketingCarrier.lowercase(Locale.ENGLISH)
        val identifier = GlobalStuff.StaffRes.getIdentifier(mc, "drawable", "com.stukalov.staffairlines.pro")

        var OriginNameExt = f.DepartureName + " (" + f.Origin + ")"
        if (!f.DepartureTerminal.isNullOrEmpty())
        {
            OriginNameExt = OriginNameExt + ", Terminal " + f.DepartureTerminal
        }
        if (!f.DepartureCityName.isNullOrEmpty())
        {
            OriginNameExt = f.DepartureCityName + ", " + OriginNameExt
        }

        var DestinationNameExt = f.ArrivalName + " (" + f.Destination + ")"
        if (!f.ArrivalTerminal.isNullOrEmpty())
        {
            DestinationNameExt = DestinationNameExt + ", Terminal " + f.ArrivalTerminal
        }
        if (!f.ArrivalCityName.isNullOrEmpty())
        {
            DestinationNameExt = f.ArrivalCityName + ", " + DestinationNameExt
        }

        alogo.setImageResource(identifier)
        aname.setText(f.MarketingName)

        onumfl.setText(f.MarketingCarrier + " " + f.FlightNumber)
        oengine.setText(f.EquipmentName)

        olbsublost.setText(GlobalStuff.Remain.toString())

        val sstdep = stf.format(f.DepDateTime)
        val ssddep = sdf.format(f.DepDateTime) + ", " + f.DepDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

        val sstarr = stf.format(f.ArrDateTime)
        val ssdarr = sdf.format(f.ArrDateTime) + ", " + f.ArrDateTime.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)

        var MarkColor: Int = 0
        if (f.RatingType == RType.Good) {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_green)
        }
        else if (f.RatingType == RType.Medium) {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_yellow)
        }
        else {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_red)
        }

        var strclass = "";
        for (OneC in f.NumSeatsForBookingClass) {
            if (OneC[1] == '0')
            {
                strclass = strclass + "<font color=${Color.LTGRAY}>" + OneC + "&nbsp;</font>"
            }
            else
            {
                strclass = strclass + "<font color=${Color.BLACK}>" + OneC + "&nbsp;</font>"
            }
        }

        val strflyzed = "<u>" + GlobalStuff.activity.getString(R.string.label_flyzed) + " " + f.MarketingName + "</u>"

        if (GlobalStuff.ResType == ResultType.Direct)
        {
            osearch.setText("NEW SEARCH")
        }
        else
        {
            osearch.setText("SELECT FLIGHT")
        }

        val actionbut = getArguments()?.getString("ActionButton")

        if (GlobalStuff.ResType == ResultType.Final || actionbut == "no")
        {
            osearch.visibility = View.GONE
        }
        else
        {
            osearch.visibility = View.VISIBLE
        }

        otimedep.setText(sstdep)
        odatedep.setText(ssddep)
        onamedep.setText(OriginNameExt)
        otimearr.setText(sstarr)
        odatearr.setText(ssdarr)
        onamearr.setText(DestinationNameExt)

        oduration.setText(GetTimeAsHM2(f.Duration))

        orat.setTextColor(MarkColor)
        orat.setText(f.RatingType.name + ", " + f.AllPlaces + " seats")
        oclasses.setText(Html.fromHtml(strclass))
        oflyzed.setText(Html.fromHtml(strflyzed))

        if (SM.ExistInFavourites(f)) {
            ofav.setImageResource(R.drawable.favon)
        }
        else {
            ofav.setImageResource(R.drawable.favoff)
        }
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }

    fun search_click_one(view: View) {
        if (GlobalStuff.ResType == ResultType.Direct) {
            GlobalStuff.BackResType = null
            GlobalStuff.navController.navigate(R.id.navigation_home)    //Переход на начало поиска
        }
        else if (GlobalStuff.ResType == ResultType.First)
        {
            GlobalStuff.FirstSegment = GlobalStuff.OneResult!!
            GlobalStuff.ResType = ResultType.Second
            GlobalStuff.BackResType = null
            GlobalStuff.navController.navigate(R.id.resultlayout)     //Переходим к выбору второго сегмента
        }
        else if (GlobalStuff.ResType == ResultType.Second)
        {
            GlobalStuff.SecondSegment = GlobalStuff.OneResult!!
            GlobalStuff.ResType = ResultType.Final
            GlobalStuff.BackResType = null
            GlobalStuff.navController.navigate(R.id.resultlayout)     //Переходим к показу полета с пересадкой
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    fun flyzed_click(view: View) {
        val bundle = Bundle()
        bundle.putString(
            "zed_href", "http://www.flyzed.info/" + GlobalStuff.OneResult!!.MarketingCarrier + "#index"
            )
        GlobalStuff.navController.navigate(R.id.show_zed_frag, bundle)
    }

    fun btSubscribe_Click(view: View) {
        if (!GlobalStuff.premiumAccess)  // показываем пэйвол
        {
            val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_flight)
            spin_layout.isVisible = true

            AdControl.GetPaywallViewParams("test_main_action2")
        }
        else {
            AlertDialog.Builder(view.context)
                .setTitle("")
                .setMessage("Do you want to subscribe to this flight?")
                .setPositiveButton("YES") { dialog, id -> NextStep(dialog, view.context) }
                .setNegativeButton("NO") { dialog, id -> dialog.cancel() }
                .show()
        }
    }

    fun NextStep(dialog: DialogInterface, cont: Context) {
        dialog.cancel()

        if (GlobalStuff.Token.isNullOrEmpty()) {
            AlertDialog.Builder(cont)
                .setTitle("An error occured")
                .setMessage("The token is not defined")
                .setNegativeButton("OK") { dialog2, id -> dialog2.cancel() }
                .show()
        } else {
            val f = GlobalStuff.OneResult!!
            val flight = f.MarketingCarrier + f.FlightNumber

            lifecycleScope.launch {
                val subresult = withContext(Dispatchers.IO) {
                    SM.CreateSubscribe(
                        GlobalStuff.Token!!,
                        flight,
                        f.Origin,
                        f.Destination,
                        GlobalStuff.Pax,
                        f.DepartureDateTime
                    )
                }

                if (subresult.alert.isNullOrEmpty())
                {
                    val st = GetTimeAsHM2(subresult.before_push)

                    AlertDialog.Builder(cont)
                        .setTitle("Success")
                        .setMessage("You've successfully subscribed to this flight. We will send you the first push with load info in " + st)
                        .setNegativeButton("OK") { dialog3, id -> dialog3.cancel() }
                        .show()

                    GlobalStuff.Remain = subresult.remain
                    olbsublost.setText(GlobalStuff.Remain.toString())
                }
                else
                {
                    AlertDialog.Builder(cont)
                        .setTitle("An error occured")
                        .setMessage(subresult.alert)
                        .setNegativeButton("OK") { dialog4, id -> dialog4.cancel() }
                        .show()
                }
            }
        }
    }

    fun fav_click(view: View) {
        val ofav = view.findViewById<ImageButton>(R.id.fav_one)

        lifecycleScope.launch {

            val FWP = FlightWithPax(
                GlobalStuff.OneResult!!,
                GlobalStuff.Pax,
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            )

            if (SM.ExistInFavourites(GlobalStuff.OneResult!!))
            {
                val filt = GlobalStuff.FavoriteList.filter { it.Fl.DepartureDateTime == GlobalStuff.OneResult!!.DepartureDateTime && it.Fl.FlightNumber == GlobalStuff.OneResult!!.FlightNumber && it.Fl.MarketingCarrier == GlobalStuff.OneResult!!.MarketingCarrier }
                GlobalStuff.FavoriteList.remove(filt[0])

                ofav.setImageResource(R.drawable.favoff)
            }
            else {
                GlobalStuff.FavoriteList.add(0, FWP)

                ofav.setImageResource(R.drawable.favon)
            }
            SM.SaveFavorites()
        }
    }
}