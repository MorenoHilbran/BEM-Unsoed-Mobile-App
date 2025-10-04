package com.example.bemunsoed.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.ui.home.model.MerchandiseItem

class MerchandiseAdapter(private val merchandiseList: List<MerchandiseItem>) :
    RecyclerView.Adapter<MerchandiseAdapter.MerchandiseViewHolder>() {

    class MerchandiseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivMerchImage: ImageView = itemView.findViewById(R.id.iv_merch_image)
        val tvMerchName: TextView = itemView.findViewById(R.id.tv_merch_name)
        val tvMerchPrice: TextView = itemView.findViewById(R.id.tv_merch_price)
        val tvMerchDescription: TextView = itemView.findViewById(R.id.tv_merch_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchandiseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merch, parent, false)
        return MerchandiseViewHolder(view)
    }

    override fun onBindViewHolder(holder: MerchandiseViewHolder, position: Int) {
        val item = merchandiseList[position]
        holder.ivMerchImage.setImageResource(item.imageRes)
        holder.tvMerchName.text = item.name
        holder.tvMerchPrice.text = item.price
        holder.tvMerchDescription.text = item.description
    }

    override fun getItemCount(): Int = merchandiseList.size
}
