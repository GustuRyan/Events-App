package com.example.eventlistapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.eventlistapp.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize the binding
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Set the image and text views
        binding.imageView.setImageResource(R.drawable.profile_picture)
        binding.Fullname.text = "Ida Bagus Putu Ryan Paramasatya Putra"
        binding.Email.text = "a014b4ky1904@bangkit.academy"

        // Return the root view of the binding
        return binding.root
    }
}
