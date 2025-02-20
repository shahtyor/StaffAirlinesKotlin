package com.stukalov.staffairlines.pro.ui.result

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.stukalov.staffairlines.pro.DirectResultAdapter
import com.stukalov.staffairlines.pro.Flight
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.Location
import com.stukalov.staffairlines.pro.NonDirectResult
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.RType
import com.stukalov.staffairlines.pro.ResultType
import com.stukalov.staffairlines.pro.SelectedPoint
import com.stukalov.staffairlines.pro.databinding.FragmentResultBinding
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.Locale


class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    lateinit var resultadapter: DirectResultAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //val selPointViewModel =
        //    ViewModelProvider(this).get(SelPointViewModel::class.java)

        _binding = FragmentResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //val textView: TextView = binding.selpointtext

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val result_title = GlobalStuff.OriginPoint!!.Code + " - " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(formatter0)
        (activity as AppCompatActivity).supportActionBar?.title = result_title

        val direct_lv: ListView = view.findViewById<ListView>(R.id.directlistview)
        val tabTransfers = view.findViewById<LinearLayout>(R.id.TabTransfers)
        val llResTab = view.findViewById<LinearLayout>(R.id.ResultsTab)
        val tvResInfo = view.findViewById<TextView>(R.id.tvResultInfo)
        val llFirstSegment = view.findViewById<LinearLayout>(R.id.llFirstSegment)
        val llFirstLayout = view.findViewById<LinearLayout>(R.id.llFirstLayout)
        val btFinalNew = view.findViewById<Button>(R.id.btFinalNew)
        val llWaitInfoFinal = view.findViewById<LinearLayout>(R.id.llWaitInfoFinal)
        val tvWaitInfoFinal = view.findViewById<TextView>(R.id.tvWaitInfoFinal)

        if (GlobalStuff.BackResType != null)
        {
            GlobalStuff.ResType = GlobalStuff.BackResType!!
        }

        if (GlobalStuff.ResType == ResultType.Direct) {
            GlobalStuff.BackResType = null
            llResTab.visibility = View.VISIBLE
            tvResInfo.visibility = View.GONE
            llFirstSegment.visibility = View.GONE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            resultadapter = DirectResultAdapter(view.context, GlobalStuff.ExtResult!!.DirectRes)
        }
        else if (GlobalStuff.ResType == ResultType.First) {
            GlobalStuff.BackResType = ResultType.First
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            llFirstSegment.visibility = View.GONE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            val infotxt = "Choose a FIRST FLIGHT " + GlobalStuff.OriginPoint!!.Code + "-" + GlobalStuff.ChangePoint
            tvResInfo.setText(infotxt)

            var ListRes: List<Flight> = listOf()
            var NonRes: NonDirectResult? = null
            val ListNonDir = GlobalStuff.ExtResult!!.NonDirectRes!!.filter{ it -> it.Transfer == GlobalStuff.ChangePoint }
            if (ListNonDir != null && ListNonDir.isNotEmpty())
            {
                NonRes = ListNonDir[0]
            }
            if (NonRes != null)
            {
                ListRes = NonRes.To_airport_transfer
            }
            resultadapter = DirectResultAdapter(view.context, ListRes)
        }
        else if (GlobalStuff.ResType == ResultType.Second)
        {
            GlobalStuff.BackResType = ResultType.First
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            val infotxt = "Choose a SECOND FLIGHT " + GlobalStuff.ChangePoint + "-" + GlobalStuff.DestinationPoint!!.Code
            tvResInfo.setText(infotxt)

            llFirstSegment.visibility = View.VISIBLE
            llFirstLayout.setBackgroundColor(ContextCompat.getColor(GlobalStuff.activity, R.color.lightgray))
            InitFirstSegment(view)

            var ListRes: List<Flight> = listOf()
            var NonRes: NonDirectResult? = null
            val ListNonDir = GlobalStuff.ExtResult!!.NonDirectRes!!.filter{ it -> it.Transfer == GlobalStuff.ChangePoint }
            if (ListNonDir != null && ListNonDir.isNotEmpty())
            {
                NonRes = ListNonDir[0]
            }
            if (NonRes != null)
            {
                val depmints = GlobalStuff.FirstSegment!!.ArrDateTime.plusMinutes(80)
                val depmaxts = GlobalStuff.FirstSegment!!.ArrDateTime.plusHours(24)
                ListRes = NonRes.From_airport_transfer.filter{ it -> it.DepDateTime >= depmints && it.DepDateTime <= depmaxts }
            }
            resultadapter = DirectResultAdapter(view.context, ListRes)
        }
        else if (GlobalStuff.ResType == ResultType.Final)
        {
            GlobalStuff.BackResType = ResultType.Second
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            btFinalNew.visibility = View.VISIBLE
            llWaitInfoFinal.visibility = View.VISIBLE
            tvResInfo.visibility = View.GONE

            var waittext = ""
            if (GlobalStuff.FirstSegment != null && GlobalStuff.SecondSegment != null) {
                var waittime = Duration.between(GlobalStuff.FirstSegment!!.ArrDateTime, GlobalStuff.SecondSegment!!.DepDateTime).toMinutes().toInt()
                waittext = "Waiting time in " + GetNameChangePoint(GlobalStuff.FirstSegment!!) + " " + GetTimeAsHM2(waittime)
            }
            tvWaitInfoFinal.setText(waittext)

            llFirstSegment.visibility = View.VISIBLE
            llFirstLayout.setBackgroundColor(ContextCompat.getColor(GlobalStuff.activity, R.color.white))
            InitFirstSegment(view)

            val ListRes: MutableList<Flight> = mutableListOf()
            ListRes.add(0, GlobalStuff.SecondSegment!!)

            resultadapter = DirectResultAdapter(view.context, ListRes.toList())
        }

        direct_lv.setAdapter(resultadapter)

        direct_lv.setOnItemClickListener{parent, view, position, id ->
            val fl = parent.getItemAtPosition(position) as Flight

            GlobalStuff.OneResult = fl
            GlobalStuff.BackResType = null
            GlobalStuff.navController.navigate(R.id.result_one)
        }

        llFirstLayout.setOnClickListener { view ->
            GlobalStuff.OneResult = GlobalStuff.FirstSegment
            GlobalStuff.BackResType = null
            val bundle = Bundle()
            bundle.putString("ActionButton", "no")
            GlobalStuff.navController.navigate(R.id.result_one, bundle)
        }

        tabTransfers.setOnClickListener { view ->
            GlobalStuff.navController.navigate(R.id.transferlayout, Bundle())
        }

        btFinalNew.setOnClickListener { view ->
            GlobalStuff.ResType = ResultType.Direct
            GlobalStuff.BackResType = null
            GlobalStuff.FirstSegment = null
            GlobalStuff.SecondSegment = null
            GlobalStuff.navController.navigate(R.id.navigation_home)
        }
    }

    fun InitFirstSegment(view: View)
    {
        val tv1resultIndo = view.findViewById<TextView>(R.id.firstResultInfo)
        val tv1date = view.findViewById<TextView>(R.id.first_date_for_result)
        val fl1FrameRat = view.findViewById<FrameLayout>(R.id.firstRatingFrame)
        val iv1aclogo = view.findViewById<ImageView>(R.id.firstaclogo)
        val tv1acname = view.findViewById<TextView>(R.id.firstacname)
        val tv1timedep = view.findViewById<TextView>(R.id.firsttimedep)
        val tv1deppoint = view.findViewById<TextView>(R.id.firstdeppoint)
        val im1planepic = view.findViewById<ImageView>(R.id.firstplanepic)
        val tv1durtext = view.findViewById<TextView>(R.id.firstdurtext)
        val tv1nextday = view.findViewById<TextView>(R.id.firstnextDay)
        val tv1timearr = view.findViewById<TextView>(R.id.firsttimearr)
        val tv1arrpoint = view.findViewById<TextView>(R.id.firstarrpoint)
        val tv1cntrat = view.findViewById<TextView>(R.id.firstcntrating)

        var info1text = ""
        if (GlobalStuff.ResType == ResultType.Second) {
            info1text = "Your FIRST FLIGHT " + GlobalStuff.OriginPoint!!.Code + "-" + GlobalStuff.ChangePoint
        }
        else {
            val sdf22 = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
            info1text = "Your trip from " + GlobalStuff.OriginPoint!!.Code + " to " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(sdf22)
        }
        tv1resultIndo.setText(info1text)

        val f = GlobalStuff.FirstSegment!!
        val mc = "_" + f.MarketingCarrier.lowercase(Locale.ENGLISH)
        val arrdep = f.DepartureDateTime.split("T")
        val deptime = arrdep[1].substring(0, 5)
        val arrarr = f.ArrivalDateTime.split("T")
        val arrtime = arrarr[1].substring(0, 5)
        val arrday = arrarr[0].split("-")[2].toInt()
        var orig = f.Origin
        if (!f.DepartureTerminal.isNullOrEmpty()) {
            orig = orig + " (" + f.DepartureTerminal + ")"
        }
        var dest = f.Destination
        if (!f.ArrivalTerminal.isNullOrEmpty()) {
            dest = dest + " (" + f.ArrivalTerminal + ")"
        }

        val identifier =
            GlobalStuff.StaffRes.getIdentifier(
                mc,
                "drawable",
                "com.stukalov.staffairlines.pro"
            )
        val durt = GetTimeAsHM2(f.Duration)

        var MarkColor: Int = 0
        var MarkBack: Int = 0
        if (f.RatingType == RType.Good) {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_green)
            MarkBack = R.drawable.round_box_green
        } else if (f.RatingType == RType.Medium) {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_yellow)
            MarkBack = R.drawable.round_box_yellow
        } else {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_red)
            MarkBack = R.drawable.round_box_red
        }

        var nextDayVis: Int = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_full_transparent)
        val dom = GlobalStuff.SearchDT?.dayOfMonth
        if (dom != arrday) {
            nextDayVis = ContextCompat.getColor(GlobalStuff.activity, R.color.black)
        }

        var visdate = View.GONE
        if (f.DepDateTime.toLocalDate() == GlobalStuff.SearchDT) {
            visdate = View.GONE
        } else {
            visdate = View.VISIBLE
        }

        val sdf = DateTimeFormatter.ofPattern("dd MMMM, yyyy")

        iv1aclogo!!.setImageResource(identifier)
        tv1acname!!.setText(f.MarketingName)
        tv1timedep!!.setText(deptime)
        tv1deppoint!!.setText(orig)
        im1planepic!!.setImageResource(R.drawable.plane1)
        tv1durtext!!.setText(durt)
        tv1timearr!!.setText(arrtime)
        tv1arrpoint!!.setText(dest)
        tv1cntrat!!.setText(f.AllPlaces)
        tv1cntrat!!.setTextColor(MarkColor)
        tv1cntrat!!.setBackgroundResource(MarkBack)
        fl1FrameRat!!.setBackgroundColor(MarkColor)
        tv1nextday!!.setTextColor(nextDayVis)
        tv1date!!.setText(sdf.format(f.DepDateTime))
        tv1date!!.visibility = visdate
    }

    fun GetNameChangePoint(f: Flight): String
    {
        var res = ""
        if (f.ArrivalCityName.isNullOrEmpty())
        {
            res = f.ArrivalName
        }
        else
        {
            res = f.ArrivalCityName
        }
        res = res + " (" + f.Destination + ")"
        return res
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }

    override fun onStart() {
        super.onStart()

        //var LocList = StaffApp.Locations
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}