package com.example.eventlistapp.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val pref: SettingPreferences) : ViewModel() {
    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    // Fetch the reminder setting (true if enabled)
    fun getReminderSetting(): LiveData<Boolean> = pref.getReminderSetting().asLiveData()

    // Save the reminder setting
    fun saveReminderSetting(isReminderActive: Boolean) {
        viewModelScope.launch { pref.saveReminderSetting(isReminderActive) }
    }
}