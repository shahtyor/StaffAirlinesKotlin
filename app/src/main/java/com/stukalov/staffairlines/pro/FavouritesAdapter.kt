package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale


class FavouritesAdapter(private val context: Context, private val FavResult: List<FlightWithPax>) : BaseAdapter() {

    var CurDate: LocalDate = LocalDate.now().minusYears(5)
    var CurPosition: Int = -100
    var MaxPosition: Int = -100

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (FavResult.size > 0) {
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
        count = if (FavResult.size > 0) {
            FavResult.size
        } else {
            1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return FavResult[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        /*if (CurPosition < position && MaxPosition < position) {
            CurPosition = position
            if (MaxPosition < position)
            {
                MaxPosition = position
            }*/

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

                convertView.tag = holder
            } else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = convertView.tag as ViewHolder
            }

            if (FavResult.size > 0) {
                val FWP = FavResult[position]
                val f = FWP.Fl

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

                var SeatsAndRating = true
                val HoursBeforeDeparture =
                    Duration.between(LocalDateTime.now(), f.DepDateTime).toHours()
                var SaveTime: Int = 0
                if (HoursBeforeDeparture <= 24) {
                    SaveTime = 5
                } else {
                    SaveTime = 15
                }
                val MinuteFromInfo = Duration.between(
                    LocalDateTime.ofEpochSecond(FWP.Dt, 0, ZoneOffset.UTC),
                    LocalDateTime.now(ZoneOffset.UTC)
                ).toMinutes()
                if (MinuteFromInfo > SaveTime) SeatsAndRating = false;

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

                var TextForAllPlaces = f.AllPlaces
                if (!SeatsAndRating) {
                    MarkColor = ContextCompat.getColor(context, R.color.staff_dark)
                    MarkBack = R.drawable.round_box_gray
                    TextForAllPlaces = "?"
                }

                var nextDayVis: Int = ContextCompat.getColor(context, R.color.sa_full_transparent)
                val dom = GlobalStuff.SearchDT?.dayOfMonth
                if (dom != arrday) {
                    nextDayVis = ContextCompat.getColor(context, R.color.black)
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

                holder.ivaclogo!!.setImageResource(identifier)
                holder.tvacname!!.setText(f.MarketingName)
                holder.tvtimedep!!.setText(deptime)
                holder.tvdeppoint!!.setText(orig)
                holder.ivplanepic!!.setImageResource(R.drawable.plane1)
                holder.tvdurtext!!.setText(durt)
                holder.tvtimearr!!.setText(arrtime)
                holder.tvarrpoint!!.setText(dest)
                holder.tvcntrat!!.setText(TextForAllPlaces)
                holder.tvcntrat!!.setTextColor(MarkColor)
                holder.tvcntrat!!.setBackgroundResource(MarkBack)
                holder.flframelay!!.setBackgroundColor(MarkColor)
                holder.tvnextday!!.setTextColor(nextDayVis)
                holder.tvdateoneres!!.setText(sdf.format(f.DepDateTime))
                holder.tvdateoneres!!.visibility = visdate
                holder.llWaitInfo!!.visibility = View.GONE
            }

            holder.llWaitInfo!!.visibility = View.GONE
            return convertView
        /*}

        return convertView!!*/
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
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }
}