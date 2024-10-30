package com.example.eventlistapp.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.eventlistapp.data.remote.response.Event
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentEventDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class EventDetailFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailBinding
    private val args: EventDetailFragmentArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels() // Use DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe the loading state
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.constraintLayout.visibility = View.GONE
                binding.buttonFavorite.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.constraintLayout.visibility = View.VISIBLE
                binding.buttonFavorite.visibility = View.VISIBLE
            }
        }

        // Observe the event details
        viewModel.event.observe(viewLifecycleOwner) { event ->
            event?.let {
                bindEventData(it)
                updateFavoriteIcon(it.id ?: 0)
            }
        }

        // Fetch event details
        val eventId = args.eventId
        viewModel.fetchEventDetail(eventId)

        binding.buttonFavorite.setOnClickListener {
            viewModel.event.value?.let { event ->
                // Launch a coroutine to toggle favorite
                lifecycleScope.launch {
                    viewModel.toggleFavorite(event) // Pass the entire event

                    // Check if the event is now a favorite
                    val isFavorite = viewModel.isEventFavorite(event.id ?: 0)
                    if (isFavorite) {
                        Toast.makeText(requireContext(), "Event added to favorites", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Event removed from favorites", Toast.LENGTH_SHORT).show()
                    }

                    updateFavoriteIcon(event.id ?: 0)
                }
            }
        }
    }

    // Function to update the favorite button icon based on favorite status
    private fun updateFavoriteIcon(eventId: Int) {
        lifecycleScope.launch {
            val isFavorite = viewModel.isEventFavorite(eventId)
            if (isFavorite) {
                binding.buttonFavorite.setImageResource(R.drawable.baseline_favorite_24) // Event is a favorite
            } else {
                binding.buttonFavorite.setImageResource(R.drawable.baseline_favorite_border_24) // Event is not a favorite
            }
        }
    }

    private fun bindEventData(event: Event) {
        binding.tvTitle.text = event.name
        binding.tvCategory.text = event.category
        binding.tvOwner.text = event.ownerName
        binding.tvCity.text = event.cityName
        binding.tvItemDescription.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_COMPACT)
        binding.tvBeginTime.text = event.beginTime
        binding.tvEndTime.text = event.endTime

        // Ensure safe calls and calculations for available slots
        val registrants = event.registrants ?: 0 // Default to 0 if null
        val quota = event.quota ?: 0 // Default to 0 if null

        // Calculate available slots: quota - registrants
        val available = quota - registrants
        binding.tvAvailable.text = available.toString()

        binding.tvRegistrants.text = registrants.toString()
        binding.tvQuota.text = quota.toString()

        // Load event image into ImageView using Glide
        Glide.with(binding.ivCover.context)
            .load(event.mediaCover ?: "default_image_url")
            .placeholder(R.mipmap.team_work)
            .into(binding.ivCover)

        // Set button click listener to open the URL in a web browser
        binding.buttonReserve.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.link))
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show the bottom navigation when leaving this fragment
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavView.visibility = View.VISIBLE
    }
}
