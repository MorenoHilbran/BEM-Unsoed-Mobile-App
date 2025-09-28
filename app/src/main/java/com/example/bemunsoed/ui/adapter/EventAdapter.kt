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
import com.example.bemunsoed.data.model.Event

class EventAdapter(
    private val onItemClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivEventCover: ImageView = itemView.findViewById(R.id.iv_event_cover)
        private val tvEventTitle: TextView = itemView.findViewById(R.id.tv_event_title)
        private val tvEventDate: TextView = itemView.findViewById(R.id.tv_event_date)
        private val tvEventLocation: TextView = itemView.findViewById(R.id.tv_event_location)
        private val tvEventPrice: TextView = itemView.findViewById(R.id.tv_event_price)
        private val tvEventOrganizer: TextView = itemView.findViewById(R.id.tv_event_organizer)

        fun bind(event: Event) {
            tvEventTitle.text = event.title
            tvEventDate.text = event.date
            tvEventLocation.text = event.location
            tvEventPrice.text = "Gratis" // Default price since Event model doesn't have price field
            tvEventOrganizer.text = "BEM Unsoed" // Default organizer

            // Set cover image - using default image for now
            ivEventCover.setImageResource(R.drawable.test1)

            itemView.setOnClickListener {
                onItemClick(event)
            }
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}
