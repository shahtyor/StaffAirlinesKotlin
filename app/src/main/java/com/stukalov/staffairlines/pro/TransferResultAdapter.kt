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
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class TransferResultAdapter(private val context: Context, private val ExtResult: ExtendedResult) : BaseAdapter() {

    var CurDate: LocalDate = LocalDate.now().minusYears(5)
    var CurPosition: Int = -100
    var MaxPosition: Int = -100

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (ExtResult.NonDirectRes.size > 0) {
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
        count = if (ExtResult.NonDirectRes.size > 0) {
            ExtResult.NonDirectRes.size
        } else {
            1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return ExtResult.NonDirectRes[position]
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
                convertView = inflater.inflate(R.layout.item_transferlist, null, true)

                holder.tvTransName = convertView.findViewById(R.id.transfer_name)
                holder.tvTransSeats = convertView!!.findViewById(R.id.transfer_seats)

                convertView.tag = holder
            } else {
                // the getTag returns the viewHolder object set as a tag to the view
                holder = convertView.tag as ViewHolder
            }

            val tr = ExtResult.NonDirectRes[position]

            var trname = tr.Transfer
            val filt = GlobalStuff.Locations.filter { it -> it.Code == trname }
            if (filt.size > 0)
            {
                val loc = filt[0]
                trname = loc.Name_en + " (" + loc.Code + "), " + loc.Name_country
            }

            var trseats = "<font color='#92C55A'>" + tr.GreenCount + "</font>&nbsp;<font color='#F9AA33'>" + tr.YellowCount + "</font>&nbsp;<font color='#FF643B'>" + tr.RedCount + "</font>"


            holder.tvTransName?.setText(trname)
            holder.tvTransSeats?.setText(Html.fromHtml(trseats))

            return convertView
        }

        return convertView!!
    }

    private inner class ViewHolder {
        var tvTransName: TextView? = null
        var tvTransSeats: TextView? = null
    }
}