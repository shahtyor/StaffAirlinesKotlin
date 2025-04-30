package com.stukalov.staffairlines.pro.ui.result

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.GetNonDirectType
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.NonDirectResult
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.ResultType
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.TransferPoint
import com.stukalov.staffairlines.pro.TransferResultAdapter
import com.stukalov.staffairlines.pro.databinding.FragmentTransferBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period


class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null
    lateinit var transferadapter: TransferResultAdapter
    lateinit var vView: View
    val SM: StaffMethods = StaffMethods()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    /*fun GetTitle(): String
    {
        val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val result_title = GlobalStuff.OriginPoint!!.Code + " - " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(formatter0)
        return result_title
    }*/

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vView = view

        //(activity as AppCompatActivity).supportActionBar?.title = result_title
        GlobalStuff.setActionBar(true, true, GlobalStuff.GetTitle())

        val tvinfo: TextView = view.findViewById(R.id.tvInfoTransfer)
        val transfer_lv: ListView = view.findViewById(R.id.transferlistview)
        val tabDirect = view.findViewById<LinearLayout>(R.id.TabDirect)
        val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_transfer)
        val switch2 = view.findViewById<Switch>(R.id.switch2)
        val useraclogo2 = view.findViewById<ImageView>(R.id.useraclogo2)
        val tvForSwitch2 = view.findViewById<TextView>(R.id.tvForSwitch2)
        val tvForSwitch20 = view.findViewById<TextView>(R.id.tvForSwitch20)
        val tvForSwitch22 = view.findViewById<TextView>(R.id.tvForSwitch22)

        var tdet = SM.GetTransferDetails()
        GlobalStuff.FirstSegment = null
        GlobalStuff.SecondSegment = null

        if (GlobalStuff.Permitted.isEmpty()) {
            switch2.visibility = View.GONE
            tvForSwitch20.setText("List of ")
            tvForSwitch2.setText(Html.fromHtml("<u>permitted airlines</u>"))
            tvForSwitch22.setText(" for " + GlobalStuff.OwnAC?.Code + " unavailable")
        }
        else {
            switch2.visibility = View.VISIBLE
            switch2.isChecked = GlobalStuff.UsePermitted
            tvForSwitch20.setText("")
            tvForSwitch2.setText(Html.fromHtml("<u>Permitted airlines</u>"))
            tvForSwitch22.setText(" for " + GlobalStuff.OwnAC?.Code + " staff only")
            switch2.setOnCheckedChangeListener()
            { _, isChecked ->
                GlobalStuff.UsePermitted = isChecked
                SM.SaveUsePermitted()
                SM.sendEventSwitcherPresetFilter(isChecked, 1)
                RunExtendedSearch()
            }
        }
        tvForSwitch2.setOnClickListener()
        {
            SM.sendEventClickPermittedAirlines()
            GlobalStuff.navController.navigate(R.id.show_permitt_frag, Bundle())
        }

        val idenuserac = GlobalStuff.StaffRes.getIdentifier("_" + GlobalStuff.OwnAC?.Code?.lowercase(), "drawable", "com.stukalov.staffairlines.pro")
        useraclogo2.setImageResource(idenuserac)

        if (!GlobalStuff.premiumAccess)
        {
            tvinfo.setText("")
        }

        if (tdet.tp.isNotEmpty()) {
            tvinfo.setText("Choose a city to get a list of transfer flights")
            transferadapter = TransferResultAdapter(view.context, tdet.tp, tdet.ndr)
            transfer_lv.setAdapter(transferadapter)
        }

        transfer_lv.setOnItemClickListener{parent, view, position, id ->
            val trans = parent.getItemAtPosition(position) as TransferPoint

            //Выбрана локация пересадки
            val event = GlobalStuff.GetBaseEvent("Transfer location selected", true)
            event.eventProperties = mutableMapOf("Transfer location selected" to trans.Origin,
                "With results" to if (GlobalStuff.ExtResult?.NonDirectRes.isNullOrEmpty()) "false" else "true",
                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)

            var reqtodo = true
            val ExtRes = GlobalStuff.ExtResult
            if (ExtRes != null)
            {
                val NonDirRes = ExtRes.NonDirectRes
                if (NonDirRes != null && NonDirRes.isNotEmpty())
                {
                    var NonRes: NonDirectResult? = null
                    val NonResList = NonDirRes.filter { it -> it.Transfer == trans.Origin }
                    if (NonResList.isNotEmpty())
                    {
                        NonRes = NonResList[0]
                    }
                    if (NonRes != null)
                    {
                        reqtodo = false
                    }
                }
            }

            if (reqtodo)
            {
                //запрос
                spin_layout.isVisible = true
                tabDirect.isEnabled = false
                transfer_lv.isEnabled = false
                //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
                GlobalStuff.setActionBar(true, false, GlobalStuff.GetTitle())

                val permlist = SM.GetStringPermitt()

                //Пользователь запустил поиск через выбранную локацию пересадки (только если результатов поиска еще нет)
                val event2 = GlobalStuff.GetBaseEvent("Nondirect search started", true)
                event2.eventProperties = mutableMapOf<String, Any?>("Origin" to if (GlobalStuff.OriginPoint?.Code == null) GlobalStuff.OriginPoint?.Id.toString() else GlobalStuff.OriginPoint?.Code,
                    "Destination" to if (GlobalStuff.DestinationPoint?.Code == null) GlobalStuff.DestinationPoint?.Id.toString() else GlobalStuff.DestinationPoint?.Code,
                    "Date" to Period.between(GlobalStuff.SearchDT, LocalDate.now()).days, "Passengers" to GlobalStuff.Pax,
                    "Location for transfer" to trans.Origin,
                    "Country origin" to GlobalStuff.OriginPoint?.CountryName, "Country destination" to GlobalStuff.DestinationPoint?.CountryName,
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                GlobalStuff.amplitude?.track(event2)

                lifecycleScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        SM.GetNonDirectFlights(
                            GlobalStuff.OriginPoint!!.Id.toString(),
                            GlobalStuff.DestinationPoint!!.Id.toString(),
                            trans.Origin,
                            GlobalStuff.SearchDT!!,
                            permlist,
                            GlobalStuff.Pax,
                            "USD",
                            "EN",
                            null,
                            "3.0",
                            GlobalStuff.customerID
                        )
                    }

                    if (result == "OK") {

                        tdet = SM.GetTransferDetails()
                        if (tdet.tp.isNotEmpty()) {

                            //Пользователь получил результаты поиска через выбранную локацию пересадки
                            val textra = SM.GetTransferExtra(trans.Origin)
                            val event3 = GlobalStuff.GetBaseEvent("Results of nondirect search", true)
                            event3.eventProperties = mutableMapOf("Variants_1" to if (textra != null) textra.to.count() else 0,
                                "Variants_2" to if (textra != null) textra.from.count() else 0,
                                "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                            GlobalStuff.amplitude?.track(event3)

                            tabDirect.isEnabled = true
                            transfer_lv.isEnabled = true
                            //(activity as AppCompatActivity?)!!.supportActionBar!!.show()
                            GlobalStuff.setActionBar(true, true, GlobalStuff.GetTitle())
                            spin_layout.isVisible = false
                            tvinfo.setText("Choose a city to get a list of transfer flights")
                            transferadapter = TransferResultAdapter(vView.context, tdet.tp, tdet.ndr)
                            transfer_lv.setAdapter(transferadapter)

                            GlobalStuff.ResType = ResultType.First
                            GlobalStuff.ChangePoint = trans.Origin
                            GlobalStuff.navController.navigate(R.id.resultlayout, Bundle())
                        }
                        spin_layout.isVisible = false
                    }
                    else
                    {
                        spin_layout.isVisible = false
                    }
                }
            }
            else
            {
                //переход на детализацию
                GlobalStuff.ResType = ResultType.First
                GlobalStuff.BackResType = ResultType.First
                GlobalStuff.ChangePoint = trans.Origin
                GlobalStuff.navController.navigate(R.id.resultlayout, Bundle())
            }
        }

        tabDirect.setOnClickListener { view ->
            GlobalStuff.ResType = ResultType.Direct
            GlobalStuff.BackResType = ResultType.Direct
            GlobalStuff.navController.navigate(R.id.resultlayout, Bundle())
        }
    }

    fun RunExtendedSearch()
    {
        val transfer_lv: ListView = vView.findViewById<ListView>(R.id.transferlistview)
        val tvinfo: TextView = vView.findViewById(R.id.tvInfoTransfer)
        val spin_layout = vView.findViewById<FrameLayout>(R.id.spinner_transfer)
        val tabDirect = vView.findViewById<LinearLayout>(R.id.TabDirect)

        spin_layout.isVisible = true
        tabDirect.isEnabled = false
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        GlobalStuff.setActionBar(true, false, GlobalStuff.GetTitle())

        tvinfo.setText("Searching for optimal stopovers...")

        val permlist = SM.GetStringPermitt()

        // Пользователь запустил поиск
        val event = GlobalStuff.GetBaseEvent("Extended search started", true)
        event.eventProperties = mutableMapOf<String, Any?>("Origin" to if (GlobalStuff.OriginPoint?.Code == null) GlobalStuff.OriginPoint?.Id.toString() else GlobalStuff.OriginPoint?.Code,
            "Destination" to if (GlobalStuff.DestinationPoint?.Code == null) GlobalStuff.DestinationPoint?.Id.toString() else GlobalStuff.DestinationPoint?.Code,
            "Date" to Period.between(GlobalStuff.SearchDT, LocalDate.now()).days, "Passengers" to GlobalStuff.Pax,
            "Country origin" to GlobalStuff.OriginPoint?.CountryName, "Country destination" to GlobalStuff.DestinationPoint?.CountryName,
            "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
        GlobalStuff.amplitude?.track(event)

        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                SM.ExtendedSearch(
                    GlobalStuff.OriginPoint!!.Id.toString(),
                    GlobalStuff.DestinationPoint!!.Id.toString(),
                    GlobalStuff.SearchDT!!,
                    permlist,
                    true,
                    GetNonDirectType.auto,
                    GlobalStuff.Pax,
                    "USD",
                    "EN",
                    "USA",
                    "",
                    false,
                    "3.0",
                    "--"
                )
            }

            if (result == "OK") {

                val event0 = GlobalStuff.GetBaseEvent("Results of extended search", true)
                event0.eventProperties = mutableMapOf<String, Any?>("Directs" to if (GlobalStuff.ExtResult?.DirectRes == null) 0 else GlobalStuff.ExtResult?.DirectRes?.count(),
                    "Transfers total" to if (GlobalStuff.ExtResult?.TransferPoints == null) 0 else GlobalStuff.ExtResult?.TransferPoints?.count(),
                    "Transfers with results" to if (GlobalStuff.ExtResult?.ResultTransferPoints == null) 0 else GlobalStuff.ExtResult?.ResultTransferPoints?.count(),
                    "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                GlobalStuff.amplitude?.track(event0)

                val tdet = SM.GetTransferDetails()

                if (tdet.tp.isNotEmpty()) {

                    val event2 = GlobalStuff.GetBaseEvent("Transfer show", true)
                    event2.eventProperties = mutableMapOf("Transfers total" to tdet.tp.count().toString(),
                        "Transfers with results" to tdet.ndr.count().toString(), "UserID" to if (GlobalStuff.customerID == null) "-" else GlobalStuff.customerID)
                    GlobalStuff.amplitude?.track(event2)

                    tabDirect.isEnabled = true
                    //(activity as AppCompatActivity?)!!.supportActionBar!!.show()
                    GlobalStuff.setActionBar(true, true, GlobalStuff.GetTitle())
                    spin_layout.isVisible = false
                    tvinfo.setText("Choose a city to get a list of transfer flights")
                    transferadapter = TransferResultAdapter(vView.context, tdet.tp, tdet.ndr)
                    transfer_lv.setAdapter(transferadapter)
                }
                else
                {
                    tabDirect.isEnabled = true
                    spin_layout.isVisible = false
                    tvinfo.setText("No results")
                }
            }
            else
            {
                spin_layout.isVisible = false
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val tptmp = SM.GetTransferDetails()

        if (tptmp.tp.isEmpty())
        {
            RunExtendedSearch()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}