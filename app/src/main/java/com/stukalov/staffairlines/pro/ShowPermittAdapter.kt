package com.stukalov.staffairlines.pro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import java.util.Locale

class ShowPermittAdapter(private val context: Context, private val ACArrayList: ArrayList<PermittedAC>) : BaseAdapter() {

    override fun getViewTypeCount(): Int {
        val count: Int
        count = if (ACArrayList.size > 0) {
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
        count = if (ACArrayList.size > 0) {
            ACArrayList.size
        } else {
            1
        }
        return count
    }

    override fun getItem(position: Int): Any {
        return ACArrayList[position]
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
            convertView = inflater.inflate(R.layout.item_aclist, null, true)

            holder.ivimage = convertView.findViewById(R.id.acimage) as ImageView
            holder.tvname = convertView!!.findViewById(R.id.acname) as TextView
            holder.tvcode = convertView.findViewById(R.id.accode) as TextView

            convertView.tag = holder
        } else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = convertView.tag as ViewHolder
        }

        if (ACArrayList.size > 0) {
            val Air = ACArrayList[position]

            val mc = "_" + Air.Code.lowercase(Locale.ENGLISH)
            val identifier =
                GlobalStuff.StaffRes.getIdentifier(mc, "drawable", "com.stukalov.staffairlines.pro")

            holder.ivimage!!.setImageResource(identifier)
            holder.tvname!!.setText(Air.Permit)
            holder.tvcode!!.setText(" (" + Air.Code + ")")
        }

        return convertView
    }

    private inner class ViewHolder {
        var ivimage: ImageView? = null
        var tvname: TextView? = null
        var tvcode: TextView? = null
    }
}