package com.stukalov.staffairlines.pro.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stukalov.staffairlines.pro.ExtendedResult
import com.stukalov.staffairlines.pro.GetNonDirectType
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.MainActivity
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentHomeBinding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date


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
    //lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        //(activity as MainActivity).supportActionBar?.title = ""
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //mView=inflater.inflate(R.layout.fragment_home,container,false)
        //val btReplace: ImageButton = mView.findViewById(R.id.btReplace)

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

        SetSelPoint()
        if (GlobalStuff.SearchDT == null) {
            GlobalStuff.SearchDT = LocalDate.now()
        }
        var formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        tbSearchDT.text = GlobalStuff.SearchDT?.format(formatter)

        btReplace.setOnClickListener {
            ReplacePoint()
        }

        /*btSearch.setOnClickListener{
            Search()*/
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
            }
            else {
                val t1 = tbOrigin.text
                val t2 = lbOriginId.text
                val t3 = lbOriginCode.text
                val t4 = lbOriginCountry.text
                tbOrigin.text = tbDestination.text
                lbOriginId.text = lbDestinationId.text
                lbOriginCode.text = lbDestinationCode.text
                lbOriginCountry.text = lbDestinationCountry.text
                tbDestination.text = t1
                lbDestinationId.text = t2
                lbDestinationCode.text = t3
                lbDestinationCountry.text = t4
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
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}