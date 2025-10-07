package com.example.bemunsoed.ui.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.Kuliner

class KulinerAdapter(
    private val onItemClick: (Kuliner) -> Unit
) : ListAdapter<Kuliner, KulinerAdapter.KulinerViewHolder>(KulinerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KulinerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kuliner, parent, false)
        return KulinerViewHolder(view)
    }

    override fun onBindViewHolder(holder: KulinerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class KulinerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivKulinerImage: ImageView = itemView.findViewById(R.id.iv_kuliner_image)
        private val tvKulinerNama: TextView = itemView.findViewById(R.id.tv_kuliner_nama)
        private val tvKulinerHarga: TextView = itemView.findViewById(R.id.tv_kuliner_harga)
        private val tvKulinerAlamat: TextView = itemView.findViewById(R.id.tv_kuliner_alamat)

        fun bind(kuliner: Kuliner) {
            tvKulinerNama.text = kuliner.nama
            tvKulinerHarga.text = kuliner.harga
            tvKulinerAlamat.text = kuliner.alamat

            // Load image dengan rounded corners
            Glide.with(itemView.context)
                .load(kuliner.gambar)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(24))
                        .placeholder(R.drawable.ic_circle_outline)
                        .error(R.drawable.ic_circle_outline)
                )
                .into(ivKulinerImage)

            itemView.setOnClickListener {
                onItemClick(kuliner)
            }
        }
    }

    private class KulinerDiffCallback : DiffUtil.ItemCallback<Kuliner>() {
        override fun areItemsTheSame(oldItem: Kuliner, newItem: Kuliner): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Kuliner, newItem: Kuliner): Boolean {
            return oldItem == newItem
        }
    }
}
