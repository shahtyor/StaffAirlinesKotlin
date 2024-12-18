package com.stukalov.staffairlines.pro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class SelPointAdapter(private val context: Context, private val PointArrayList: ArrayList<Location>) : BaseAdapter() {

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (PointArrayList.size > 0) {
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
            1
        }
        return result
    }

    override fun getCount(): Int {
        val count: Int
        count = if (PointArrayList.size > 0) {
            PointArrayList.size
        } else {
            1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return PointArrayList[position]
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
            convertView = inflater.inflate(R.layout.item_pointlist, null, true)

            holder.ivimage = convertView.findViewById(R.id.pointimage) as ImageView
            holder.tvname = convertView!!.findViewById(R.id.pointname) as TextView
            holder.tvcode = convertView.findViewById(R.id.codelabel) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        if (PointArrayList[position].ImagePath == "hotel.png")
        {
            holder.ivimage!!.setImageResource(R.drawable.hotel)
        }
        else
        {
            holder.ivimage!!.setImageResource(R.drawable.engine)
        }
        holder.tvname!!.setText(PointArrayList[position].NameWithCountry)
        holder.tvcode!!.setText(PointArrayList[position].CodeEnh)

        return convertView
    }

    private inner class ViewHolder {
        var ivimage: ImageView? = null
        var tvname: TextView? = null
        var tvcode: TextView? = null
    }
}