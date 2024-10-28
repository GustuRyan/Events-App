package com.example.eventlistapp.ui.upcoming

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.eventlistapp.ui.event_list.EventListFragment
import com.example.eventlistapp.databinding.FragmentUpcomingBinding
import com.mancj.materialsearchbar.MaterialSearchBar

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val upcomingViewModel: UpcomingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load the EventListFragment
        if (savedInstanceState == null) {
            val eventListFragment = EventListFragment()
            childFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, eventListFragment)
                .commit()
        }

        binding.searchBar.setOnSearchActionListener(object : MaterialSearchBar.OnSearchActionListener {
            override fun onSearchStateChanged(enabled: Boolean) {
                // Handle search state change if necessary
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                text?.let { searchText ->
                    upcomingViewModel.searchEvents(searchText.toString())
                }
            }

            override fun onButtonClicked(buttonCode: Int) {
                // Handle button clicks if necessary (e.g., speech button)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        upcomingViewModel.clearEventList() // Clear the event list when the fragment stops
    }
}
