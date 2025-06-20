package com.stukalov.staffairlines.pro.ui.result

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.onesignal.OneSignal
import com.stukalov.staffairlines.pro.DirectResultAdapter
import com.stukalov.staffairlines.pro.Flight
import com.stukalov.staffairlines.pro.GetNonDirectType
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.NonDirectResult
import com.stukalov.staffairlines.pro.PlaceInfo
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.RType
import com.stukalov.staffairlines.pro.ResultType
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentResultBinding
import com.stukalov.staffairlines.pro.ui.paywall.AdaptyController
import com.survicate.surveys.Survicate
import com.survicate.surveys.traits.UserTrait
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale


class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    lateinit var resultadapter: DirectResultAdapter
    lateinit var btResCommercial: Button
    val SM: StaffMethods = StaffMethods()
    val AdControl: AdaptyController = AdaptyController()
    val SCREEN_NAME = "resultSearch"

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

        return root
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Survicate.enterScreen(SCREEN_NAME)

        val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val result_title = GlobalStuff.GetTitle()
        //GlobalStuff.OriginPoint!!.Code + " - " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(formatter0)
        //(activity as AppCompatActivity).supportActionBar?.title = result_title
        GlobalStuff.setActionBar(true, true, result_title)

        val tv1resultInfo = view.findViewById<TextView>(R.id.firstResultInfo)
        val direct_lv: ListView = view.findViewById<ListView>(R.id.directlistview)
        val tabTransfers = view.findViewById<LinearLayout>(R.id.TabTransfers)
        val llResTab = view.findViewById<LinearLayout>(R.id.ResultsTab)
        val tvResInfo = view.findViewById<TextView>(R.id.tvResultInfo)
        val llFirstSegment = view.findViewById<LinearLayout>(R.id.llFirstSegment)
        val llFirstLayout = view.findViewById<LinearLayout>(R.id.llFirstLayout)
        val btFinalNew = view.findViewById<Button>(R.id.btFinalNew)
        val llWaitInfoFinal = view.findViewById<LinearLayout>(R.id.llWaitInfoFinal)
        val tvWaitInfoFinal = view.findViewById<TextView>(R.id.tvWaitInfoFinal)
        val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_result)
        val llResForListview = view.findViewById<LinearLayout>(R.id.llResForListview)
        val llResTryTransfer = view.findViewById<LinearLayout>(R.id.llResTryTransfer)
        val switch1 = view.findViewById<Switch>(R.id.switch1)
        val useraclogo1 = view.findViewById<ImageView>(R.id.useraclogo1)
        val tvForSwitch1 = view.findViewById<TextView>(R.id.tvForSwitch1)
        val tvForSwitch10 = view.findViewById<TextView>(R.id.tvForSwitch10)
        val tvForSwitch12 = view.findViewById<TextView>(R.id.tvForSwitch12)
        val flPermitted = view.findViewById<FrameLayout>(R.id.flPermitted)

        btResCommercial = view.findViewById(R.id.btResCommercial)

        //tvResCheckTransfer.setText(Html.fromHtml("<u>Check transfer flights</u>"))
        llResTryTransfer.visibility = View.GONE

        if (GlobalStuff.Permitted.isEmpty()) {
            switch1.visibility = View.GONE
            tvForSwitch10.setText("List of ")
            tvForSwitch1.setText(Html.fromHtml("<u>permitted airlines</u>"))
            tvForSwitch12.setText(" for " + GlobalStuff.OwnAC?.Code + " unavailable")
        } else {
            switch1.visibility = View.VISIBLE
            switch1.isChecked = GlobalStuff.UsePermitted
            tvForSwitch10.setText("")
            tvForSwitch1.setText(Html.fromHtml("<u>Permitted airlines</u>"))
            tvForSwitch12.setText(" for " + GlobalStuff.OwnAC?.Code + " staff only")
            switch1.setOnCheckedChangeListener()
            { _, isChecked ->
                GlobalStuff.UsePermitted = isChecked
                GlobalStuff.featureUsedFilterPreset += 1
                Survicate.setUserTrait(UserTrait("featureUsedFilterPreset", GlobalStuff.featureUsedFilterPreset))
                SM.SaveFeatureData()
                SM.SaveUsePermitted()
                SM.sendEventSwitcherPresetFilter(isChecked, 0)

                spin_layout.isVisible = true
                RunExtendedSearch(view.context, spin_layout)
            }
        }
        tvForSwitch1.setOnClickListener()
        {
            SM.sendEventClickPermittedAirlines()
            GlobalStuff.navController.navigate(R.id.show_permitt_frag, Bundle())
        }

        val idenuserac = GlobalStuff.StaffRes.getIdentifier(
            "_" + GlobalStuff.OwnAC?.Code?.lowercase(),
            "drawable",
            "com.stukalov.staffairlines.pro"
        )
        useraclogo1.setImageResource(idenuserac)

        if (GlobalStuff.BackResType != null) {
            GlobalStuff.ResType = GlobalStuff.BackResType!!
        }

        if (GlobalStuff.ResType == ResultType.Direct) {
            GlobalStuff.BackResType = null
            llResTab.visibility = View.VISIBLE
            tvResInfo.visibility = View.GONE
            llFirstSegment.visibility = View.GONE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            tv1resultInfo.visibility = View.GONE
            flPermitted.visibility = View.VISIBLE

            //Открылась страница с результатами прямых перелетов
            val event = GlobalStuff.GetBaseEvent("Direct show", true)
            event.eventProperties = mutableMapOf(
                "Variants" to GlobalStuff.ExtResult?.DirectRes?.count(),
                "Variants_red" to GlobalStuff.ExtResult?.DirectRes?.filter { x -> x.Rating == RType.Bad.value }
                    ?.count(),
                "Variants_yellow" to GlobalStuff.ExtResult?.DirectRes?.filter { x -> x.Rating == RType.Medium.value }
                    ?.count(),
                "Variants_green" to GlobalStuff.ExtResult?.DirectRes?.filter { x -> x.Rating == RType.Good.value }
                    ?.count(),
                "price from ms" to if (GlobalStuff.ExtResult?.DirectInfo != null && GlobalStuff.ExtResult?.DirectInfo?.Amount!! > 0) "yes" else "no",
                "statusPresetFilter" to SM.GetStringStatus(GlobalStuff.UsePermitted),
                "existPreset" to SM.GetStringExistPreset(),
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
            )
            GlobalStuff.amplitude?.track(event)

            if (GlobalStuff.ExtResult?.DirectRes.isNullOrEmpty()) {
                llResTryTransfer.visibility = View.VISIBLE
            } else {
                val flast = GlobalStuff.ExtResult?.DirectRes?.last()
                if (flast?.FlightNumber != "") {
                    var tmplist = GlobalStuff.ExtResult?.DirectRes?.toMutableList()
                    tmplist?.add(
                        GlobalStuff.ExtResult?.DirectRes?.lastIndex?.plus(1)!!, Flight(
                            "", "", "", "", "",
                            "", "", "", "", "", 0, 0, "",
                            "", "", "", "", "", "", "", "",
                            0, arrayOf(), 0, 0, 0, 0, "", 0, 0.0f, 0,
                            "", PlaceInfo(0, 0, 0, "", 0, "", ""), false
                        )
                    )
                    GlobalStuff.ExtResult?.DirectRes = tmplist?.toList()!!
                }
            }

            resultadapter =
                DirectResultAdapter(view.context, GlobalStuff.ExtResult!!.DirectRes, true)
        } else if (GlobalStuff.ResType == ResultType.First) {
            GlobalStuff.BackResType = ResultType.Direct
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            llFirstSegment.visibility = View.GONE
            tv1resultInfo.visibility = View.VISIBLE
            flPermitted.visibility = View.GONE

            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            //val infotxt = "Choose a FIRST FLIGHT " + GlobalStuff.OriginPoint!!.Code + "-" + GlobalStuff.ChangePoint
            //tvResInfo.setText(infotxt)
            val info1text =
                "Choose a FIRST FLIGHT " + GlobalStuff.OriginPoint!!.Code + "-" + GlobalStuff.ChangePoint
            tv1resultInfo.setText(info1text)

            InitFirstSegment(view)

            var ListRes: List<Flight> = listOf()
            var NonRes: NonDirectResult? = null
            val ListNonDir =
                GlobalStuff.ExtResult!!.NonDirectRes.filter { it -> it.Transfer == GlobalStuff.ChangePoint }
            if (ListNonDir != null && ListNonDir.isNotEmpty()) {
                NonRes = ListNonDir[0]
            }
            if (NonRes != null && NonRes.To_airport_transfer != null) {
                ListRes = NonRes.To_airport_transfer
            }
            GlobalStuff.FirstSegment = null

            //Открылась страница с результатами первого сегмента кривого варианта
            val event = GlobalStuff.GetBaseEvent("Non direct show first", true)
            event.eventProperties = mutableMapOf(
                "Variants" to ListRes.count(),
                "Variants_red" to ListRes.filter { x -> x.Rating == RType.Bad.value }.count(),
                "Variants_yellow" to ListRes.filter { x -> x.Rating == RType.Medium.value }.count(),
                "Variants_green" to ListRes.filter { x -> x.Rating == RType.Good.value }.count(),
                "price from ms" to if (NonRes?.ToTransferInfo != null && NonRes?.ToTransferInfo?.Amount!! > 0) "yes" else "no",
                "statusPresetFilter" to SM.GetStringStatus(GlobalStuff.UsePermitted),
                "existPreset" to SM.GetStringExistPreset(),
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
            )
            GlobalStuff.amplitude?.track(event)

            resultadapter = DirectResultAdapter(view.context, ListRes, false)
        } else if (GlobalStuff.ResType == ResultType.Second) {
            GlobalStuff.BackResType = ResultType.First
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            val infotxt =
                "Choose a SECOND FLIGHT " + GlobalStuff.ChangePoint + "-" + GlobalStuff.DestinationPoint!!.Code
            tvResInfo.setText(infotxt)
            tv1resultInfo.visibility = View.VISIBLE
            llFirstSegment.visibility = View.VISIBLE
            flPermitted.visibility = View.GONE

            val info2text =
                "Your FIRST FLIGHT " + GlobalStuff.OriginPoint!!.Code + "-" + GlobalStuff.ChangePoint
            tv1resultInfo.setText(info2text)

            llFirstLayout.setBackgroundColor(
                ContextCompat.getColor(
                    GlobalStuff.activity,
                    R.color.lightgray
                )
            )
            InitFirstSegment(view)

            var ListRes: List<Flight> = listOf()
            var NonRes: NonDirectResult? = null
            val ListNonDir =
                GlobalStuff.ExtResult!!.NonDirectRes!!.filter { it -> it.Transfer == GlobalStuff.ChangePoint }
            if (ListNonDir != null && ListNonDir.isNotEmpty()) {
                NonRes = ListNonDir[0]
            }
            if (NonRes != null) {
                val depmints = GlobalStuff.FirstSegment!!.ArrDateTime.plusMinutes(80)
                val depmaxts = GlobalStuff.FirstSegment!!.ArrDateTime.plusHours(24)
                ListRes =
                    NonRes.From_airport_transfer.filter { it -> it.DepDateTime >= depmints && it.DepDateTime <= depmaxts }
            }
            GlobalStuff.SecondSegment = null

            //Открылась страница с результатами второго сегмента кривого варианта
            val event = GlobalStuff.GetBaseEvent("Non direct show second", true)
            event.eventProperties = mutableMapOf(
                "Variants" to ListRes.count(),
                "Variants_red" to ListRes.filter { x -> x.Rating == RType.Bad.value }.count(),
                "Variants_yellow" to ListRes.filter { x -> x.Rating == RType.Medium.value }.count(),
                "Variants_green" to ListRes.filter { x -> x.Rating == RType.Good.value }.count(),
                "price from ms" to if (NonRes?.FromTransferInfo != null && NonRes?.FromTransferInfo?.Amount!! > 0) "yes" else "no",
                "statusPresetFilter" to SM.GetStringStatus(GlobalStuff.UsePermitted),
                "existPreset" to SM.GetStringExistPreset(),
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
            )
            GlobalStuff.amplitude?.track(event)

            resultadapter = DirectResultAdapter(view.context, ListRes, false)
        } else if (GlobalStuff.ResType == ResultType.Final) {
            GlobalStuff.BackResType = ResultType.Second
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            btFinalNew.visibility = View.VISIBLE
            llWaitInfoFinal.visibility = View.VISIBLE
            tvResInfo.visibility = View.GONE
            tv1resultInfo.visibility = View.VISIBLE
            flPermitted.visibility = View.GONE

            val sdf22 = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
            val info3text =
                "Your trip from " + GlobalStuff.OriginPoint!!.Code + " to " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(
                    sdf22
                )
            tv1resultInfo.setText(info3text)

            var waittext = ""
            if (GlobalStuff.FirstSegment != null && GlobalStuff.SecondSegment != null) {
                var waittime = Duration.between(
                    GlobalStuff.FirstSegment!!.ArrDateTime,
                    GlobalStuff.SecondSegment!!.DepDateTime
                ).toMinutes().toInt()
                waittext =
                    "Waiting time in " + GetNameChangePoint(GlobalStuff.FirstSegment!!) + " " + GetTimeAsHM2(
                        waittime
                    )
            }
            tvWaitInfoFinal.setText(waittext)

            llFirstSegment.visibility = View.VISIBLE
            llFirstLayout.setBackgroundColor(
                ContextCompat.getColor(
                    GlobalStuff.activity,
                    R.color.white
                )
            )
            InitFirstSegment(view)

            val ListRes: MutableList<Flight> = mutableListOf()
            ListRes.add(0, GlobalStuff.SecondSegment!!)

            //форма с выбранным вариантом
            val event = GlobalStuff.GetBaseEvent("Selected variant show", true)
            event.eventProperties = mutableMapOf(
                "Selected ac" to "[" + GlobalStuff.FirstSegment?.MarketingCarrier + "," + GlobalStuff.SecondSegment?.MarketingCarrier + "]",
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
            )
            GlobalStuff.amplitude?.track(event)

            resultadapter = DirectResultAdapter(view.context, ListRes.toList(), false)
        }

        SetCommercialButton()

        direct_lv.setAdapter(resultadapter)

        if (GlobalStuff.ResType == ResultType.Direct && GlobalStuff.ExtResult?.DirectRes.isNullOrEmpty()) {
            direct_lv.visibility = View.GONE
        }

        direct_lv.setOnItemClickListener { parent, view, position, id ->
            val fl = parent.getItemAtPosition(position) as Flight

            if (fl.FlightNumber == "") {

                if (!GlobalStuff.premiumAccess)  // показываем пэйвол
                {
                    spin_layout.isVisible = true
                    GlobalStuff.PointOfShow = "Transfer option"

                    AdControl.GetPaywallViewParams("transfer_a_ver")
                } else {
                    GlobalStuff.navController.navigate(R.id.transferlayout, Bundle())
                }

            } else {
                GlobalStuff.OneResult = fl
                GlobalStuff.BackResType = null
                GlobalStuff.navController.navigate(R.id.result_one)
            }
        }

        llFirstLayout.setOnClickListener { view ->
            GlobalStuff.OneResult = GlobalStuff.FirstSegment
            GlobalStuff.BackResType = null
            val bundle = Bundle()
            bundle.putString("ActionButton", "no")
            GlobalStuff.navController.navigate(R.id.result_one, bundle)
        }

        tabTransfers.setOnClickListener { view ->
            if (!GlobalStuff.premiumAccess)  // показываем пэйвол
            {
                spin_layout.isVisible = true
                GlobalStuff.PointOfShow = "Transfer option"

                AdControl.GetPaywallViewParams("transfer_a_ver")
            } else {
                GlobalStuff.navController.navigate(R.id.transferlayout, Bundle())
            }
        }

        btFinalNew.setOnClickListener { view ->
            GlobalStuff.ResType = ResultType.Direct
            GlobalStuff.BackResType = null
            GlobalStuff.FirstSegment = null
            GlobalStuff.SecondSegment = null

            //нажата кнопка Новый поиск на форме выбранного варианта с пересадкой
            val event = GlobalStuff.GetBaseEvent("New search button", true, true)
            GlobalStuff.amplitude?.track(event)

            GlobalStuff.navController.navigate(R.id.main_frag)
        }
    }

    fun SetCommercialButton() {
        val Exres = GlobalStuff.ExtResult

        btResCommercial.visibility = View.GONE
        if (GlobalStuff.ResType != ResultType.Final) {
            btResCommercial.visibility = View.VISIBLE
            btResCommercial.setText("View commercial fares")
            if (Exres != null) {
                if (GlobalStuff.ResType == ResultType.Direct && Exres.DirectInfo != null && Exres.DirectInfo.Amount > 0) {
                    val strPrice =
                        "Commercial fares from " + Exres.DirectInfo.Amount.toString() + " " + Exres.DirectInfo.Currency
                    btResCommercial.setText(strPrice)
                } else if (!Exres.NonDirectRes.isNullOrEmpty()) {
                    val ndr =
                        Exres.NonDirectRes.filter { it -> it.Transfer == GlobalStuff.ChangePoint }
                            .first()
                    if (GlobalStuff.ResType == ResultType.First) {
                        if (ndr.ToTransferInfo != null && ndr.ToTransferInfo.Amount > 0) {
                            val strPrice2 =
                                "Commercial fares from " + ndr.ToTransferInfo.Amount.toString() + " " + ndr.ToTransferInfo.Currency
                            btResCommercial.setText(strPrice2)
                        }
                    } else if (GlobalStuff.ResType == ResultType.Second) {
                        if (ndr.FromTransferInfo != null && ndr.FromTransferInfo.Amount > 0) {
                            val strPrice3 =
                                "Commercial fares from " + ndr.FromTransferInfo.Amount + " " + ndr.FromTransferInfo.Currency
                            btResCommercial.setText(strPrice3)
                        }
                    }
                }
            }
        }

        btResCommercial.setOnClickListener()
        {
            //var suser = PremiumFunctions.SetUserID();
            val openURL = Intent(Intent.ACTION_VIEW)

            if (Exres != null) {
                var dlink: String = ""
                if (GlobalStuff.ResType == ResultType.Direct && Exres.DirectInfo != null && Exres.DirectInfo.Link != null) {
                    val event = GlobalStuff.GetBaseEvent("click ms button")
                    event.eventProperties = mutableMapOf(
                        "price from ms" to if (GlobalStuff.ExtResult?.DirectInfo != null && GlobalStuff.ExtResult?.DirectInfo?.Amount!! > 0) "yes" else "no",
                        "flow" to "direct",
                        "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
                    )
                    GlobalStuff.amplitude?.track(event)

                    dlink = Exres.DirectInfo.Link
                } else if (Exres.NonDirectRes != null) {
                    val ndr =
                        Exres.NonDirectRes.filter { it -> it.Transfer == GlobalStuff.ChangePoint }
                            .first()
                    if (GlobalStuff.ResType == ResultType.First && ndr != null) {
                        if (ndr.ToTransferInfo != null && ndr.ToTransferInfo.Link != null) {
                            val event = GlobalStuff.GetBaseEvent("click ms button")
                            event.eventProperties = mutableMapOf(
                                "price from ms" to if (ndr.ToTransferInfo != null && ndr.ToTransferInfo.Amount > 0) "yes" else "no",
                                "flow" to "transfer1",
                                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
                            )
                            GlobalStuff.amplitude?.track(event)

                            dlink = ndr.ToTransferInfo.Link
                        }
                    } else if (GlobalStuff.ResType == ResultType.Second && ndr != null) {
                        if (ndr.FromTransferInfo != null && ndr.FromTransferInfo.Amount > 0) {
                            val event = GlobalStuff.GetBaseEvent("click ms button")
                            event.eventProperties = mutableMapOf(
                                "price from ms" to if (ndr.FromTransferInfo != null && ndr.FromTransferInfo.Amount > 0) "yes" else "no",
                                "flow" to "transfer2",
                                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
                            )
                            GlobalStuff.amplitude?.track(event)

                            dlink = ndr.FromTransferInfo.Link
                        }
                    }
                }

                openURL.data = Uri.parse(dlink)
                startActivity(openURL)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //(activity as AppCompatActivity?)!!.supportActionBar!!.show()
        if (GlobalStuff.ResType == ResultType.Direct) {
            OneSignal.InAppMessages.addTrigger("os_open_screen", "resultSearch")
        }
        GlobalStuff.FirstHomeOpen = false
    }

    fun InitFirstSegment(view: View) {
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

        if (GlobalStuff.ResType != ResultType.First) {
            //llFirstSegmentDet.visibility = View.VISIBLE
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

            var nextDayVis: Int =
                ContextCompat.getColor(GlobalStuff.activity, R.color.sa_full_transparent)
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
    }

    fun GetNameChangePoint(f: Flight): String {
        var res = ""
        if (f.ArrivalCityName.isNullOrEmpty()) {
            res = f.ArrivalName
        } else {
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
        Survicate.leaveScreen(SCREEN_NAME)
        _binding = null
    }

    fun RunExtendedSearch(context: Context, spin_layout: FrameLayout) {
        val permlist = SM.GetStringPermitt()

        // Пользователь запустил поиск
        val event = GlobalStuff.GetBaseEvent("Extended search started", true)
        event.eventProperties = mutableMapOf<String, Any?>(
            "Origin" to if (GlobalStuff.OriginPoint?.Code == null) GlobalStuff.OriginPoint?.Id.toString() else GlobalStuff.OriginPoint?.Code,
            "Destination" to if (GlobalStuff.DestinationPoint?.Code == null) GlobalStuff.DestinationPoint?.Id.toString() else GlobalStuff.DestinationPoint?.Code,
            "Date" to Period.between(GlobalStuff.SearchDT, LocalDate.now()).days,
            "Passengers" to GlobalStuff.Pax,
            "Country origin" to GlobalStuff.OriginPoint?.CountryName,
            "Country destination" to GlobalStuff.DestinationPoint?.CountryName,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
        )
        GlobalStuff.amplitude?.track(event)

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                SM.ExtendedSearch(
                    GlobalStuff.OriginPoint!!.Id.toString(),
                    GlobalStuff.DestinationPoint!!.Id.toString(),
                    GlobalStuff.SearchDT!!,
                    permlist,
                    false,
                    GetNonDirectType.off,
                    GlobalStuff.Pax,
                    "USD",
                    "EN",
                    "USA",
                    "",
                    false,
                    "3.0",
                    "--",
                    GlobalStuff.customerID
                )
            }

            if (result == "OK" && GlobalStuff.ExtResult != null) {

                val event = GlobalStuff.GetBaseEvent("Results of extended search", true)
                event.eventProperties = mutableMapOf<String, Any?>(
                    "Directs" to if (GlobalStuff.ExtResult?.DirectRes == null) 0 else GlobalStuff.ExtResult?.DirectRes?.count(),
                    "Transfers total" to if (GlobalStuff.ExtResult?.TransferPoints == null) 0 else GlobalStuff.ExtResult?.TransferPoints?.count(),
                    "Transfers with results" to if (GlobalStuff.ExtResult?.ResultTransferPoints == null) 0 else GlobalStuff.ExtResult?.ResultTransferPoints?.count(),
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
                )
                GlobalStuff.amplitude?.track(event)

                GlobalStuff.ResType = ResultType.Direct
                GlobalStuff.BackResType = null
                GlobalStuff.navController.navigate(R.id.resultlayout, Bundle())
            } else {
                //SetDisable(true)
                spin_layout.isVisible = false

                var serr: String = ""
                if (GlobalStuff.ExtResult == null) {
                    serr = " , er=null"
                }
                val toast = Toast.makeText(context, result + serr, Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }
}