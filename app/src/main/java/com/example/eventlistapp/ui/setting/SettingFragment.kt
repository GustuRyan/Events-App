package com.example.eventlistapp.ui.setting

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.eventlistapp.R
import com.example.eventlistapp.databinding.FragmentSettingBinding
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val settingViewModel: SettingViewModel by viewModels { SettingViewModelFactory(SettingPreferences.getInstance(requireContext().dataStore)) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        // Observe theme settings from ViewModel
        settingViewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkModeActive) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            binding.switchDarkmode.isChecked = isDarkModeActive
        }

        binding.switchDarkmode.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveThemeSetting(isChecked)
        }

        // Set user profile information
        binding.imageView.setImageResource(R.mipmap.profile_picture_round)
        binding.Fullname.text = getString(R.string.fullname)
        binding.Email.text = getString(R.string.email)

        // Set up daily reminder switch
        binding.switchDailyReminder.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingViewModel.saveReminderSetting(isChecked)
            if (isChecked) {
                startDailyReminder() // Start WorkManager when enabled
            } else {
                // Cancel the existing work if reminders are disabled
                WorkManager.getInstance(requireContext()).cancelUniqueWork("DailyReminderWorker")
            }
        }

        // Observe the saved reminder setting
        settingViewModel.getReminderSetting().observe(viewLifecycleOwner) { isReminderActive ->
            binding.switchDailyReminder.isChecked = isReminderActive
            if (isReminderActive) startDailyReminder() // Ensure worker is started if it's active
        }

        return binding.root
    }

    private fun startDailyReminder() {
        // Set up the periodic work request to run every 24 hours
        val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(10, TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "DailyReminderWorker",
            ExistingPeriodicWorkPolicy.UPDATE,
            dailyWorkRequest
        )
    }
}