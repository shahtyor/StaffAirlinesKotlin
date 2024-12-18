package com.stukalov.staffairlines.pro

import android.R.string
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.Locale


class DirectResultAdapter(private val context: Context, private val ExtResult: ExtendedResult) : BaseAdapter() {

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (ExtResult.DirectRes.size > 0) {
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
        count = if (ExtResult.DirectRes.size > 0) {
            ExtResult.DirectRes.size
        } else {
            1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return ExtResult.DirectRes[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder

        if (convertView == null) {
            holder = ViewHolder()
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_directlist, null, true)

            holder.ivaclogo = convertView.findViewById(R.id.aclogo) as ImageView
            holder.tvacname = convertView!!.findViewById(R.id.acname) as TextView
            holder.tvtimedep = convertView.findViewById(R.id.timedep) as TextView
            holder.tvdeppoint = convertView.findViewById(R.id.deppoint) as TextView
            holder.ivplanepic = convertView.findViewById(R.id.planepic) as ImageView
            holder.tvdurtext = convertView.findViewById(R.id.durtext) as TextView
            holder.tvtimearr = convertView.findViewById(R.id.timearr) as TextView
            holder.tvarrpoint = convertView.findViewById(R.id.arrpoint) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        val f = ExtResult.DirectRes[position]

        val mc = "_" + f.MarketingCarrier.toLowerCase(Locale.ENGLISH)
        val arrdep = f.DepartureDateTime.split("T")
        val deptime = arrdep[1].substring(0, 5)
        val arrarr = f.ArrivalDateTime.split("T")
        val arrtime = arrarr[1].substring(0, 5)
        var orig = f.Origin
        if (!f.DepartureTerminal.isNullOrEmpty())
        {
            orig = orig + " (" + f.DepartureTerminal + ")"
        }
        var dest = f.Destination
        if (!f.ArrivalTerminal.isNullOrEmpty())
        {
            dest = dest + " (" + f.ArrivalTerminal + ")"
        }

        val identifier = GlobalStuff.StaffRes.getIdentifier(mc, "drawable", "com.stukalov.staffairlines.pro")
        val durt = GetTimeAsHM2(f.Duration)

        holder.ivaclogo!!.setImageResource(identifier)
        holder.tvacname!!.setText(f.MarketingName)
        holder.tvtimedep!!.setText(deptime)
        holder.tvdeppoint!!.setText(orig)
        holder.ivplanepic!!.setImageResource(R.drawable.plane1)
        holder.tvdurtext!!.setText(durt)
        holder.tvtimearr!!.setText(arrtime)
        holder.tvarrpoint!!.setText(dest)

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
    }

    fun GetTimeAsHM2(minutes: Int): String {
        val h = minutes / 60
        val m = minutes - h * 60
        return h.toString().padStart(2, '0') + "h " + m.toString().padStart(2, '0').toString() + "m"
    }
}