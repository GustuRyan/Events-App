package com.example.eventlistapp.ui.favorite

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventlistapp.data.local.entity.Favorite
import com.example.eventlistapp.databinding.FragmentFavoriteCardBinding

class FavoriteAdapter(
    private val favoriteList: List<Favorite>,
    private val onFavoriteClick: (Int) -> Unit // Add a callback to handle clicks
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    inner class FavoriteViewHolder(val binding: FragmentFavoriteCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(favorite: Favorite) {
            binding.tvTitleFavorite.text = favorite.title
            binding.tvCategory.text = favorite.category
            binding.tvCity.text = favorite.cityName
            binding.tvItemDescription.text = favorite.description
            // Load image using Glide or other library if necessary
            Glide.with(binding.ivCover.context)
                .load(favorite.mediaCover)
                .into(binding.ivCover)

            // Set click listener
            binding.root.setOnClickListener {
                onFavoriteClick(favorite.id) // Call the callback with the event ID
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val binding = FragmentFavoriteCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(favoriteList[position])
    }

    override fun getItemCount(): Int = favoriteList.size
}


