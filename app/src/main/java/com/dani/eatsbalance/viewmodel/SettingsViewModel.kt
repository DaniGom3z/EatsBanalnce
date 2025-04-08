package com.dani.eatsbalance.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var notificationsEnabled by mutableStateOf(true)
    var darkModeEnabled by mutableStateOf(false)

    fun toggleNotifications() {
        notificationsEnabled = !notificationsEnabled
    }

    fun toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled
    }
}
