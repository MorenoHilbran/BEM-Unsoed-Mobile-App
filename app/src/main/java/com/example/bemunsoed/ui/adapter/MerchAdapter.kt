package com.example.bemunsoed.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.Merch

class MerchAdapter(
    private val onItemClick: (Merch) -> Unit
) : ListAdapter<Merch, MerchAdapter.MerchViewHolder>(MerchDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merch, parent, false)
        return MerchViewHolder(view)
    }

    override fun onBindViewHolder(holder: MerchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MerchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivMerchImage: ImageView = itemView.findViewById(R.id.iv_merch_image)
        private val tvMerchName: TextView = itemView.findViewById(R.id.tv_merch_name)
        private val tvMerchPrice: TextView = itemView.findViewById(R.id.tv_merch_price)
        private val tvMerchDescription: TextView = itemView.findViewById(R.id.tv_merch_description)

        fun bind(merch: Merch) {
            tvMerchName.text = if (merch.name.length > 20) {
                "${merch.name.take(20)}..."
            } else {
                merch.name
            }

            tvMerchPrice.text = merch.price
            tvMerchDescription.text = if (merch.description.length > 30) {
                "${merch.description.take(30)}..."
            } else {
                merch.description
            }

            // For now, use test1 image - later can implement image loading with Glide/Picasso
            ivMerchImage.setImageResource(R.drawable.merch1)

            itemView.setOnClickListener {
                onItemClick(merch)
            }
        }
    }

    private class MerchDiffCallback : DiffUtil.ItemCallback<Merch>() {
        override fun areItemsTheSame(oldItem: Merch, newItem: Merch): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Merch, newItem: Merch): Boolean {
            return oldItem == newItem
        }
    }
}
