package com.stukalov.staffairlines.pro.ui.home

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stukalov.staffairlines.pro.ExtendedResult
import com.stukalov.staffairlines.pro.GetNonDirectType
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.HistoryElement
import com.stukalov.staffairlines.pro.MainActivity
import com.stukalov.staffairlines.pro.PointType
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.SelectedPoint
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentHomeBinding
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
    lateinit var btMinusBut: ImageButton
    lateinit var btPlusBut: ImageButton
    lateinit var tbCntPass: TextView
    lateinit var btDate: ImageButton
    val SM: StaffMethods = StaffMethods()

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
        btMinusBut = view.findViewById(R.id.minusbut)
        btPlusBut =  view.findViewById(R.id.plusbut)
        tbCntPass = view.findViewById(R.id.cntpass)
        btDate = view.findViewById(R.id.datebutton)

        try {
            GlobalStuff.navView.setVisibility(View.VISIBLE)
        } catch (ex: Exception)
        {
        }

        SetSelPoint()
        if (GlobalStuff.SearchDT == null) {
            GlobalStuff.SearchDT = LocalDate.now()
        }
        var formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        tbSearchDT.text = GlobalStuff.SearchDT?.format(formatter)

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


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        if (GlobalStuff.OwnAC == null)
        {
            val bundle = Bundle()
            bundle.putString("SelACMode", "home")

            if (GlobalStuff.Airlines.size > 0)
            {
                GlobalStuff.navController.navigate(R.id.sel_ac_frag, bundle)
            }
            else {
                lifecycleScope.launch {
                    val jsonair = withContext(Dispatchers.IO) { SM.LoadAirlines() }

                    if (jsonair.isNotEmpty() && GlobalStuff.Airlines.size > 0) {
                        GlobalStuff.navController.navigate(R.id.sel_ac_frag, bundle)
                    }
                }
            }
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

    fun add_to_history(Origin: String, Destination: String, OriginId: String, DestinationId: String, OriginName: String, DestinationName: String, SearchDt: Long, Pax: Int)
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

    fun search_click(view: View) {

        if (GlobalStuff.OriginPoint != null && GlobalStuff.DestinationPoint != null) {

            val spin_layout = view.findViewById<FrameLayout>(R.id.spinner_home)
            spin_layout.isVisible = true
            GlobalStuff.navView.visibility = View.GONE

            SetDisable(false)
            val OP = GlobalStuff.OriginPoint
            val DP = GlobalStuff.DestinationPoint
            add_to_history(OP!!.Code, DP!!.Code, OP.Id.toString(), DP.Id.toString(), OP.Name, DP.Name, GlobalStuff.SearchDT!!.toEpochDay(), tbCntPass.text.toString().toInt())

            val permlist = SM.GetStringPermitt()

            lifecycleScope.launch {
                val result = withContext(Dispatchers.IO) {
                    SM.ExtendedSearch(
                        GlobalStuff.OriginPoint!!.Code,
                        GlobalStuff.DestinationPoint!!.Code,
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
                        "--"
                    )
                }

                if (result == "OK" && GlobalStuff.ExtResult != null) {
                    val bundle = Bundle()
                    bundle.putString("keyDashBoard", "No")
                    GlobalStuff.navController.navigate(R.id.resultlayout, bundle)
                }
                else
                {
                    SetDisable(true)
                    spin_layout.isVisible = false
                    GlobalStuff.navView.visibility = View.VISIBLE
                    var serr: String = ""
                    if (GlobalStuff.ExtResult == null)
                    {
                        serr = " , er=null"
                    }
                    val toast = Toast.makeText(context, result + serr, Toast.LENGTH_LONG)
                    toast.show()
                }
            }
        }
    }
}