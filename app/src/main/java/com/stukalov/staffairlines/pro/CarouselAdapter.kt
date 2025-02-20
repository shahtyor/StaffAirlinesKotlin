package com.stukalov.staffairlines.pro

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CarouselAdapter(private val carouselDataList: ArrayList<CarouselData>) :
    RecyclerView.Adapter<CarouselAdapter.CarouselItemViewHolder>() {

    class CarouselItemViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselItemViewHolder {
        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return CarouselItemViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: CarouselItemViewHolder, position: Int) {
        val tvCarTitle = holder.itemView.findViewById<TextView>(R.id.tvCarouselTitle)
        val ivCar = holder.itemView.findViewById<ImageView>(R.id.ivCarousel)
        val tvCarDesc = holder.itemView.findViewById<TextView>(R.id.tvCarouselDesc)

        val data = carouselDataList[position]

        val img = GlobalStuff.StaffRes.getIdentifier(data.image, "drawable", "com.stukalov.staffairlines.pro")

        tvCarTitle.text = data.title
        ivCar.setImageResource(img)
        tvCarDesc.text = data.desc
    }

    override fun getItemCount(): Int {
        return carouselDataList.size
    }

}