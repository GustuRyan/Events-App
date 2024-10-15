package com.example.eventlistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels // Use activityViewModels for shared ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventlistapp.databinding.FragmentEventListBinding
import com.example.eventlistapp.ui.home.HomeFragment
import com.example.eventlistapp.ui.home.HomeFragmentDirections
import com.example.eventlistapp.ui.upcoming.UpcomingFragment
import com.example.eventlistapp.ui.upcoming.UpcomingFragmentDirections
import com.example.eventlistapp.ui.finished.FinishedFragment
import com.example.eventlistapp.ui.finished.FinishedFragmentDirections
import com.example.eventlistapp.ui.upcoming.UpcomingViewModel // Import UpcomingViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class EventListFragment : Fragment() {
    private lateinit var binding: FragmentEventListBinding
    private lateinit var eventListAdapter: EventListAdapter

    // Shared UpcomingViewModel with UpcomingFragment
    private val upcomingViewModel: UpcomingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventListAdapter = EventListAdapter { event ->
            val action: NavDirections = when (parentFragment) {
                is HomeFragment -> {
                    val eventId = event.id ?: throw IllegalArgumentException("Event ID cannot be null")
                    HomeFragmentDirections.actionNavigationHomeToEventDetailFragment(eventId)
                }
                is UpcomingFragment -> {
                    val eventId = event.id ?: throw IllegalArgumentException("Event ID cannot be null")
                    UpcomingFragmentDirections.actionNavigationUpcomingToEventDetailFragment(eventId)
                }
                is FinishedFragment -> {
                    val eventId = event.id ?: throw IllegalArgumentException("Event ID cannot be null")
                    FinishedFragmentDirections.actionNavigationFinishedToEventDetailFragment(eventId)
                }
                else -> throw IllegalStateException("Unknown parent fragment")
            }

            navigateSafely(action)
        }

        binding.recyclerView.apply {
            adapter = eventListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        // Observe UpcomingViewModel data
        upcomingViewModel.eventList.observe(viewLifecycleOwner) { events ->
            eventListAdapter.submitList(events) // Update the adapter with new data
        }

        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE // Show or hide the progress bar
        }

        upcomingViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() // Show error message as a toast
            }
        }

        // Fetch events based on the parent fragment type
        fetchEvents()
    }

    private fun navigateSafely(action: NavDirections) {
        try {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
            navHostFragment?.navController?.navigate(action)
            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNavView.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchEvents() {
        // Trigger the event fetch process based on the shared UpcomingViewModel
        val parentFragmentName = when (parentFragment) {
            is HomeFragment -> "HomeFragment"
            is UpcomingFragment -> "UpcomingFragment"
            is FinishedFragment -> "FinishedFragment"
            else -> "UnknownFragment"
        }

        // Trigger the event fetch process based on the shared UpcomingViewModel
        upcomingViewModel.searchEvents("", parentFragmentName)
    }

    override fun onStop() {
        super.onStop()
        upcomingViewModel.clearEventList() // Clear the event list when the fragment stops
    }
}
