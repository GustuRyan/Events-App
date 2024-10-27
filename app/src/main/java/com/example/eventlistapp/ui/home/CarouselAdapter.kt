package com.example.eventlistapp.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.eventlistapp.data.remote.response.Event
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentFinishedCardBinding

class CarouselAdapter(private val onItemClick: (Event) -> Unit) : RecyclerView.Adapter<CarouselAdapter.EventViewHolder>() {

    private var eventList: List<Event> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Event>) {
        eventList = list
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = FragmentFinishedCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = eventList.size

    inner class EventViewHolder(private val binding: FragmentFinishedCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) {
            binding.tvTitleFinished.text = event.name
            binding.tvCategory.text = event.category
            binding.tvCity.text = event.cityName
            binding.tvItemDescription.text = event.summary

            // Load the image into the ImageView using Glide
            Glide.with(binding.ivCover.context)
                .load(event.imageLogo) // Load the image URL from the Event object
                .placeholder(R.mipmap.team_work) // Optional: Set a placeholder image
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache strategy
                .into(binding.ivCover) // Target ImageView

            // Set click listener for the item
            binding.root.setOnClickListener {
                onItemClick(event) // Pass event to onItemClick lambda
            }
        }
    }
}
