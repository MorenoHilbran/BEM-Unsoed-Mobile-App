package com.example.bemunsoed.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R

class BannerAdapter(
    private var banners: List<Map<String, Any>> = emptyList()
) : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bannerImage: ImageView = view.findViewById(R.id.bannerImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_banner, parent, false)
        return BannerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        if (position < banners.size) {
            val banner = banners[position]
            val imageUrl = banner["imageUrl"] as? String ?: ""
            val linkUrl = banner["linkUrl"] as? String ?: ""
            val title = banner["title"] as? String ?: ""

            // Use placeholder image for now
            holder.bannerImage.setImageResource(R.drawable.pesta_rakyat)

            // Set click listener to open link
            holder.bannerImage.setOnClickListener {
                if (linkUrl.isNotEmpty()) {
                    try {
                        val context = holder.itemView.context
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle error silently to prevent crash
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = banners.size

    fun updateBanners(newBanners: List<Map<String, Any>>) {
        banners = newBanners
        notifyDataSetChanged()
    }
}
