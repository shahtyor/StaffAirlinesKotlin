package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class DirectResultAdapter(private val context: Context, private val Res: List<Flight>, private val Direct: Boolean) : BaseAdapter() {

    var CurDate: LocalDate = LocalDate.now().minusYears(5)

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (Res.size > 0) {
            getCount()
        } else {
            1
        }
        return count
    }

    override fun getItemViewType(position: Int): Int {
        val result: Int
        result = if (position > 0) {
            position
        } else {
            0
        }
        return result
    }

    override fun getCount(): Int {
        val count: Int
        count = if (Res.size > 0) {
            Res.size
        } else {
            0
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return Res[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_directlist, null, true)

            holder.ivaclogo = convertView.findViewById(R.id.aclogo)
            holder.tvacname = convertView!!.findViewById(R.id.acname)
            holder.tvtimedep = convertView.findViewById(R.id.timedep)
            holder.tvdeppoint = convertView.findViewById(R.id.deppoint)
            holder.ivplanepic = convertView.findViewById(R.id.planepic)
            holder.tvdurtext = convertView.findViewById(R.id.durtext)
            holder.tvtimearr = convertView.findViewById(R.id.timearr)
            holder.tvarrpoint = convertView.findViewById(R.id.arrpoint)
            holder.tvcntrat = convertView.findViewById(R.id.cntrating)
            holder.flframelay = convertView.findViewById(R.id.RatingFrame)
            holder.tvnextday = convertView.findViewById(R.id.nextDay)
            holder.tvdateoneres = convertView.findViewById(R.id.date_for_result)
            holder.llWaitInfo = convertView.findViewById(R.id.llWaitInfo)
            holder.tvWaitInfo = convertView.findViewById(R.id.tvWaitInfo)
            holder.llFlightLayout = convertView.findViewById(R.id.llFlightLayout)
            holder.tvResCheckTransfer = convertView.findViewById(R.id.tvResCheckTransfer)

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        if (Res.size > 0) {
            val f = Res[position]

            if (f.FlightNumber == "")
            {
                holder.tvdateoneres?.visibility = View.GONE
                holder.tvResCheckTransfer?.visibility = View.VISIBLE
                holder.llFlightLayout?.visibility = View.GONE
                holder.tvResCheckTransfer?.setText(Html.fromHtml("<u>Check transfer flights</u>"))
            }
            else {
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
                    var waittime =
                        Duration.between(GlobalStuff.FirstSegment!!.ArrDateTime, f.DepDateTime)
                            .toMinutes().toInt()
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
                if (GlobalStuff.ResType == ResultType.Second) {
                    waitvis = View.VISIBLE
                }

                if (position == 0)
                {
                    CurDate = LocalDate.now().minusYears(5)
                }
                var step = position
                var visdate = View.GONE
                if (f.DepDateTime.toLocalDate() == CurDate) {
                    visdate = View.GONE
                } else {
                    CurDate = f.DepDateTime.toLocalDate()
                    visdate = View.VISIBLE
                }

                val sdf = DateTimeFormatter.ofPattern("dd MMMM, yyyy")

                holder.llFlightLayout?.visibility = View.VISIBLE
                holder.tvResCheckTransfer?.visibility = View.GONE
                holder.ivaclogo!!.setImageResource(identifier)
                holder.tvacname!!.setText(f.MarketingName)
                holder.tvtimedep!!.setText(deptime)
                holder.tvdeppoint!!.setText(orig)
                holder.ivplanepic!!.setImageResource(R.drawable.plane1)
                holder.tvdurtext!!.setText(durt)
                holder.tvtimearr!!.setText(arrtime)
                holder.tvarrpoint!!.setText(dest)
                holder.tvcntrat!!.setText(f.AllPlaces)
                holder.tvcntrat!!.setTextColor(MarkColor)
                holder.tvcntrat!!.setBackgroundResource(MarkBack)
                holder.flframelay!!.setBackgroundColor(MarkColor)
                holder.tvnextday!!.setTextColor(nextDayVis)
                holder.tvdateoneres?.setText(sdf.format(f.DepDateTime))
                holder.tvdateoneres?.visibility = visdate
                holder.llWaitInfo!!.visibility = waitvis
                holder.tvWaitInfo!!.setText(waittext)
                val params = holder.llFlightLayout!!.getLayoutParams()
                //params.height = params.height + llheight
                holder.llFlightLayout!!.setLayoutParams(params)
            }
        }

        return convertView
    }

    private inner class ViewHolder {
        var ivaclogo: ImageView? = null
        var tvacname: TextView? = null
        var tvtimedep: TextView? = null
        var tvdeppoint: TextView? = null
        var ivplanepic: ImageView? = null
        var tvdurtext: TextView? = null
        var tvtimearr: TextView? = null
        var tvarrpoint: TextView? = null
        var tvcntrat: TextView? = null
        var flframelay: FrameLayout? = null
        var tvnextday: TextView? = null
        var tvdateoneres: TextView? = null
        var llWaitInfo: LinearLayout? = null
        var tvWaitInfo: TextView? = null
        var llFlightLayout: LinearLayout? = null
        var tvResCheckTransfer: TextView? = null
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }
}