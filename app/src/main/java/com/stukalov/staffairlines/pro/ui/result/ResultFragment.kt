package com.stukalov.staffairlines.pro.ui.result

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stukalov.staffairlines.pro.DirectResultAdapter
import com.stukalov.staffairlines.pro.Flight
import com.stukalov.staffairlines.pro.FlightWithPax
import com.stukalov.staffairlines.pro.GlobalStuff
import com.stukalov.staffairlines.pro.NonDirectResult
import com.stukalov.staffairlines.pro.R
import com.stukalov.staffairlines.pro.RType
import com.stukalov.staffairlines.pro.ResultType
import com.stukalov.staffairlines.pro.StaffMethods
import com.stukalov.staffairlines.pro.databinding.FragmentResultBinding
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale


class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private var resultadapter: DirectResultAdapter? = null
    private var direct_lv: RecyclerView? = null
    private val p = Paint()
    private lateinit var myContext: Context
    val SM: StaffMethods = StaffMethods()

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

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val formatter0 = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val result_title = GlobalStuff.OriginPoint!!.Code + " - " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(formatter0)
        (activity as AppCompatActivity).supportActionBar?.title = result_title
        myContext = view.context

        direct_lv = view.findViewById<RecyclerView>(R.id.directlistview)
        val tabTransfers = view.findViewById<LinearLayout>(R.id.TabTransfers)
        val llResTab = view.findViewById<LinearLayout>(R.id.ResultsTab)
        val tvResInfo = view.findViewById<TextView>(R.id.tvResultInfo)
        val llFirstSegment = view.findViewById<LinearLayout>(R.id.llFirstSegment)
        val llFirstLayout = view.findViewById<LinearLayout>(R.id.llFirstLayout)
        val btFinalNew = view.findViewById<Button>(R.id.btFinalNew)
        val llWaitInfoFinal = view.findViewById<LinearLayout>(R.id.llWaitInfoFinal)
        val tvWaitInfoFinal = view.findViewById<TextView>(R.id.tvWaitInfoFinal)

        direct_lv!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(view.context)
        direct_lv!!.layoutManager = layoutManager

        if (GlobalStuff.BackResType != null)
        {
            GlobalStuff.ResType = GlobalStuff.BackResType!!
        }

        if (GlobalStuff.ResType == ResultType.Direct) {
            GlobalStuff.BackResType = null
            llResTab.visibility = View.VISIBLE
            tvResInfo.visibility = View.GONE
            llFirstSegment.visibility = View.GONE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE

            resultadapter = DirectResultAdapter(GlobalStuff.ExtResult!!.DirectRes)
        }
        else if (GlobalStuff.ResType == ResultType.First) {
            GlobalStuff.BackResType = ResultType.First
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            llFirstSegment.visibility = View.GONE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            val infotxt = "Choose a FIRST FLIGHT " + GlobalStuff.OriginPoint!!.Code + "-" + GlobalStuff.ChangePoint
            tvResInfo.setText(infotxt)

            var ListRes: ArrayList<Flight> = arrayListOf()
            var NonRes: NonDirectResult? = null
            val ListNonDir = GlobalStuff.ExtResult!!.NonDirectRes!!.filter{ it -> it.Transfer == GlobalStuff.ChangePoint }
            if (ListNonDir != null && ListNonDir.isNotEmpty())
            {
                NonRes = ListNonDir[0]
            }
            if (NonRes != null)
            {
                ListRes = NonRes.To_airport_transfer
            }
            resultadapter = DirectResultAdapter(ListRes)
        }
        else if (GlobalStuff.ResType == ResultType.Second)
        {
            GlobalStuff.BackResType = ResultType.First
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            btFinalNew.visibility = View.GONE
            llWaitInfoFinal.visibility = View.GONE
            val infotxt = "Choose a SECOND FLIGHT " + GlobalStuff.ChangePoint + "-" + GlobalStuff.DestinationPoint!!.Code
            tvResInfo.setText(infotxt)

            llFirstSegment.visibility = View.VISIBLE
            llFirstLayout.setBackgroundColor(ContextCompat.getColor(GlobalStuff.activity, R.color.lightgray))
            InitFirstSegment(view)

            var ListRes: ArrayList<Flight> = arrayListOf()
            var NonRes: NonDirectResult? = null
            val ListNonDir = GlobalStuff.ExtResult!!.NonDirectRes.filter{ it -> it.Transfer == GlobalStuff.ChangePoint }
            if (ListNonDir != null && ListNonDir.isNotEmpty())
            {
                NonRes = ListNonDir[0]
            }
            if (NonRes != null)
            {
                val depmints = GlobalStuff.FirstSegment!!.ArrDateTime.plusMinutes(80)
                val depmaxts = GlobalStuff.FirstSegment!!.ArrDateTime.plusHours(24)
                ListRes = NonRes.From_airport_transfer.filter{ it -> it.DepDateTime >= depmints && it.DepDateTime <= depmaxts } as ArrayList<Flight>
            }
            resultadapter = DirectResultAdapter(ListRes)
        }
        else if (GlobalStuff.ResType == ResultType.Final)
        {
            GlobalStuff.BackResType = ResultType.Second
            llResTab.visibility = View.GONE
            tvResInfo.visibility = View.VISIBLE
            btFinalNew.visibility = View.VISIBLE
            llWaitInfoFinal.visibility = View.VISIBLE
            tvResInfo.visibility = View.GONE

            var waittext = ""
            if (GlobalStuff.FirstSegment != null && GlobalStuff.SecondSegment != null) {
                var waittime = Duration.between(GlobalStuff.FirstSegment!!.ArrDateTime, GlobalStuff.SecondSegment!!.DepDateTime).toMinutes().toInt()
                waittext = "Waiting time in " + GetNameChangePoint(GlobalStuff.FirstSegment!!) + " " + GetTimeAsHM2(waittime)
            }
            tvWaitInfoFinal.setText(waittext)

            llFirstSegment.visibility = View.VISIBLE
            llFirstLayout.setBackgroundColor(ContextCompat.getColor(GlobalStuff.activity, R.color.white))
            InitFirstSegment(view)

            val ListRes: ArrayList<Flight> = arrayListOf()
            ListRes.add(0, GlobalStuff.SecondSegment!!)

            resultadapter = DirectResultAdapter(ListRes)
        }

        direct_lv!!.adapter = resultadapter
        //resultadapter!!.notifyDataSetChanged()
        initSwipe()

        resultadapter!!.onItemClick = { F ->

            GlobalStuff.OneResult = F
            GlobalStuff.BackResType = null
            GlobalStuff.navController.navigate(R.id.result_one)
        }

        llFirstLayout.setOnClickListener { view ->
            GlobalStuff.OneResult = GlobalStuff.FirstSegment
            GlobalStuff.BackResType = null
            val bundle = Bundle()
            bundle.putString("ActionButton", "no")
            GlobalStuff.navController.navigate(R.id.result_one, bundle)
        }

        tabTransfers.setOnClickListener { view ->
            GlobalStuff.navController.navigate(R.id.transferlayout, Bundle())
        }

        btFinalNew.setOnClickListener { view ->
            GlobalStuff.ResType = ResultType.Direct
            GlobalStuff.BackResType = null
            GlobalStuff.FirstSegment = null
            GlobalStuff.SecondSegment = null
            GlobalStuff.navController.navigate(R.id.navigation_home)
        }
    }

    override fun onResume() {
        super.onResume()

        direct_lv!!.adapter = resultadapter
        initSwipe()
    }

    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val f = resultadapter!!.getItem(position)

                lifecycleScope.launch {

                    val FWP = FlightWithPax(f, GlobalStuff.Pax, LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    val jhg = "123"

                    if (f.InFav) {
                        val filt =
                            GlobalStuff.FavoriteList.filter { it.Fl.DepartureDateTime == f.DepartureDateTime && it.Fl.FlightNumber == f.FlightNumber && it.Fl.MarketingCarrier == f.MarketingCarrier }
                        GlobalStuff.FavoriteList.remove(filt[0])

                        AlertDialog.Builder(myContext)
                            .setTitle("Alert")
                            .setMessage("Flight unset as your favorite!")
                            .setNegativeButton("ok", { dialog, id -> dialog.cancel() })
                            .show()

                        //ofav.setImageResource(R.drawable.favoff)
                    } else {
                        GlobalStuff.FavoriteList.add(0, FWP)

                        AlertDialog.Builder(myContext)
                            .setTitle("Alert")
                            .setMessage("Flight set as your favorite!")
                            .setNegativeButton("ok", { dialog, id -> dialog.cancel() })
                            .show()

                        //ofav.setImageResource(R.drawable.favon)
                    }
                    SM.SaveFavorites()
                    resultadapter!!.refreshAdapter()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView

                    val itemFav = itemView.findViewById<TextView>(R.id.itemfav)
                    if (itemFav.text == "true")
                    {
                        icon = BitmapFactory.decodeResource(resources, R.drawable.favon)
                    }
                    else
                    {
                        icon = BitmapFactory.decodeResource(resources, R.drawable.favoff)
                    }

                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.color = Color.parseColor("#FFFFFF")
                        val background = RectF(itemView.left.toFloat(), itemView.top.toFloat(), dX, itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        //icon = BitmapFactory.decodeResource(resources, R.drawable.favoff)
                        val icon_dest = RectF(itemView.left.toFloat() + width, itemView.top.toFloat() + width, itemView.left.toFloat() + 2 * width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else {
                        p.color = Color.parseColor("#FFFFFF")
                        val background = RectF(itemView.right.toFloat() + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                        c.drawRect(background, p)
                        //icon = BitmapFactory.decodeResource(resources, R.drawable.favon)
                        val icon_dest = RectF(itemView.right.toFloat() - 2 * width, itemView.top.toFloat() + width, itemView.right.toFloat() - width, itemView.bottom.toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(direct_lv)
    }

    fun InitFirstSegment(view: View)
    {
        val tv1resultIndo = view.findViewById<TextView>(R.id.firstResultInfo)
        val tv1date = view.findViewById<TextView>(R.id.first_date_for_result)
        val fl1FrameRat = view.findViewById<FrameLayout>(R.id.firstRatingFrame)
        val iv1aclogo = view.findViewById<ImageView>(R.id.firstaclogo)
        val tv1acname = view.findViewById<TextView>(R.id.firstacname)
        val tv1timedep = view.findViewById<TextView>(R.id.firsttimedep)
        val tv1deppoint = view.findViewById<TextView>(R.id.firstdeppoint)
        val im1planepic = view.findViewById<ImageView>(R.id.firstplanepic)
        val tv1durtext = view.findViewById<TextView>(R.id.firstdurtext)
        val tv1nextday = view.findViewById<TextView>(R.id.firstnextDay)
        val tv1timearr = view.findViewById<TextView>(R.id.firsttimearr)
        val tv1arrpoint = view.findViewById<TextView>(R.id.firstarrpoint)
        val tv1cntrat = view.findViewById<TextView>(R.id.firstcntrating)

        var info1text = ""
        if (GlobalStuff.ResType == ResultType.Second) {
            info1text = "Your FIRST FLIGHT " + GlobalStuff.OriginPoint!!.Code + "-" + GlobalStuff.ChangePoint
        }
        else {
            val sdf22 = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
            info1text = "Your trip from " + GlobalStuff.OriginPoint!!.Code + " to " + GlobalStuff.DestinationPoint!!.Code + ", " + GlobalStuff.SearchDT!!.format(sdf22)
        }
        tv1resultIndo.setText(info1text)

        val f = GlobalStuff.FirstSegment!!
        val mc = "_" + f.MarketingCarrier.lowercase(Locale.ENGLISH)
        val arrdep = f.DepartureDateTime.split("T")
        val deptime = arrdep[1].substring(0, 5)
        val arrarr = f.ArrivalDateTime.split("T")
        val arrtime = arrarr[1].substring(0, 5)
        val arrday = arrarr[0].split("-")[2].toInt()
        var orig = f.Origin
        if (!f.DepartureTerminal.isNullOrEmpty()) {
            orig = orig + " (" + f.DepartureTerminal + ")"
        }
        var dest = f.Destination
        if (!f.ArrivalTerminal.isNullOrEmpty()) {
            dest = dest + " (" + f.ArrivalTerminal + ")"
        }

        val identifier =
            GlobalStuff.StaffRes.getIdentifier(
                mc,
                "drawable",
                "com.stukalov.staffairlines.pro"
            )
        val durt = GetTimeAsHM2(f.Duration)

        var MarkColor: Int = 0
        var MarkBack: Int = 0
        if (f.RatingType == RType.Good) {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_green)
            MarkBack = R.drawable.round_box_green
        } else if (f.RatingType == RType.Medium) {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_yellow)
            MarkBack = R.drawable.round_box_yellow
        } else {
            MarkColor = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_red)
            MarkBack = R.drawable.round_box_red
        }

        var nextDayVis: Int = ContextCompat.getColor(GlobalStuff.activity, R.color.sa_full_transparent)
        val dom = GlobalStuff.SearchDT?.dayOfMonth
        if (dom != arrday) {
            nextDayVis = ContextCompat.getColor(GlobalStuff.activity, R.color.black)
        }

        var visdate = View.GONE
        if (f.DepDateTime.toLocalDate() == GlobalStuff.SearchDT) {
            visdate = View.GONE
        } else {
            visdate = View.VISIBLE
        }

        val sdf = DateTimeFormatter.ofPattern("dd MMMM, yyyy")

        iv1aclogo!!.setImageResource(identifier)
        tv1acname!!.setText(f.MarketingName)
        tv1timedep!!.setText(deptime)
        tv1deppoint!!.setText(orig)
        im1planepic!!.setImageResource(R.drawable.plane1)
        tv1durtext!!.setText(durt)
        tv1timearr!!.setText(arrtime)
        tv1arrpoint!!.setText(dest)
        tv1cntrat!!.setText(f.AllPlaces)
        tv1cntrat!!.setTextColor(MarkColor)
        tv1cntrat!!.setBackgroundResource(MarkBack)
        fl1FrameRat!!.setBackgroundColor(MarkColor)
        tv1nextday!!.setTextColor(nextDayVis)
        tv1date!!.setText(sdf.format(f.DepDateTime))
        tv1date!!.visibility = visdate
    }

    fun GetNameChangePoint(f: Flight): String
    {
        var res = ""
        if (f.ArrivalCityName.isNullOrEmpty())
        {
            res = f.ArrivalName
        }
        else
        {
            res = f.ArrivalCityName
        }
        res = res + " (" + f.Destination + ")"
        return res
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
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