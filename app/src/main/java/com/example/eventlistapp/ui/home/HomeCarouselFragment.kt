package com.example.eventlistapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventlistapp.CarouselAdapter
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentHomeCarouselBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeCarouselFragment : Fragment() {
    private var _binding: FragmentHomeCarouselBinding? = null
    private val binding get() = _binding!!

    private lateinit var carouselAdapter: CarouselAdapter
    private val viewModel: HomeViewModel by viewModels() // Get the ViewModel instance

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Initialize the binding
        _binding = FragmentHomeCarouselBinding.inflate(inflater, container, false)
        return binding.root // Return the root view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the adapter and handle item clicks
        carouselAdapter = CarouselAdapter { event ->
            // Handle item click here
            val action: NavDirections = HomeFragmentDirections.actionNavigationHomeToEventDetailFragment(event.id!!)
            navigateSafely(action)
        }

        // Set up the RecyclerView with a horizontal LinearLayoutManager
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = carouselAdapter
        }

        // Observe LiveData from the ViewModel
        viewModel.eventList.observe(viewLifecycleOwner) { events ->
            carouselAdapter.submitList(events)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // Fetch events from the ViewModel
        viewModel.fetchEvents()
    }

    private fun navigateSafely(action: NavDirections) {
        val navHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment_activity_main) as? NavHostFragment
        navHostFragment?.navController?.navigate(action)
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavView.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        // Clear binding when the view is destroyed to prevent memory leaks
        _binding = null
    }
}
