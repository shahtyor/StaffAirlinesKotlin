package com.stukalov.staffairlines.pro.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.stukalov.staffairlines.pro.FavouritesAdapter
import com.stukalov.staffairlines.pro.FlightWithPax
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.HistoryAdapter
import com.stukalov.staffairlines.pro.HistoryElement
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.SelectedPoint
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

        GlobalStuff.navView!!.setVisibility(View.VISIBLE)

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

            GlobalStuff.navController.navigate(R.id.navigation_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}