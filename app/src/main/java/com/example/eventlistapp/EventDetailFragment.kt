package com.example.eventlistapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.eventlistapp.databinding.FragmentEventDetailBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class EventDetailFragment : Fragment() {

    private lateinit var binding: FragmentEventDetailBinding
    private val args: EventDetailFragmentArgs by navArgs()
    private val viewModel: DetailViewModel by viewModels() // Initialize the ViewModel

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
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.constraintLayout.visibility = View.GONE
            } else {
                binding.progressBar.visibility = View.GONE
                binding.constraintLayout.visibility = View.VISIBLE
            }
        }

        // Observe the event details
        viewModel.eventDetails.observe(viewLifecycleOwner) { event ->
            event?.let { bindEventData(it) }
        }

        // Observe error messages
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        // Fetch event details
        val eventId = args.eventId
        Log.d("EventDetailsFragment", "Event ID: $eventId")
        viewModel.fetchEventDetails(eventId)
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
            .placeholder(R.drawable.teamwork)
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
