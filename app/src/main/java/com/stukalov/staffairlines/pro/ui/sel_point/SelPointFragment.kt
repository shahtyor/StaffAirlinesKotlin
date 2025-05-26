package com.stukalov.staffairlines.pro.ui.sel_point

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.Location
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.SelPointAdapter
import com.stukalov.staffairlines.pro.SelectedPoint
import com.stukalov.staffairlines.pro.databinding.FragmentSelPointBinding


class SelPointFragment : Fragment() {

    private var _binding: FragmentSelPointBinding? = null
    lateinit var pointlistadapter: SelPointAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val selPointViewModel =
            ViewModelProvider(this).get(SelPointViewModel::class.java)

        _binding = FragmentSelPointBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.selpointtext

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val points_lv: ListView = view.findViewById<ListView>(R.id.pointlistview)

        val strmode = getArguments()?.getString("PointMode")
        val PointMode: PointType = if (strmode == "Origin")
        {
            PointType.Origin
        }
        else
        {
            PointType.Destination
        }

        try {

            val sel_point_text = view.findViewById<EditText>(R.id.selpointtext)
            sel_point_text.addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int) {}

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int) {
                    val text = sel_point_text.text.toString().uppercase()
                    if (!text.isNullOrEmpty())
                    {
                        //var tmpLoc = StaffAirlines().Locations
                        val tmpLoc = GlobalStuff.Locations
                        //val tmpLoc2 = tmpLoc.filter { it.Name_en.contains("Moscow") }

                        var tmp = tmpLoc.asSequence().filter { !it.Name_en.isNullOrEmpty() && it.Name_en.uppercase().contains(text) }.take(20).toMutableList()

                        if (text.length == 3) {
                            val liata = tmpLoc.asSequence().filter { it.Code == text }.take(1).firstOrNull();
                            if (liata != null)
                            {
                                tmp.add(0, liata)
                            }
                        }

                        //GlobalStuff.tmppointlist = ArrayList(tmp)

                        if (tmp.count() > 0) {
                            pointlistadapter =
                                SelPointAdapter(view.context, ArrayList(tmp))
                            points_lv.setAdapter(pointlistadapter)
                        }
                    }
                }
            })

            sel_point_text.requestFocus()
            // open the soft keyboard
            val imm = GlobalStuff.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(sel_point_text, InputMethodManager.SHOW_IMPLICIT)

            points_lv.setOnItemClickListener{parent, view, position, id ->
                var point = parent.getItemAtPosition(position) as Location
                if (PointMode == PointType.Origin) {
                    GlobalStuff.OriginPoint = SelectedPoint(point.Id, point.NameWithCountry, point.Code, PointMode, point.Name_country)
                }
                else
                {
                    GlobalStuff.DestinationPoint = SelectedPoint(point.Id, point.NameWithCountry, point.Code, PointMode, point.Name_country)
                }

                GlobalStuff.HomeFromSelect = true
                GlobalStuff.navController.navigateUp()
             }
        }
        catch (e: Exception)
        {
            val stre = e.message + "..." + e.stackTrace
        }
    }

    override fun onResume() {
        super.onResume()

        GlobalStuff.setActionBar(true, true, "Flights")
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}