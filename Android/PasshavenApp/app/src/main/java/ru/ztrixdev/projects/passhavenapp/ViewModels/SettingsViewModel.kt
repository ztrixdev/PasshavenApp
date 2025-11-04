package ru.ztrixdev.projects.passhavenapp.ViewModels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {
    val openAppearance = mutableStateOf(false)
    val openSecurity = mutableStateOf(false)
    val openExports = mutableStateOf(false)
    val openImports = mutableStateOf(false)
    val openInfo = mutableStateOf(false)
}
