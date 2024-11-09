package com.example.eventlistapp.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentFavoriteBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter
    private lateinit var viewModel: FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the RecyclerView
        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())

        // Use the ViewModelFactory to create the ViewModel instance
        val factory = FavoriteViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(FavoriteViewModel::class.java)

        // Observe the favorites LiveData from the ViewModel
        viewModel.getAllFavoritesLiveData().observe(viewLifecycleOwner) { favorites ->
            favoriteAdapter = FavoriteAdapter(favorites) { favoriteId ->
                // Navigate to EventDetailFragment and pass the event ID
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToNavigationDetailEvent(favoriteId)
                findNavController().navigate(action)
                val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
                bottomNavView.visibility = View.GONE
            }
            binding.rvFavorite.adapter = favoriteAdapter
        }
    }
}


