package com.example.bemunsoed.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R

class BannerSliderAdapter(
    private val bannerImages: List<Int>
) : RecyclerView.Adapter<BannerSliderAdapter.BannerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner_slider, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(bannerImages[position])
    }

    override fun getItemCount(): Int = bannerImages.size

    inner class BannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.iv_banner)

        fun bind(imageResource: Int) {
            imageView.setImageResource(imageResource)
        }
    }
}
