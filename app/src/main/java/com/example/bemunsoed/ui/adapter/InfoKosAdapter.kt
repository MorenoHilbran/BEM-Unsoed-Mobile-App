package com.example.bemunsoed.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.bemunsoed.R
import com.example.bemunsoed.data.model.InfoKos

class InfoKosAdapter(
    private val onItemClick: (InfoKos) -> Unit
) : ListAdapter<InfoKos, InfoKosAdapter.InfoKosViewHolder>(InfoKosDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoKosViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_infokos, parent, false)
        return InfoKosViewHolder(view)
    }

    override fun onBindViewHolder(holder: InfoKosViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class InfoKosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivInfoKosImage: ImageView = itemView.findViewById(R.id.iv_infokos_image)
        private val tvInfoKosNama: TextView = itemView.findViewById(R.id.tv_infokos_nama)
        private val tvInfoKosHarga: TextView = itemView.findViewById(R.id.tv_infokos_harga)
        private val tvInfoKosTipe: TextView = itemView.findViewById(R.id.tv_infokos_tipe)
        private val tvInfoKosAlamat: TextView = itemView.findViewById(R.id.tv_infokos_alamat)

        fun bind(infoKos: InfoKos) {
            tvInfoKosNama.text = infoKos.nama
            tvInfoKosHarga.text = infoKos.harga
            tvInfoKosTipe.text = infoKos.tipe
            tvInfoKosAlamat.text = infoKos.alamat

            // Load image dengan rounded corners
            Glide.with(itemView.context)
                .load(infoKos.gambar)
                .apply(
                    RequestOptions()
                        .transform(RoundedCorners(24))
                        .placeholder(R.drawable.ic_circle_outline)
                        .error(R.drawable.ic_circle_outline)
                )
                .into(ivInfoKosImage)

            itemView.setOnClickListener {
                onItemClick(infoKos)
            }
        }
    }

    private class InfoKosDiffCallback : DiffUtil.ItemCallback<InfoKos>() {
        override fun areItemsTheSame(oldItem: InfoKos, newItem: InfoKos): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InfoKos, newItem: InfoKos): Boolean {
            return oldItem == newItem
        }
    }
}
