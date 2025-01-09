package com.stukalov.staffairlines.pro.ui.result

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.stukalov.staffairlines.pro.DirectResultAdapter
import com.stukalov.staffairlines.pro.Flight
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.Location
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.SelectedPoint
import com.stukalov.staffairlines.pro.databinding.FragmentResultBinding


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

        val title = arguments?.getString("result_title")
        (activity as AppCompatActivity).supportActionBar?.title = title

        val direct_lv: ListView = view.findViewById<ListView>(R.id.directlistview)

        resultadapter = DirectResultAdapter(view.context, GlobalStuff.ExtResult!!)

        direct_lv.setAdapter(resultadapter)

        direct_lv.setOnItemClickListener{parent, view, position, id ->
            var fl = parent.getItemAtPosition(position) as Flight

            GlobalStuff.OneResult = fl
            GlobalStuff.navController.navigate(R.id.result_one)
        }
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