package com.stukalov.staffairlines.pro.ui.flight

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.adapty.Adapty
import com.adapty.models.AdaptyProfileParameters
import com.onesignal.OneSignal
import com.stukalov.staffairlines.pro.Flight
import com.stukalov.staffairlines.pro.FlightWithPax
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.ProfileTokens
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.RType
import com.stukalov.staffairlines.pro.ResultType
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.ui.paywall.AdaptyController
import com.survicate.surveys.Survicate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class FlightFragment : Fragment() {

    lateinit var tvOneSubLost: TextView
    lateinit var alogo: ImageView
    lateinit var aname: TextView
    lateinit var onumfl: TextView
    lateinit var oengine: TextView
    lateinit var otimedep: TextView
    lateinit var odatedep: TextView
    lateinit var onamedep: TextView
    lateinit var oduration: TextView
    lateinit var otimearr: TextView
    lateinit var odatearr: TextView
    lateinit var onamearr: TextView
    lateinit var orat: TextView
    lateinit var oclasses: TextView
    lateinit var oflyzed: TextView
    lateinit var btOneAction: Button
    lateinit var ofav: ImageButton
    lateinit var ivOneCoins: ImageView
    lateinit var tvOneCoins: TextView
    lateinit var tvOneNewFeature: TextView
    lateinit var tvOneShareLoads: TextView
    lateinit var llOneButtons: LinearLayout
    lateinit var llOneBellCoins: LinearLayout
    lateinit var btOneRequest: Button
    lateinit var btOneSubscribe: Button
    lateinit var tvOneAgentEconomy: TextView
    lateinit var tvOneAgentBusiness: TextView
    lateinit var tvOneAgentStanby: TextView
    lateinit var llOneAgentInfo: LinearLayout
    lateinit var tvOneAgentInfo: TextView
    lateinit var llSeatsInfo: LinearLayout
    var acceptedValue: Boolean? = null

    val SM: StaffMethods = StaffMethods()
    val AdControl: AdaptyController = AdaptyController()
    val SCREEN_NAME = "flightDetails"

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

        SM.AdaptyGetProfile()

        Survicate.enterScreen(SCREEN_NAME)

        val stf = DateTimeFormatter.ofPattern("HH:mm")
        val sdf = DateTimeFormatter.ofPattern("dd MMM")

        alogo = view.findViewById(R.id.aclogo_one)
        aname = view.findViewById(R.id.acname_one)
        onumfl = view.findViewById(R.id.numflight_one)
        oengine = view.findViewById(R.id.engine_one)
        otimedep = view.findViewById(R.id.timedep_one)
        odatedep = view.findViewById(R.id.datedep_one)
        onamedep = view.findViewById(R.id.namedep_one)
        oduration = view.findViewById(R.id.duration_one)
        otimearr = view.findViewById(R.id.timearr_one)
        odatearr = view.findViewById(R.id.datearr_one)
        onamearr = view.findViewById(R.id.namearr_one)
        orat = view.findViewById(R.id.rating_one)
        oclasses = view.findViewById(R.id.classes_one)
        oflyzed = view.findViewById(R.id.flyzed_one)
        btOneAction = view.findViewById(R.id.btOneAction)
        ofav = view.findViewById(R.id.fav_one)
        ivOneCoins = view.findViewById(R.id.ivOneCoins)
        tvOneCoins = view.findViewById(R.id.tvOneCoins)
        tvOneNewFeature = view.findViewById(R.id.tvOneNewFeature)
        tvOneShareLoads = view.findViewById(R.id.tvOneShareLoads)
        llOneButtons = view.findViewById(R.id.llOneButtons)
        llOneBellCoins = view.findViewById(R.id.llOneBellCoins)
        btOneRequest = view.findViewById(R.id.btOneRequest)
        btOneSubscribe = view.findViewById(R.id.btOneSubscribe)
        tvOneSubLost = view.findViewById(R.id.tvOneSubLost)
        tvOneAgentEconomy = view.findViewById(R.id.tvOneAgentEconomy)
        tvOneAgentBusiness = view.findViewById(R.id.tvOneAgentBusiness)
        tvOneAgentStanby = view.findViewById(R.id.tvOneAgentStandby)
        llOneAgentInfo = view.findViewById(R.id.llOneAgentInfo)
        tvOneAgentInfo = view.findViewById(R.id.tvOneAgentInfo)
        llSeatsInfo = view.findViewById(R.id.llSeatsInfo)

        tvOneShareLoads.setText(Html.fromHtml("Share loads data with other SA commuters and get premium for free. <a href='https://staffairlines.com/flightclub'><u>Become an agent</u></a>"))

        tvOneShareLoads.setMovementMethod(LinkMovementMethod.getInstance())
        tvOneShareLoads.setLinkTextColor(GlobalStuff.StaffRes.getColor(R.color.staff_blue, null))

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

        btOneAction.setOnClickListener()
        {
            GlobalStuff.AskRatingTimer.resume()
            GlobalStuff.AskRatingTimer.stop()
            action_click_one(view)
        }

        ofav.setOnClickListener()
        {
            GlobalStuff.AskRatingTimer.resume()
            fav_click(view)
        }

        oflyzed.setOnClickListener()
        {
            GlobalStuff.AskRatingTimer.resume()
            GlobalStuff.AskRatingTimer.stop()
            flyzed_click(view)
        }

        btOneRequest.setOnClickListener()
        {
            GlobalStuff.AskRatingTimer.resume()
            GlobalStuff.AskRatingTimer.stop()
            Request_Click(view)
        }

        btOneSubscribe.setOnClickListener()
        {
            GlobalStuff.AskRatingTimer.resume()
            GlobalStuff.AskRatingTimer.stop()
            Subscribe_Click(view)
        }

        var f: Flight? = null
        if (GlobalStuff.ResType == ResultType.Second) {
            f = if (GlobalStuff.SecondSegment == null) GlobalStuff.OneResult else GlobalStuff.SecondSegment
        }
        else if (GlobalStuff.ResType == ResultType.First) {
            f = if (GlobalStuff.FirstSegment == null) GlobalStuff.OneResult else GlobalStuff.FirstSegment
        }
        else {
            f = GlobalStuff.OneResult
        }

        if (f == null) return

        //val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        //val title = f.Origin + " - " + f.Destination + ", " + f.DepDateTime.format(formatter0)
        GlobalStuff.setActionBar(true, true, GlobalStuff.GetTitle())
        //(activity as AppCompatActivity).supportActionBar?.title = title

        val mc = "_" + f.MarketingCarrier.lowercase(Locale.ENGLISH)
        val identifier = GlobalStuff.StaffRes.getIdentifier(mc, "drawable", "com.stukalov.staffairlines.pro")

        var OriginNameExt = f.DepartureName + " (" + f.Origin + ")" + if (!f.DepartureTerminal.isNullOrEmpty()) ", Terminal " + f.DepartureTerminal else "" + if (f.OriginDistance != null) ", " + f.OriginDistance.toString() + " km to airport" else ""

        if (!f.DepartureCityName.isNullOrEmpty())
        {
            OriginNameExt = f.DepartureCityName + ", " + OriginNameExt
        }

        var DestinationNameExt = f.ArrivalName + " (" + f.Destination + ")" + if (!f.DepartureTerminal.isNullOrEmpty()) ", Terminal " + f.DepartureTerminal else "" + if (f.DestinationDistance != null) ", " + f.DestinationDistance.toString() + " km to airport" else ""

        if (!f.ArrivalCityName.isNullOrEmpty())
        {
            DestinationNameExt = f.ArrivalCityName + ", " + DestinationNameExt
        }

        if (f.Reporters)
        {
            btOneRequest.setBackgroundResource(R.drawable.search_button_on)
            btOneRequest.setTextColor(GlobalStuff.StaffRes.getColor(R.color.white, null))
            btOneRequest.setText("REQUEST")

            tvOneNewFeature.setBackgroundResource(R.drawable.box_green)
            tvOneNewFeature.setText(Html.fromHtml("<b>New feature!</b> Use the Request button to, clarify seat availability via this airline's agent. This can be helpful on the day of departure.", Html.FROM_HTML_MODE_LEGACY))
        }
        else
        {
            btOneRequest.setBackgroundResource(R.drawable.search_button_off)
            btOneRequest.setTextColor(GlobalStuff.StaffRes.getColor(R.color.staff_blue, null))
            btOneRequest.setText("NO AGENTS YET")

            tvOneNewFeature.setBackgroundColor(GlobalStuff.StaffRes.getColor(R.color.white, null))
            tvOneNewFeature.setText("There are no agents for this airline yet, use the available seats estimate above.")
        }

        alogo.setImageResource(identifier)
        aname.setText(f.MarketingName)

        onumfl.setText(f.MarketingCarrier + " " + f.FlightNumber)
        oengine.setText(f.EquipmentName)

        val actionbut = getArguments()?.getString("ActionButton")

        if (GlobalStuff.ResType == ResultType.Direct || GlobalStuff.ResType == ResultType.Final || actionbut == "no")
        {
            llOneButtons.visibility = View.VISIBLE
            btOneAction.visibility = View.GONE
            llOneBellCoins.visibility = View.VISIBLE
        }
        else
        {
            llOneButtons.visibility = View.GONE
            btOneAction.visibility = View.VISIBLE
            llOneBellCoins.visibility = View.VISIBLE
        }

        SetPlan()

        if (f.AgentInfo != null)
        {
            val ainfo = f.AgentInfo
            tvOneAgentEconomy.setText(if (ainfo.EconomyPlaces == null) "--" else ainfo.EconomyPlaces.toString())
            tvOneAgentBusiness.setText(if (ainfo.BusinessPlaces == null) "--" else ainfo.BusinessPlaces.toString())
            tvOneAgentStanby.setText(if (ainfo.CntSAPassenger == null) "--" else ainfo.CntSAPassenger.toString())
            llOneAgentInfo.visibility = View.VISIBLE

            if (ainfo.TimePassed < 60)
            {
                tvOneAgentInfo.setText("Flight load details were received from agent " + ainfo.Nickname + " " + GetTimeAsHM2(ainfo.TimePassed) + " ago")
                llSeatsInfo.visibility = View.GONE
            }
            else
            {
                tvOneAgentInfo.setText("Loads details from agent " + GetTimeAsHM2(ainfo.TimePassed) + " ago")
                llSeatsInfo.visibility = View.VISIBLE
            }
        }
        else
        {
            llOneAgentInfo.visibility = View.GONE
        }

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

        var strclass = ""
        for (i in 0..f.NumSeatsForBookingClass.lastIndex)
        {
            val OneC = f.NumSeatsForBookingClass[i]
            var nbsp = ""
            if (i < f.NumSeatsForBookingClass.lastIndex)
            {
                nbsp = "&nbsp;"
            }

            if (OneC[1] == '0')
            {
                strclass = strclass + "<font color=${Color.LTGRAY}>" + OneC + nbsp + "</font>"
            }
            else
            {
                strclass = strclass + "<font color=${Color.BLACK}>" + OneC + nbsp + "</font>"
            }
        }

        /*for (OneC in f.NumSeatsForBookingClass) {
            if (OneC[1] == '0')
            {
                strclass = strclass + "<font color=${Color.LTGRAY}>" + OneC + "&nbsp;</font>"
            }
            else
            {
                strclass = strclass + "<font color=${Color.BLACK}>" + OneC + "&nbsp;</font>"
            }
        }*/

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

        if (SM.ExistInFavourites(f)) {
            ofav.setImageResource(R.drawable.favon)
        }
        else {
            ofav.setImageResource(R.drawable.favoff)
        }

        lifecycleScope.launch {
            val accepted = withContext(Dispatchers.IO) {
                OneSignal.Notifications.requestPermission(true)
            }

            if (accepted)
            {
                val id = OneSignal.User.pushSubscription.id
                val builder = AdaptyProfileParameters.Builder().withCustomAttribute("oneSignalSubscriptionId", id)
                Adapty.updateProfile(builder.build()) { error ->
                    if (error != null) {
                        Log.d("Adapty.updateProfile", error.toString())
                    }
                }
            }

            acceptedValue = accepted
        }
    }

    fun SetPlan()
    {
        if (GlobalStuff.Remain == 0 || !GlobalStuff.premiumAccess)
        {
            btOneSubscribe.setBackgroundResource(R.drawable.search_button_off)
            btOneSubscribe.setTextColor(GlobalStuff.StaffRes.getColor(R.color.staff_blue, null))
        }
        else
        {
            btOneSubscribe.setBackgroundResource(R.drawable.search_button_on)
            btOneSubscribe.setTextColor(GlobalStuff.StaffRes.getColor(R.color.white, null))
        }

        if (GlobalStuff.customerID.isNullOrEmpty())
        {
            tvOneCoins.visibility = View.GONE
            ivOneCoins.visibility = View.GONE
            aname.layoutParams.width = (250 * GlobalStuff.density).toInt()
        }
        else
        {
            tvOneCoins.visibility = View.VISIBLE
            ivOneCoins.visibility = View.VISIBLE
            aname.layoutParams.width = (210 * GlobalStuff.density).toInt()

            if (GlobalStuff.premiumAccess && GlobalStuff.customerProfile != null)
            {
                val sum = GlobalStuff.customerProfile!!.SubscribeTokens + GlobalStuff.customerProfile!!.NonSubscribeTokens
                tvOneCoins.setText(sum.toString())
            }
            else
            {
                tvOneCoins.setText("0")
            }
        }

        if (GlobalStuff.premiumAccess) {
            tvOneSubLost.setText(GlobalStuff.Remain.toString())
        } else {
            tvOneSubLost.setText("0")
        }
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }

    fun action_click_one(view: View) {
        val f = GlobalStuff.OneResult!!

        if (GlobalStuff.ResType == ResultType.Direct) {
            GlobalStuff.BackResType = null
            GlobalStuff.navController.navigate(R.id.navigation_home)    //Переход на начало поиска
        }
        else if (GlobalStuff.ResType == ResultType.First)
        {
            GlobalStuff.FirstSegment = GlobalStuff.OneResult!!
            GlobalStuff.ResType = ResultType.Second
            GlobalStuff.BackResType = null

            val event = GlobalStuff.GetBaseEvent("First segment selected", true)
            event.eventProperties = mutableMapOf("Rating" to f.RatingType.name, "Ac" to f.OperatingCarrier,
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
            GlobalStuff.amplitude?.track(event)

            GlobalStuff.navController.navigate(R.id.resultlayout)     //Переходим к выбору второго сегмента
        }
        else if (GlobalStuff.ResType == ResultType.Second)
        {
            GlobalStuff.SecondSegment = GlobalStuff.OneResult!!
            GlobalStuff.ResType = ResultType.Final
            GlobalStuff.BackResType = null

            val event = GlobalStuff.GetBaseEvent("Second segment selected", true)
            event.eventProperties = mutableMapOf("Rating" to f.RatingType.name, "Ac" to f.OperatingCarrier,
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
            GlobalStuff.amplitude?.track(event)

            GlobalStuff.navController.navigate(R.id.resultlayout)     //Переходим к показу полета с пересадкой
        }
    }

    fun SendEventsShowDetails()
    {
        if (GlobalStuff.OneResult != null) {
            val f = GlobalStuff.OneResult!!

            var typeEvent = ""
            if (GlobalStuff.ResType == ResultType.Direct) {
                typeEvent = "Details show direct"
            } else if (GlobalStuff.ResType == ResultType.First) {
                typeEvent = "Details show nondirect first"
            } else if (GlobalStuff.ResType == ResultType.Second) {
                typeEvent = "Details show nondirect second"
            }

            var sforecastrat = ""
            if (f.Forecast == 0.0f)
                sforecastrat = "None"
            if (f.Forecast <= 1) {
                sforecastrat = "Red"
            } else if (f.Forecast <= 2) {
                sforecastrat = "Yellow"
            } else {
                sforecastrat = "Green"
            }

            var dataSAFromAgent = false
            var dataClassesFromAgent = false
            var ageDataFromAgent = 0
            if (f.AgentInfo != null) {
                if (f.AgentInfo.CntSAPassenger != null) dataSAFromAgent = true
                if (f.AgentInfo.EconomyPlaces != null) dataClassesFromAgent = true
                ageDataFromAgent = f.AgentInfo.TimePassed
            }

            val event = GlobalStuff.GetBaseEvent(typeEvent, true)
            event.eventProperties = mutableMapOf<String, Any?>(
                "ac" to f.OperatingCarrier,
                "Rating" to f.RatingType.name,
                "ForecastRating" to sforecastrat,
                "ForecastAccuracy" to f.Accuracy,
                "dataSAFromAgent" to replaceBoolValue(dataSAFromAgent),
                "dataClassesFromAgent" to replaceBoolValue(dataClassesFromAgent),
                "ageDataFromAgent" to ageDataFromAgent,
                "agents" to replaceBoolValue(f.Reporters),
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID
            )
            GlobalStuff.amplitude?.track(event)
        }
    }

    fun replaceBoolValue(value: Boolean): String
    {
        if (value)
        {
            return "yes"
        }
        else
        {
            return "no"
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalStuff.AskRatingTimer.start()

        GlobalStuff.FirstHomeOpen = false
        SendEventsShowDetails()
    }

    override fun onPause() {
        super.onPause()
        GlobalStuff.AskRatingTimer.resume()
        GlobalStuff.AskRatingTimer.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        GlobalStuff.AskRatingTimer.resume()
        GlobalStuff.AskRatingTimer.stop()
        Survicate.leaveScreen(SCREEN_NAME)
    }

    fun flyzed_click(view: View) {
        val bundle = Bundle()
        bundle.putString(
            "zed_href", "http://www.flyzed.info/" + GlobalStuff.OneResult?.MarketingCarrier + "#index"
            )

        val event = GlobalStuff.GetBaseEvent("Сlick ZED button", true)
        event.eventProperties = mutableMapOf("Ac" to GlobalStuff.OneResult?.MarketingCarrier, "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)

        GlobalStuff.navController.navigate(R.id.show_zed_frag, bundle)
    }

    fun Subscribe_Click(view: View) {

        if (!GlobalStuff.premiumAccess)  // показываем пэйвол
        {
            val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_flight)
            spin_layout.isVisible = true
            GlobalStuff.PointOfShow = "Subscribe option"

            AdControl.GetPaywallViewParams("push_a_ver")
        }
        else if (GlobalStuff.Remain == 0)
        {
            AlertDialog.Builder(view.context)
                .setTitle("Subscribe")
                .setMessage("You have reached the maximum number of active flight subscriptionns - 5")
                .setNegativeButton("OK") { dial, id -> dial.cancel() }
                .show()
        }
        else if (acceptedValue!!) {
            AlertDialog.Builder(view.context)
                .setTitle("")
                .setMessage("Do you want to subscribe to this flight?")
                .setPositiveButton("YES") { dialog, id -> NextStep(dialog, view.context) }
                .setNegativeButton("NO") { dialog, id -> dialog.cancel() }
                .show()
        }
    }

    fun TestOneSignalPush()
    {
        var accepted = false
    }

    fun Request_Click(view: View)
    {
        val f = GlobalStuff.OneResult!!

        SM.SendEventClickAgentRequest(f.OperatingCarrier, f.RatingType.name, f.NumSeatsForBookingClass.joinToString(","), f.Rating.toString(), f.DepartureDateTime, if (f.AgentInfo != null) true else false, f.Forecast.toString(), if (f.AgentInfo != null) f.AgentInfo.TimePassed else -1)

        if (f.Reporters == false)
        {
            val event = GlobalStuff.GetBaseEvent("no agent popup", true, false)
            event.eventProperties = mutableMapOf("ac" to f.OperatingCarrier,
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
            GlobalStuff.amplitude?.track(event)

            AlertDialog.Builder(view.context)
                .setTitle("Post request")
                .setMessage("Unfortunately, there are no registered " + f.OperatingName + " staff yet who could help with data on the actual load on flight.\nDetails on staffairlines.com")
                .setNegativeButton("OK") { dial, id -> dial.cancel() }
                .show()
        }
        else
        {
            if (GlobalStuff.customerID.isNullOrEmpty())
            {
                login()
            }
            else
            {
                RequestWork(view)
            }
        }
        return;
    }

    private fun SumTokens(pt: ProfileTokens): Int
    {
        var result: Int = pt.SubscribeTokens + pt.NonSubscribeTokens
        return result;
    }


    fun RequestWork(view: View)
    {
        val f = GlobalStuff.OneResult!!

        var sumTokens = 0
        val custProf = GlobalStuff.customerProfile
        if (custProf != null)
        {
            sumTokens = SumTokens(custProf)
        }

        if (GlobalStuff.premiumAccess && sumTokens >= 1) // есть премиум подписка и хватает токенов
        {
            AlertDialog.Builder(view.context)
                .setTitle("Post Request")
                .setMessage("You can request up-to-date flight " + f.MarketingCarrier + f.FlightNumber + " loads from an airline staff member. The cost of the request is 1 token.")
                .setPositiveButton("Request") { dialog, id -> CreateRequest(view) }
                .setNegativeButton("Cancel") { dialog, id -> dialog.cancel() }
                .show()
        }
        else if (!GlobalStuff.premiumAccess && sumTokens == 0)
        {
            // кидаем на пэйвол
            val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_flight)
            spin_layout.isVisible = true
            GlobalStuff.PointOfShow = "Agent option"

            AdControl.GetPaywallViewParams("agent")
        }
        else if (GlobalStuff.premiumAccess && sumTokens == 0)
        {
            // кидаем на пэйвол
            val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_flight)
            spin_layout.isVisible = true
            GlobalStuff.PointOfShow = "Agent option"

            AdControl.GetPaywallViewParams("flight_detail")
        }
    }

    fun CreateRequest(view: View)
    {
        val f = GlobalStuff.OneResult!!
        // делаем запрос
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                SM.SendReportRequest(
                    GlobalStuff.customerID!!,
                    GlobalStuff.Token!!,
                    f.Origin,
                    f.Destination,
                    f.OperatingCarrier,
                    f.MarketingCarrier + f.FlightNumber,
                    f.DepDateTime,
                    GlobalStuff.Pax
                )
            }

            if (result != null) {
                val tokens = result.Tokens
                val custProf = GlobalStuff.customerProfile!!

                if (tokens != null) {
                    custProf.SubscribeTokens = tokens.SubscribeTokens
                    custProf.NonSubscribeTokens = tokens.NonSubscribeTokens
                }

                GlobalStuff.customerProfile = custProf

                AlertDialog.Builder(view.context)
                    .setTitle("Post request")
                    .setMessage(result.StatusName)
                    .setNegativeButton("OK") { dial, id -> dial.cancel() }
                    .show()
            }
        }
    }

    private fun login() {
        val loginIntent: Intent = GlobalStuff.googleInClient!!.signInIntent
        startActivityForResult(loginIntent, 1)
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
                    tvOneSubLost.setText(GlobalStuff.Remain.toString())
                }
                else
                {
                    AlertDialog.Builder(cont)
                        .setTitle("An error occured")
                        .setMessage(subresult.alert)
                        .setNegativeButton("OK") { dialog4, id -> dialog4.cancel() }
                        .show()
                }

                //нажата Подписка
                val event = GlobalStuff.GetBaseEvent("Subscribe", true)
                event.eventProperties = mutableMapOf<String, Any?>("Result" to if (subresult.alert.isNullOrEmpty()) "Success" else subresult.alert,
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                GlobalStuff.amplitude?.track(event)
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

                val event = GlobalStuff.GetBaseEvent("Remove favorite", true)
                event.eventProperties = mutableMapOf<String, Any?>(
                    "Favourites" to GlobalStuff.FavoriteList.count(),
                    "Screen" to "detail", "Swipe" to "none",
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                GlobalStuff.amplitude?.track(event)

                ofav.setImageResource(R.drawable.favoff)
            }
            else {
                GlobalStuff.FavoriteList.add(0, FWP)

                val event = GlobalStuff.GetBaseEvent("Add favorite", true)
                event.eventProperties = mutableMapOf<String, Any?>(
                    "Favourites" to GlobalStuff.FavoriteList.count(),
                    "Screen" to "detail", "Swipe" to "none",
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                GlobalStuff.amplitude?.track(event)

                ofav.setImageResource(R.drawable.favon)
            }
            SM.SaveFavorites()
        }
    }
}