package com.stukalov.staffairlines.pro.ui.result

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.Flight
import com.stukalov.staffairlines.pro.GetNonDirectType
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.StaffMethods
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
        val title = arguments?.getString("result_title")
        (activity as AppCompatActivity).supportActionBar?.title = title

        val transfer_lv: ListView = view.findViewById<ListView>(R.id.transferlistview)
        val tabDirect = view.findViewById<LinearLayout>(R.id.TabDirect)

        if (GlobalStuff.ExtResult?.NonDirectRes != null) {
            transferadapter = TransferResultAdapter(view.context, GlobalStuff.ExtResult!!)

            transfer_lv.setAdapter(transferadapter)
        }

        transfer_lv.setOnItemClickListener{parent, view, position, id ->
            val fl = parent.getItemAtPosition(position) as Flight

            GlobalStuff.OneResult = fl
            GlobalStuff.navController.navigate(R.id.result_one)
        }

        tabDirect.setOnClickListener { view ->
            GlobalStuff.navController.navigate(R.id.resultlayout, Bundle())
        }
    }

    override fun onResume() {
        super.onResume()

        if (GlobalStuff.ExtResult?.NonDirectRes == null)
        {
            val transfer_lv: ListView = vView.findViewById<ListView>(R.id.transferlistview)
            val spin_layout = vView.findViewById<FrameLayout>(R.id.spinner_transfer)
            spin_layout.isVisible = true

            val permlist = SM.GetStringPermitt()

            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    SM.ExtendedSearch(
                        GlobalStuff.OriginPoint!!.Code,
                        GlobalStuff.DestinationPoint!!.Code,
                        GlobalStuff.SearchDT!!,
                        permlist,
                        false,
                        GetNonDirectType.on,
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

                if (result == "OK" && GlobalStuff.ExtResult?.NonDirectRes != null) {
                    spin_layout.isVisible = false
                    transferadapter = TransferResultAdapter(vView.context, GlobalStuff.ExtResult!!)
                    transfer_lv.setAdapter(transferadapter)
                }
                else
                {
                    spin_layout.isVisible = false
                }
            }
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