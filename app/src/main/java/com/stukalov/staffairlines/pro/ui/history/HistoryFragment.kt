package com.stukalov.staffairlines.pro.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.HistoryAdapter
import com.stukalov.staffairlines.pro.HistoryElement
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.SelectedPoint
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentHistoryBinding
import java.time.LocalDate

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    lateinit var historyadapter: HistoryAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val SM: StaffMethods = StaffMethods()

        val hist_lv: ListView = view.findViewById<ListView>(R.id.histlistview)

        SM.ReadHistory()
        historyadapter = HistoryAdapter(view.context, GlobalStuff.HistoryList)

        hist_lv.setAdapter(historyadapter)

        hist_lv.setOnItemClickListener { parent, view, position, id ->

            val H = parent.getItemAtPosition(position) as HistoryElement

            GlobalStuff.OriginPoint = SelectedPoint(H.OriginId.toInt(), H.OriginName, H.Origin, PointType.Origin, "")
            GlobalStuff.DestinationPoint = SelectedPoint(H.DestinationId.toInt(), H.DestinationName, H.Destination, PointType.Destination, "")
            GlobalStuff.Pax = H.Pax
            val SearchDT = LocalDate.ofEpochDay(H.SearchDate)
            if (SearchDT >= LocalDate.now())
            {
                GlobalStuff.SearchDT = SearchDT
            }

            GlobalStuff.navController.navigate(R.id.main_frag)
        }
    }

    override fun onResume() {
        super.onResume()

        val event = GlobalStuff.GetBaseEvent("History", true, true)
        GlobalStuff.amplitude?.track(event)

        GlobalStuff.setActionBar(true, false, "History")
        GlobalStuff.FirstHomeOpen = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}