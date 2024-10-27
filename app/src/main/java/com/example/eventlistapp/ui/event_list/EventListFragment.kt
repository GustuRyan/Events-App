package com.example.eventlistapp.ui.event_list

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
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentEventListBinding
import com.example.eventlistapp.ui.home.HomeFragment
import com.example.eventlistapp.ui.home.HomeFragmentDirections
import com.example.eventlistapp.ui.upcoming.UpcomingFragment
import com.example.eventlistapp.ui.upcoming.UpcomingFragmentDirections
import com.example.eventlistapp.ui.finished.FinishedFragment
import com.example.eventlistapp.ui.finished.FinishedFragmentDirections
import com.example.eventlistapp.ui.upcoming.UpcomingViewModel // Import UpcomingViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class EventListFragment : Fragment() {
    private lateinit var binding: FragmentEventListBinding
    private lateinit var eventListAdapter: EventListAdapter

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

        // Observe ViewModel for events, loading state, and error messages
        upcomingViewModel.getEvents().observe(viewLifecycleOwner) { events ->
            eventListAdapter.submitList(events)
        }

        upcomingViewModel.getIsLoading().observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }

        upcomingViewModel.getErrorMessage().observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Launch fetchUpcomingEvents in a coroutine scope
        viewLifecycleOwner.lifecycleScope.launch {
            fetchUpcomingEvents()
        }
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

    private fun fetchUpcomingEvents() {
        val fragmentType = when (parentFragment) {
            is HomeFragment -> 0
            is UpcomingFragment -> 1
            else -> -1
        }

        if (fragmentType != -1) {
            upcomingViewModel.fetchUpcomingEvents(forceReload = false, fragmentType)
        }
    }

    override fun onStop() {
        super.onStop()
        upcomingViewModel.clearEventList()
    }
}
