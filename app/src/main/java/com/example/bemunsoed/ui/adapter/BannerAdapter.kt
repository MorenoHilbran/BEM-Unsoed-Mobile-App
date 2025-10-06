package com.example.bemunsoed.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.Banner

class BannerAdapter(
    private var banners: List<Banner> = emptyList()
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bannerImage: ImageView = view.findViewById(R.id.bannerImage)
        val bannerTitle: android.widget.TextView = view.findViewById(R.id.bannerTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        if (position < banners.size) {
            val banner = banners[position]
            // Load image from imageUrl using Glide
            Glide.with(holder.itemView.context)
                .load(banner.imageUrl)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.bannerImage)
            // Set title
            holder.bannerTitle.text = banner.title
            // Set click listener to open linkUrl
            holder.bannerImage.setOnClickListener {
                if (!banner.linkUrl.isNullOrEmpty()) {
                    try {
                        val context = holder.itemView.context
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(banner.linkUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle error silently
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = banners.size

    fun updateBanners(newBanners: List<Banner>) {
        banners = newBanners
        notifyDataSetChanged()
    }
}
