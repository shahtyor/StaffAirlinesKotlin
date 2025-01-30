package com.stukalov.staffairlines.pro

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
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


class HistoryAdapter(private val context: Context, private val HistoryResult: List<HistoryElement>) : BaseAdapter() {

    var CurDate: LocalDate = LocalDate.now().minusYears(5)
    var CurPosition: Int = -100
    var MaxPosition: Int = -100

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (HistoryResult.size > 0) {
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
        count = if (HistoryResult.size > 0) {
            HistoryResult.size
        } else {
            1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return HistoryResult[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (CurPosition < position && MaxPosition < position) {
            CurPosition = position
            if (MaxPosition < position)
            {
                MaxPosition = position
            }

            var convertView = convertView
            val holder: ViewHolder

            if (convertView == null) {
                holder = ViewHolder()
                val inflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                convertView = inflater.inflate(R.layout.item_historylist, null, true)

                holder.city1 = convertView.findViewById(R.id.hist_city1)
                holder.code1 = convertView.findViewById(R.id.hist_code1)
                holder.city2 = convertView.findViewById(R.id.hist_city2)
                holder.code2 = convertView.findViewById(R.id.hist_code2)
                holder.strdate = convertView.findViewById(R.id.hist_date)
                holder.cntpax = convertView.findViewById(R.id.hist_pax)

                convertView.tag = holder
            } else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = convertView.tag as ViewHolder
            }

            if (HistoryResult.size > 0) {
                val Hi = HistoryResult[position]

                val frmt = DateTimeFormatter.ofPattern("dd-MM-yyyy")

                holder.city1!!.setText(Hi.OriginName)
                holder.code1!!.setText(Hi.Origin)
                holder.city2!!.setText(Hi.DestinationName)
                holder.code2!!.setText(Hi.Destination)
                holder.strdate!!.setText(LocalDate.ofEpochDay(Hi.SearchDate).format(frmt))
                holder.cntpax!!.setText(Hi.Pax.toString() + " pax")
            }

            return convertView!!
        }

        return convertView!!
    }

    private inner class ViewHolder {
        var city1: TextView? = null
        var code1: TextView? = null
        var city2: TextView? = null
        var code2: TextView? = null
        var strdate: TextView? = null
        var cntpax: TextView? = null
    }
}