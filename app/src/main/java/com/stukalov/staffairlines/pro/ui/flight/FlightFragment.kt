package com.stukalov.staffairlines.pro.ui.flight

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var alogo = view.findViewById<ImageView>(R.id.aclogo_one)
        var aname = view.findViewById<TextView>(R.id.acname_one)

        var f = GlobalStuff.OneResult!!

        val mc = "_" + f.MarketingCarrier.toLowerCase(Locale.ENGLISH)
        val arrdep = f.DepartureDateTime.split("T")
        val deptime = arrdep[1].substring(0, 5)
        val arrarr = f.ArrivalDateTime.split("T")
        val arrtime = arrarr[1].substring(0, 5)
        val arrday  = arrarr[0].split("-")[2].toInt()
        var orig = f.Origin
        if (!f.DepartureTerminal.isNullOrEmpty())
        {
            orig = orig + " (" + f.DepartureTerminal + ")"
        }
        var dest = f.Destination
        if (!f.ArrivalTerminal.isNullOrEmpty())
        {
            dest = dest + " (" + f.ArrivalTerminal + ")"
        }

        val identifier = GlobalStuff.StaffRes.getIdentifier(mc, "drawable", "com.stukalov.staffairlines.pro")
        alogo.setImageResource(identifier)
        aname.setText(f.MarketingName)
        //val durt = GetTimeAsHM2(f.Duration)

        alogo
    }
}