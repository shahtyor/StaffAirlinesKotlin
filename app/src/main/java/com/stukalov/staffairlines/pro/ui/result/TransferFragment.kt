package com.stukalov.staffairlines.pro.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.Flight
import com.stukalov.staffairlines.pro.GetNonDirectType
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.NonDirectResult
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.TransferDetails
import com.stukalov.staffairlines.pro.TransferPoint
import com.stukalov.staffairlines.pro.TransferResultAdapter
import com.stukalov.staffairlines.pro.databinding.FragmentTransferBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vView = view
        val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val result_title = GlobalStuff.OriginPoint!!.Code + " - " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(formatter0)
        (activity as AppCompatActivity).supportActionBar?.title = result_title

        val tvinfo: TextView = view.findViewById<TextView>(R.id.tvInfoTransfer)
        val transfer_lv: ListView = view.findViewById<ListView>(R.id.transferlistview)
        val tabDirect = view.findViewById<LinearLayout>(R.id.TabDirect)
        val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_transfer)

        val tdet = GetTransferDetails()

        if (tdet.tp.isNotEmpty()) {
            tvinfo.setText("Choose a city to get a list of transfer flights")
            transferadapter = TransferResultAdapter(view.context, tdet.tp, tdet.ndr)
            transfer_lv.setAdapter(transferadapter)
        }

        transfer_lv.setOnItemClickListener{parent, view, position, id ->
            val trans = parent.getItemAtPosition(position) as TransferPoint

            var reqtodo = true
            val ExtRes = GlobalStuff.ExtResult
            if (ExtRes != null)
            {
                val NonDirRes = ExtRes.NonDirectRes
                if (NonDirRes != null && NonDirRes.isNotEmpty())
                {
                    val NonRes = NonDirRes.filter { it -> it.Transfer == trans.Origin }.first()
                    if (NonRes != null)
                    {
                        reqtodo = false
                    }
                }
            }

            if (reqtodo)
            {
                //запрос
                val permlist = SM.GetStringPermitt()
                lifecycleScope.launch {
                    val result = withContext(Dispatchers.IO) {
                        SM.GetNonDirectFlights(
                            GlobalStuff.OriginPoint!!.Code,
                            GlobalStuff.DestinationPoint!!.Code,
                            trans.Origin,
                            GlobalStuff.SearchDT!!,
                            permlist,
                            GlobalStuff.Pax,
                            "USD",
                            "EN",
                            "",
                            "3.0"
                        )
                    }

                    if (result == "OK") {

                        if (tdet.tp.isNotEmpty()) {

                            spin_layout.isVisible = false
                            tvinfo.setText("Choose a city to get a list of transfer flights")
                            transferadapter = TransferResultAdapter(vView.context, tdet.tp, tdet.ndr)
                            transfer_lv.setAdapter(transferadapter)
                        }
                    }
                    else
                    {
                        spin_layout.isVisible = false
                    }
                }
            }
            else
            {
                //детализация
            }
        }

        tabDirect.setOnClickListener { view ->
            GlobalStuff.navController.navigate(R.id.resultlayout, Bundle())
        }
    }

    override fun onResume() {
        super.onResume()

        val tptmp = GetTransferDetails()

        if (tptmp.tp.isEmpty())
        {
            val transfer_lv: ListView = vView.findViewById<ListView>(R.id.transferlistview)
            val tvinfo: TextView = vView.findViewById(R.id.tvInfoTransfer)
            val spin_layout = vView.findViewById<FrameLayout>(R.id.spinner_transfer)
            spin_layout.isVisible = true
            tvinfo.setText("Searching for optimal stopovers...")

            val permlist = SM.GetStringPermitt()

            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    SM.ExtendedSearch(
                        GlobalStuff.OriginPoint!!.Code,
                        GlobalStuff.DestinationPoint!!.Code,
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

                    val tdet = GetTransferDetails()

                    if (tdet.tp.isNotEmpty()) {

                        spin_layout.isVisible = false
                        tvinfo.setText("Choose a city to get a list of transfer flights")
                        transferadapter = TransferResultAdapter(vView.context, tdet.tp, tdet.ndr)
                        transfer_lv.setAdapter(transferadapter)
                    }
                }
                else
                {
                    spin_layout.isVisible = false
                }
            }
        }
    }

    fun GetTransferDetails(): TransferDetails
    {
        var tpcnt = 0
        var rtpcnt = 0
        var tp: List<TransferPoint> = listOf()
        var rtp: List<TransferPoint> = listOf()
        var tpsource: List<TransferPoint> = listOf()
        var ndrsource: List<NonDirectResult> = listOf()
        val ExRes = GlobalStuff.ExtResult
        if (ExRes != null)
        {
            if (ExRes.TransferPoints != null) {
                tp = ExRes.TransferPoints
            }
            if (ExRes.ResultTransferPoints != null) {
                rtp = ExRes.ResultTransferPoints
            }
            if (tp != null) {
                tpcnt = tp.size
            }
            if (rtp != null) {
                rtpcnt = rtp.size
            }
            if (ExRes.NonDirectRes != null) {
                ndrsource = ExRes.NonDirectRes
            }

            if (rtpcnt == 0)
            {
                tpsource = tp
            }
            else
            {
                tpsource = rtp
            }
        }
        val res = TransferDetails(tpsource, ndrsource)
        return res
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}