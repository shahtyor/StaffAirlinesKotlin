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


class TransferResultAdapter(private val context: Context, private val tp: List<TransferPoint>, private val ndr: List<NonDirectResult>) : BaseAdapter() {

    var CurDate: LocalDate = LocalDate.now().minusYears(5)
    var CurPosition: Int = -100
    var MaxPosition: Int = -100

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (tp.size > 0) {
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
        count = if (tp.size > 0) {
            tp.size
        } else {
            1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return tp[position]
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

            val tr = tp[position]

            var trname = tr.Name + " (" + tr.Origin + "), " + tr.CountryName
            var trseats = ""

            if (ndr.size > 0) {
                val filt = ndr.filter { it -> it.Transfer == tr.Origin }
                if (filt.size > 0) {
                    val ndrone = filt[0]
                    trseats =
                        "<font color='#92C55A'>" + ndrone.GreenCount + "</font>&nbsp;<font color='#F9AA33'>" + ndrone.YellowCount + "</font>&nbsp;<font color='#FF643B'>" + ndrone.RedCount + "</font>"
                }
            }

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