package com.example.eventlistapp.ui.event_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.eventlistapp.data.remote.response.Event
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentUpcomingCardBinding

class EventListAdapter(private val onItemClick: (Event) -> Unit) : RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    private var eventList: List<Event> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Event>) {
        eventList = list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = FragmentUpcomingCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = eventList.size

    class EventViewHolder(
        private val binding: FragmentUpcomingCardBinding,
        private val onItemClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            val available = event.quota?.minus(event.registrants!!)

            binding.tvTitleUpcoming.text = event.name

            binding.tvAvailableQuota.text = if (available == 0) {
                "Sold Out"
            } else {
                available.toString()
            }

            binding.tvCategory.text = event.category
            binding.tvCity.text = event.cityName
            binding.tvItemDescription.text = event.summary

            Glide.with(binding.ivCover.context)
                .load(event.imageLogo)
                .placeholder(R.mipmap.team_work)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivCover)

            // Set click listener for the item
            binding.root.setOnClickListener {
                onItemClick(event)
            }
        }
    }
}

