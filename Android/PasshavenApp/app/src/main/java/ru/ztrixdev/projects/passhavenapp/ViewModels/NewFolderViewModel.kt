package ru.ztrixdev.projects.passhavenapp.ViewModels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.EntryManagers.SortingKeys

class NewFolderViewModel: ViewModel() {

    val _selectedSortingKey = mutableStateOf(SortingKeys.ByAlphabet)

    fun setSelectedSortingKey(key: SortingKeys) {
        _selectedSortingKey.value = key
    }

    val newFolderName = mutableStateOf(TextFieldValue(""))



}
