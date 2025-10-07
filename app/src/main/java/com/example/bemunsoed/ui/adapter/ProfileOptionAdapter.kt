package com.example.bemunsoed.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bemunsoed.R

class ProfileOptionAdapter(
    private val options: List<ProfileOption>,
    private val onOptionSelected: (ProfileOption) -> Unit
) : RecyclerView.Adapter<ProfileOptionAdapter.ViewHolder>() {

    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_option, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = options.size

    fun setSelectedPosition(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPreview: ImageView = itemView.findViewById(R.id.iv_option_preview)
        private val tvName: TextView = itemView.findViewById(R.id.tv_option_name)
        private val ivSelected: ImageView = itemView.findViewById(R.id.iv_selected_indicator)

        fun bind(option: ProfileOption, isSelected: Boolean) {
            ivPreview.setImageResource(option.drawableRes)
            tvName.text = option.name
            ivSelected.visibility = if (isSelected) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    setSelectedPosition(position)
                    onOptionSelected(option)
                }
            }
        }
    }
}

data class ProfileOption(
    val name: String,
    val drawableRes: Int,
    val id: String
)
