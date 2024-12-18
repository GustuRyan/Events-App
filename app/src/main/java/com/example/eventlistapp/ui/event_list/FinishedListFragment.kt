package com.example.eventlistapp.ui.event_list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentFinishedListBinding
import com.example.eventlistapp.ui.home.HomeFragment
import com.example.eventlistapp.ui.home.HomeFragmentDirections
import com.example.eventlistapp.ui.upcoming.UpcomingFragment
import com.example.eventlistapp.ui.upcoming.UpcomingFragmentDirections
import com.example.eventlistapp.ui.finished.FinishedFragment
import com.example.eventlistapp.ui.finished.FinishedFragmentDirections
import com.example.eventlistapp.ui.finished.FinishedViewModel
import com.example.eventlistapp.ui.home.CarouselAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mancj.materialsearchbar.MaterialSearchBar
import kotlinx.coroutines.launch

class FinishedListFragment : Fragment() {
    private lateinit var binding: FragmentFinishedListBinding
    private lateinit var carouselAdapter: CarouselAdapter
    private val viewModel: FinishedViewModel by viewModels() // Use FinishedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFinishedListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize CarouselAdapter
        carouselAdapter = CarouselAdapter { event ->
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

        // Set up the RecyclerView for CarouselAdapter
        binding.recyclerView.apply {
            adapter = carouselAdapter
            layoutManager = GridLayoutManager(context, 2) // Using a grid layout manager
        }

        // Set up MaterialSearchBar
        binding.searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                // Handle search state change if necessary
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                // Perform search when the user confirms the input
                text?.let { searchText ->
                    lifecycleScope.launch {
                        searchFinishedEvents(searchText.toString())
                    }
                }
            }

            override fun onButtonClicked(buttonCode: Int) {
                // Handle button click
            }
        })

        // Observe ViewModel data for events
        viewModel.getEvents().observe(viewLifecycleOwner) { events ->
            // Update adapter with new event list
            carouselAdapter.submitList(events)
            carouselAdapter.notifyDataSetChanged()
        }

        // Observe error messages from ViewModel
        viewModel.getErrorMessage().observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe loading state to handle progress bar visibility
        viewModel.getIsLoading().observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Fetch the finished events initially when the fragment is loaded
        lifecycleScope.launch {
            fetchFinishedEvents()
        }
    }

    private fun navigateSafely(action: NavDirections) {
        try {
            val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
            navHostFragment?.navController?.navigate(action)
            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
            bottomNavView.visibility = View.GONE
        } catch (e: Exception) {
            // Log the error or show a message to the user
            e.printStackTrace()
        }
    }

    // Fetch finished events from the ViewModel
    private fun fetchFinishedEvents() {
        viewModel.fetchFinishedEvents()
    }

    // Search finished events based on user input
    private fun searchFinishedEvents(searchText: String) {
        viewModel.searchEvents(searchText)
    }
}