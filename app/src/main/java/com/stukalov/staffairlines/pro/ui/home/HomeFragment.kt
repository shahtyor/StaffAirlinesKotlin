package com.stukalov.staffairlines.pro.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.Global
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.amplitude.core.events.BaseEvent
import com.onesignal.OneSignal
import com.stukalov.staffairlines.pro.GetNonDirectType
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.HistoryElement
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.ResultType
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentHomeBinding
import com.stukalov.staffairlines.pro.ui.paywall.AdaptyController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    lateinit var tbOrigin: TextView
    lateinit var tbDestination: TextView
    lateinit var lbOriginId: TextView
    lateinit var lbDestinationId: TextView
    lateinit var lbOriginCode: TextView
    lateinit var lbDestinationCode: TextView
    lateinit var lbOriginCountry: TextView
    lateinit var lbDestinationCountry: TextView
    lateinit var tbSearchDT: TextView
    lateinit var btSearch: Button
    lateinit var btReplace: ImageButton
    lateinit var btMinusBut: ImageButton
    lateinit var btPlusBut: ImageButton
    lateinit var tbCntPass: TextView
    lateinit var btDate: ImageButton
    lateinit var tvHomeTryPremium: TextView
    lateinit var llHomeTryPremium: LinearLayout
    lateinit var tvPremium: TextView
    lateinit var tvFreePlan: TextView
    lateinit var llPremiumPlan: LinearLayout
    lateinit var spin_layout: FrameLayout
    lateinit var tvHomeInvite: TextView
    lateinit var llHomeInvite: LinearLayout
    lateinit var tvHomeSearchCurrent: TextView
    val SM: StaffMethods = StaffMethods()
    val AdControl: AdaptyController = AdaptyController()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        GlobalStuff.setActionBar(false, true, "")

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tbOrigin = view.findViewById<TextView>(R.id.origin)
        tbDestination = view.findViewById<TextView>(R.id.destination)
        lbOriginId = view.findViewById<TextView>(R.id.lbOriginId)
        lbDestinationId = view.findViewById<TextView>(R.id.lbDestinationId)
        lbOriginCode = view.findViewById<TextView>(R.id.lbOriginCode)
        lbDestinationCode = view.findViewById<TextView>(R.id.lbDestinationCode)
        lbOriginCountry = view.findViewById<TextView>(R.id.lbOriginCountry)
        lbDestinationCountry = view.findViewById<TextView>(R.id.lbDestinationCountry)
        tbSearchDT = view.findViewById<TextView>(R.id.datepicktv)
        btSearch = view.findViewById(R.id.btSearch)
        btReplace = view.findViewById(R.id.btReplace)
        btMinusBut = view.findViewById(R.id.minusbut)
        btPlusBut =  view.findViewById(R.id.plusbut)
        tbCntPass = view.findViewById(R.id.cntpass)
        btDate = view.findViewById(R.id.datebutton)
        tvHomeTryPremium = view.findViewById(R.id.tvHomeTryPremium)
        llHomeTryPremium = view.findViewById(R.id.llHomeTryPremium)
        tvFreePlan = view.findViewById(R.id.tvFreePlan)
        tvPremium = view.findViewById(R.id.tvPremium)
        llPremiumPlan = view.findViewById(R.id.llPremiumPlan)
        spin_layout = view.findViewById<FrameLayout>(R.id.spinner_home)
        tvHomeInvite = view.findViewById(R.id.tvHomeInvite)
        llHomeInvite = view.findViewById(R.id.llHomeInvite)
        tvHomeSearchCurrent = view.findViewById(R.id.tvHomeSearchCurrent)

        tvHomeTryPremium.setText(Html.fromHtml("<u>Try premium</u>"))
        tvPremium.setText(Html.fromHtml("<u>Premium</u>"))
        tvHomeInvite.setText(Html.fromHtml("<u>Invite your colleagues</u>"))

        SetSelPoint()

        if (GlobalStuff.SearchDT == null) {
            GlobalStuff.SearchDT = LocalDate.now()
        }
        var formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        tbSearchDT.text = GlobalStuff.SearchDT?.format(formatter)

        SetPlan()

        btReplace.setOnClickListener {
            ReplacePoint()
        }

        btMinusBut.setOnClickListener {
            minusbut_click(view)
        }

        btPlusBut.setOnClickListener {
            plusbut_click(view)
        }

        tbOrigin.setOnClickListener {
            origin_click(view)
        }

        tbDestination.setOnClickListener {
            destination_click(view)
        }

        btDate.setOnClickListener {
            datebutton_click(view)
        }

        tbSearchDT.setOnClickListener {
            datepicktv_click(view)
        }

        btSearch.setOnClickListener {
            search_click(view)
        }

        llHomeTryPremium.setOnClickListener {
            paywall_try(view)
        }

        llHomeInvite.setOnClickListener {
            invite_click(view)
        }

        llPremiumPlan.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/account/subscriptions?sku=" + GlobalStuff.subscriptionId + "&package=com.stukalov.staffairlines.pro")))
        }

        GlobalStuff.HF = this

        if (GlobalStuff.TestMessage.isNotEmpty())
        {
            val toast0 = Toast.makeText(context, GlobalStuff.TestMessage, Toast.LENGTH_LONG)
            toast0.show()
        }
    }

    /*fun SearchFun(SM: StaffMethods) = coroutineScope{
        val res: Deferred<ExtendedResult?> = async{ SM.ExtendedSearch("PAR", "BER", LocalDate.now(), "", false, GetNonDirectType.Off, 1, "USD", "EN", "USA", "", false, "3.0", "--" )}
        res.await()
    }*/

    fun ReplacePoint()
    {
        if (!(lbOriginId.text.isNullOrEmpty() && lbDestinationId.text.isNullOrEmpty())) {
            if (lbOriginId.text.isNullOrEmpty())
            {
                tbOrigin.text = tbDestination.text
                lbOriginId.text = lbDestinationId.text
                lbOriginCode.text = lbDestinationCode.text
                lbOriginCountry.text = lbDestinationCountry.text
                tbOrigin.setTextColor(getResources().getColor(R.color.black))
                tbDestination.text = "Destination city or airport"
                lbDestinationId.text = ""
                lbDestinationCode.text = ""
                lbDestinationCountry.text = ""
                tbDestination.setTextColor(getResources().getColor(R.color.text_gray))
                GlobalStuff.OriginPoint = GlobalStuff.DestinationPoint
                GlobalStuff.DestinationPoint = null
            }
            else if (lbDestinationId.text.isNullOrEmpty())
            {
                tbDestination.text = tbOrigin.text
                lbDestinationId.text = lbOriginId.text
                lbDestinationCode.text = lbOriginCode.text
                lbDestinationCountry.text = lbOriginCountry.text
                tbDestination.setTextColor(getResources().getColor(R.color.black))
                tbOrigin.text = "Origin city or airport"
                lbOriginId.text = ""
                lbOriginCode.text = ""
                lbOriginCountry.text = ""
                tbOrigin.setTextColor(getResources().getColor(R.color.text_gray))
                GlobalStuff.DestinationPoint = GlobalStuff.OriginPoint
                GlobalStuff.OriginPoint = null
            }
            else {
                val t1 = tbOrigin.text
                val t2 = lbOriginId.text
                val t3 = lbOriginCode.text
                val t4 = lbOriginCountry.text
                val t5 = GlobalStuff.OriginPoint
                tbOrigin.text = tbDestination.text
                lbOriginId.text = lbDestinationId.text
                lbOriginCode.text = lbDestinationCode.text
                lbOriginCountry.text = lbDestinationCountry.text
                tbDestination.text = t1
                lbDestinationId.text = t2
                lbDestinationCode.text = t3
                lbDestinationCountry.text = t4
                GlobalStuff.OriginPoint = GlobalStuff.DestinationPoint
                GlobalStuff.DestinationPoint = t5
            }
        }
    }

    fun SetSelPoint() {
        val Opoint = GlobalStuff.OriginPoint
        val Dpoint = GlobalStuff.DestinationPoint
        if (Opoint != null) {
            if (Opoint.PType == PointType.Origin) {
                var tmptext: String = ""
                if (Opoint.Id > 0) {
                    tmptext = Opoint.Name;
                    if (!Opoint.Code.isNullOrEmpty()) {
                        tmptext += " (" + Opoint.Code + ")"
                    }
                    tbOrigin.text = tmptext

                    tbOrigin.setTextColor(getResources().getColor(R.color.black))
                    lbOriginId.text = Opoint.Id.toString()
                    if (!Opoint.Code.isNullOrEmpty()) lbOriginCode.text =
                        Opoint.Code; else lbOriginCode.text = ""
                    lbOriginCountry.text = Opoint.CountryName;
                } else if (tbOrigin.text == "") {
                    tbOrigin.text = "Origin city or airport";
                    tbOrigin.setTextColor(getResources().getColor(R.color.text_gray))
                    lbOriginId.text = ""
                    lbOriginCode.text = ""
                    lbOriginCountry.text = ""
                    GlobalStuff.OriginPoint = null
                }
            }
        }
        if (Dpoint != null) {
            if (Dpoint.PType == PointType.Destination) {
                var tmptext: String = ""
                if (Dpoint.Id > 0) {
                    tmptext = Dpoint.Name;
                    if (!Dpoint.Code.isNullOrEmpty()) {
                        tmptext += " (" + Dpoint.Code + ")"
                    }
                    tbDestination.text = tmptext

                    tbDestination.setTextColor(getResources().getColor(R.color.black))
                    lbDestinationId.text = Dpoint.Id.toString()
                    if (!Dpoint.Code.isNullOrEmpty()) lbDestinationCode.text =
                        Dpoint.Code; else lbDestinationCode.text = ""
                    lbDestinationCountry.text = Dpoint.CountryName
                } else if (tbDestination.text == "") {
                    tbDestination.text = "Destination city or airport";
                    tbDestination.setTextColor(getResources().getColor(R.color.text_gray))
                    lbDestinationId.text = ""
                    lbDestinationCode.text = ""
                    lbDestinationCountry.text = ""
                    GlobalStuff.DestinationPoint = null
                }
            }

            if (!lbOriginId.text.isNullOrEmpty() && !lbDestinationId.text.isNullOrEmpty()) {
                btSearch.setBackgroundResource(R.drawable.search_button_on)
                btSearch.setTextColor(getResources().getColor(R.color.white))
            } else {
                btSearch.setBackgroundResource(R.drawable.search_button_off)
                btSearch.setTextColor(getResources().getColor(R.color.staff_blue))
            }

            //SelectPage = null;
            //GlobalStuff.SelPoint = null;
        }
        tbCntPass.setText(GlobalStuff.Pax.toString())
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        if (GlobalStuff.SearchDT == null)
        {
            GlobalStuff.SearchDT = LocalDate.now()
        }
        val text = GlobalStuff.SearchDT!!.format(formatter)
        tbSearchDT.setText(text)
    }

    fun SetPlan()
    {
        if (GlobalStuff.premiumAccess)
        {
            tvHomeTryPremium.visibility = View.GONE
            llHomeTryPremium.visibility = View.GONE
            llPremiumPlan.visibility = View.VISIBLE
            tvFreePlan.visibility = View.GONE
            tvHomeSearchCurrent.visibility = View.GONE
        }
        else
        {
            tvHomeTryPremium.visibility = View.VISIBLE
            llHomeTryPremium.visibility = View.VISIBLE
            llPremiumPlan.visibility = View.GONE
            tvFreePlan.visibility = View.VISIBLE
            tvHomeSearchCurrent.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()

        GlobalStuff.setActionBar(false, true, "")

        OneSignal.InAppMessages.addTrigger("os_open_screen", "formSearch")

        if (GlobalStuff.FirstSearchForm)
        {
            var event = BaseEvent()
            event.deviceId = GlobalStuff.DeviceID
            event.eventType = "Search form show"
            event.platform = "Android"
            if (GlobalStuff.customerID != null) {
                event.eventProperties = mutableMapOf<String, Any?>("UserID" to GlobalStuff.customerID)
            }
            event.userProperties = mutableMapOf<String, Any?>("Aircompany" to GlobalStuff.OwnAC, "paidStatus" to if (GlobalStuff.premiumAccess) "premiumAccess" else "free plan")

            GlobalStuff.amplitude?.track(event)

            //string DataJson = "[{,,," + SomeFunctions.GetDeviceInfo() + "," + suser + "}]";


            GlobalStuff.FirstSearchForm = false
        }

        if (GlobalStuff.OwnAC == null)
        {
            if (!GlobalStuff.HomeFromSelect) {
                GlobalStuff.FirstLaunch = true
                GlobalStuff.SaveOneSignalToAdapty()
                GlobalStuff.HomeFromSelect = false
            }

            GlobalStuff.navController.navigate(R.id.carousel_frag, Bundle())
        }
        else
        {
            if (!GlobalStuff.HomeFromSelect) {
                GlobalStuff.FirstLaunch = false
                GlobalStuff.SaveOneSignalToAdapty()
                GlobalStuff.HomeFromSelect = false
            }

            OneSignal.InAppMessages.addTrigger("os_ownAC", GlobalStuff.OwnAC!!.Code)
            if (GlobalStuff.Permitted.isEmpty()) {
                OneSignal.InAppMessages.addTrigger("os_presetAC", "notExists")
            } else {
                OneSignal.InAppMessages.addTrigger("os_presetAC", "exists")
            }
        }

        if (GlobalStuff.FirstSearchForm)
        {
            var event = BaseEvent()
            event.deviceId = GlobalStuff.DeviceID
            event.eventType = "Search form show"
            event.platform = "Android"
            if (GlobalStuff.customerID != null) {
                event.eventProperties = mutableMapOf<String, Any?>("UserID" to GlobalStuff.customerID)
            }
            event.userProperties = mutableMapOf<String, Any?>("Aircompany" to GlobalStuff.OwnAC, "paidStatus" to if (GlobalStuff.premiumAccess) "premiumAccess" else "free plan")

            GlobalStuff.amplitude?.track(event)

            //string DataJson = "[{,,," + SomeFunctions.GetDeviceInfo() + "," + suser + "}]";


            GlobalStuff.FirstSearchForm = false
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun minusbut_click(view: View)
    {
        var cnt = tbCntPass.text.toString().toInt()
        if (cnt > 1)
        {
            cnt--
        }
        tbCntPass.text = cnt.toString()

        if (cnt < 4)
        {
            btPlusBut.setBackgroundResource(R.drawable.plus_button_on)
            btPlusBut.setImageResource(R.drawable.plus_white)
        }
        else
        {
            btPlusBut.setBackgroundResource(R.drawable.plus_button_off)
            btPlusBut.setImageResource(R.drawable.plus_blue)
        }

        if (cnt > 1)
        {
            cnt--
            btMinusBut.setBackgroundResource(R.drawable.minus_button_on)
            btMinusBut.setImageResource(R.drawable.minus_white)
        }
        else
        {
            btMinusBut.setBackgroundResource(R.drawable.minus_button_off)
            btMinusBut.setImageResource(R.drawable.minus_blue)
        }
        GlobalStuff.Pax = cnt
    }

    fun plusbut_click(view: View)
    {
        var cnt = tbCntPass.text.toString().toInt()
        if (cnt < 4)
        {
            cnt++
        }
        tbCntPass.text = cnt.toString()

        if (cnt < 4)
        {
            btPlusBut.setBackgroundResource(R.drawable.plus_button_on)
            btPlusBut.setImageResource(R.drawable.plus_white)
        }
        else
        {
            btPlusBut.setBackgroundResource(R.drawable.plus_button_off)
            btPlusBut.setImageResource(R.drawable.plus_blue)
        }

        if (cnt > 1)
        {
            btMinusBut.setBackgroundResource(R.drawable.minus_button_on)
            btMinusBut.setImageResource(R.drawable.minus_white)
        }
        else
        {
            btMinusBut.setBackgroundResource(R.drawable.minus_button_off)
            btMinusBut.setImageResource(R.drawable.minus_blue)
        }
        GlobalStuff.Pax = cnt
    }

    fun origin_click(view: View) {
        val bundle = Bundle()
        bundle.putString("PointMode", PointType.Origin.name)
        GlobalStuff.navController.navigate(R.id.sel_point, bundle)
    }

    fun destination_click(view: View) {
        val bundle = Bundle()
        bundle.putString("PointMode", PointType.Destination.name)
        GlobalStuff.navController.navigate(R.id.sel_point, bundle)
    }

    fun datebutton_click(view: View) {
        GlobalStuff.dtSearch = tbSearchDT

        val newFragment = DatePickerFragment()
        newFragment.show(GlobalStuff.supportFragManager,"datePicker")
    }

    fun datepicktv_click(view: View) {
        GlobalStuff.dtSearch = tbSearchDT

        val newFragment = DatePickerFragment()
        newFragment.show(GlobalStuff.supportFragManager,"datePicker")
    }

    fun SetDisable(on: Boolean)
    {
        if (on)
        {
            btMinusBut.isEnabled = true
            btPlusBut.isEnabled = true
            tbOrigin.isEnabled = true
            tbDestination.isEnabled = true
            tbSearchDT.isEnabled = true
            btDate.isEnabled = true
            btReplace.isEnabled = true
            btSearch.isEnabled = true
        }
        else
        {
            btMinusBut.isEnabled = false
            btPlusBut.isEnabled = false
            tbOrigin.isEnabled = false
            tbDestination.isEnabled = false
            tbSearchDT.isEnabled = false
            btDate.isEnabled = false
            btReplace.isEnabled = false
            btSearch.isEnabled = false
        }
    }

    fun add_to_history(Origin: String?, Destination: String?, OriginId: String, DestinationId: String, OriginName: String, DestinationName: String, SearchDt: Long, Pax: Int)
    {
        val item: HistoryElement = HistoryElement(Origin, Destination, OriginId, DestinationId, OriginName, DestinationName, SearchDt, Pax)
        val exist = SM.ExistInHistory(item)
        if (!exist)
        {
            GlobalStuff.HistoryList.add(0, item)
            if (GlobalStuff.HistoryList.size > 30)
            {
                GlobalStuff.HistoryList.removeAt(30)
            }
            SM.SaveHistory()
        }
    }

    fun paywall_try(view: View) {

        spin_layout.isVisible = true

        SetDisable(false)

        AdControl.GetPaywallViewParams("test_main_action2")
    }

    fun invite_click(view: View) {
        val shareIntent: Intent = Intent(Intent.ACTION_SEND)
        shareIntent.setType("text/html")
        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("Seems that it useful tool for planning nonrev travel. This is the link for downloading mobile application Staff Airlines from AppStore or Google Play:<br/>https://staffairlines.com/download"))
        startActivity(Intent.createChooser(shareIntent, "I use Staff Airlines app for planning my nonrev travel"))
        GlobalStuff.navController.navigate(R.id.main_frag, Bundle())
    }

    fun search_click(view: View) {

        if (GlobalStuff.OriginPoint != null && GlobalStuff.DestinationPoint != null) {

            if (GlobalStuff.SearchDT!!.isAfter(LocalDate.now()) && !GlobalStuff.premiumAccess)  // показываем пэйвол
            {
                spin_layout.isVisible = true

                SetDisable(false)

                AdControl.GetPaywallViewParams("test_main_action2")
            }
            else {
                spin_layout.isVisible = true

                SetDisable(false)
                val OP = GlobalStuff.OriginPoint
                val DP = GlobalStuff.DestinationPoint
                add_to_history(
                    OP!!.Code,
                    DP!!.Code,
                    OP.Id.toString(),
                    DP.Id.toString(),
                    OP.Name,
                    DP.Name,
                    GlobalStuff.SearchDT!!.toEpochDay(),
                    tbCntPass.text.toString().toInt()
                )

                val permlist = SM.GetStringPermitt()

                // Отладка
                /*var event = BaseEvent()
                event.eventType = "Test 1803 event"
                event.userId = "shahtyor_test"
                event.eventProperties = mutableMapOf<String, Any?>("title" to "Test event", "desc" to "desc event")
                event.userProperties = mutableMapOf<String, Any?>("name" to "shahtyor")
                GlobalStuff.amplitude?.track(event)*/

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
                        GlobalStuff.ResType = ResultType.Direct
                        GlobalStuff.BackResType = null
                        GlobalStuff.navController.navigate(R.id.resultlayout, Bundle())
                    } else {
                        SetDisable(true)
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
    }
}