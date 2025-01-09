package com.stukalov.staffairlines.pro.ui.flight

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.RType
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class FlightFragment : Fragment() {

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

        osearch.setOnClickListener()
        {
            search_click_one(view)
        }

        var f = GlobalStuff.OneResult!!

        val mc = "_" + f.MarketingCarrier.toLowerCase(Locale.ENGLISH)
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
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }

    fun search_click_one(view: View) {
        GlobalStuff.navController.navigate(R.id.navigation_home)
    }
}