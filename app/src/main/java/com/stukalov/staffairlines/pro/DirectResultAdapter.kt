package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.ArrayList

internal class DirectResultAdapter(private val Res: ArrayList<Flight>) : RecyclerView.Adapter<DirectResultAdapter.ViewHolder>() {

    var onItemClick: ((Flight) -> Unit)? = null
    var CurDate: LocalDate = LocalDate.now().minusYears(5)

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DirectResultAdapter.ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_directlist, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {

        val f = Res[i]

        val mc = "_" + f.MarketingCarrier.lowercase(Locale.ENGLISH)
        val arrdep = f.DepartureDateTime.split("T")
        val deptime = arrdep[1].substring(0, 5)
        val depday = arrdep[0].split("-")[2].toInt()
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

        var waittext = ""
        var llheight = 0
        if (GlobalStuff.FirstSegment != null) {
            var waittime = Duration.between(GlobalStuff.FirstSegment!!.ArrDateTime, f.DepDateTime).toMinutes().toInt()
            waittext = "Waiting time " + GetTimeAsHM2(waittime)
            llheight = 40
        }

        val identifier =
            GlobalStuff.StaffRes.getIdentifier(
                mc,
                "drawable",
                "com.stukalov.staffairlines.pro"
            )
        val durt = GetTimeAsHM2(f.Duration)

        val context = GlobalStuff.activity

        var MarkColor: Int = 0
        var MarkBack: Int = 0
        if (f.RatingType == RType.Good) {
            MarkColor = ContextCompat.getColor(context, R.color.sa_green)
            MarkBack = R.drawable.round_box_green
        } else if (f.RatingType == RType.Medium) {
            MarkColor = ContextCompat.getColor(context, R.color.sa_yellow)
            MarkBack = R.drawable.round_box_yellow
        } else {
            MarkColor = ContextCompat.getColor(context, R.color.sa_red)
            MarkBack = R.drawable.round_box_red
        }

        var nextDayVis: Int = ContextCompat.getColor(context, R.color.sa_full_transparent)
        if (depday != arrday) {
            nextDayVis = ContextCompat.getColor(context, R.color.black)
        }

        var waitvis = View.GONE
        if (GlobalStuff.ResType == ResultType.Second)
        {
            waitvis = View.VISIBLE
        }

        var step = i
        var visdate = View.GONE
        if (f.DepDateTime.toLocalDate() == CurDate) {
            visdate = View.GONE
        } else {
            CurDate = f.DepDateTime.toLocalDate()
            visdate = View.VISIBLE
        }

        val sdf = DateTimeFormatter.ofPattern("dd MMMM, yyyy")

        viewHolder.ivaclogo.setImageResource(identifier)
        viewHolder.tvacname.setText(f.MarketingName)
        viewHolder.tvitemfav.setText(f.InFav.toString())
        viewHolder.tvtimedep.setText(deptime)
        viewHolder.tvdeppoint.setText(orig)
        viewHolder.ivplanepic.setImageResource(R.drawable.plane1)
        viewHolder.tvdurtext.setText(durt)
        viewHolder.tvtimearr.setText(arrtime)
        viewHolder.tvarrpoint.setText(dest)
        viewHolder.tvcntrat.setText(f.AllPlaces)
        viewHolder.tvcntrat.setTextColor(MarkColor)
        viewHolder.tvcntrat.setBackgroundResource(MarkBack)
        viewHolder.flframelay.setBackgroundColor(MarkColor)
        viewHolder.tvnextday.setTextColor(nextDayVis)
        viewHolder.tvdateoneres.setText(sdf.format(f.DepDateTime))
        viewHolder.tvdateoneres.visibility = visdate
        viewHolder.llWaitInfo.visibility = waitvis
        viewHolder.tvWaitInfo.setText(waittext)
        val params = viewHolder.llFlightLayout.getLayoutParams()
        params.height = params.height + llheight
        viewHolder.llFlightLayout.setLayoutParams(params)
    }

    override fun getItemCount(): Int {
        return Res.size
    }

    fun getItem(position: Int): Flight {
        return Res[position]
    }

    fun refreshAdapter()
    {
        val pl: PlaceInfo = PlaceInfo(1, 1, 1, LocalDate.now(), 1, "", "")
        Res.add(0, Flight("", "", "", "", "", "", "", "", "", "", 0, 0, "", "", "", "", "", "", "", "", "", 0, arrayOf(), 0, 0, 0, 0, "", 1, 1.0f, 1, "", pl, false))
        //Res.removeAt(0)
    }

    @SuppressLint("ClickableViewAccessibility")
    internal inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivaclogo: ImageView
        var tvacname: TextView
        var tvitemfav: TextView
        var tvtimedep: TextView
        var tvdeppoint: TextView
        var ivplanepic: ImageView
        var tvdurtext: TextView
        var tvtimearr: TextView
        var tvarrpoint: TextView
        var tvcntrat: TextView
        var flframelay: FrameLayout
        var tvnextday: TextView
        var tvdateoneres: TextView
        var llWaitInfo: LinearLayout
        var tvWaitInfo: TextView
        var llFlightLayout: LinearLayout

        init {

            ivaclogo = view.findViewById(R.id.aclogo) as ImageView
            tvacname = view.findViewById(R.id.acname) as TextView
            tvitemfav = view.findViewById(R.id.itemfav) as TextView
            tvtimedep = view.findViewById(R.id.timedep) as TextView
            tvdeppoint = view.findViewById(R.id.deppoint) as TextView
            ivplanepic = view.findViewById(R.id.planepic) as ImageView
            tvdurtext = view.findViewById(R.id.durtext) as TextView
            tvtimearr = view.findViewById(R.id.timearr) as TextView
            tvarrpoint = view.findViewById(R.id.arrpoint) as TextView
            tvcntrat = view.findViewById(R.id.cntrating) as TextView
            flframelay = view.findViewById(R.id.RatingFrame) as FrameLayout
            tvnextday = view.findViewById(R.id.nextDay) as TextView
            tvdateoneres = view.findViewById(R.id.date_for_result) as TextView
            llWaitInfo = view.findViewById(R.id.llWaitInfo) as LinearLayout
            tvWaitInfo = view.findViewById(R.id.tvWaitInfo) as TextView
            llFlightLayout = view.findViewById(R.id.llFlightLayout) as LinearLayout

            itemView.setOnClickListener {
                onItemClick?.invoke(Res[adapterPosition])
            }
        }
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }
}