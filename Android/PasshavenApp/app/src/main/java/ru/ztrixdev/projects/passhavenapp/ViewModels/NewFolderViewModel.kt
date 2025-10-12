package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.EntryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.SortingKeys
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import kotlin.uuid.Uuid

class NewFolderViewModel: ViewModel() {
    val folderCreated = mutableStateOf(false)

    private val _selectedSortingKey = mutableStateOf(SortingKeys.ByAlphabet)

    // Public, for the UI
    var entries = mutableStateListOf<Any>()

    // Private, for folder creation
    private val _includedEntriesUuid = emptyList<Uuid>().toMutableList()

    fun setSelectedSortingKey(key: SortingKeys) {
        _selectedSortingKey.value = key
    }

    val newFolderName = mutableStateOf(TextFieldValue(""))
    val reversedSorting = mutableStateOf(false)

    fun loadEntries(context: Context) {
        val db = DatabaseProvider.getDatabase(context)
        val key = VaultHandler().getEncryptionKey(context)

        entries.clear()
        entries.addAll(EntryManager.getAllEntriesForUI(db, key))
    }

    fun sortEntries() {
        val sortedEntries = EntryManager.sortEntries(entries.toList(), _selectedSortingKey.value)
        entries.clear()
        if (reversedSorting.value) {
            entries.addAll(sortedEntries.reversed())
        } else {
            entries.addAll(sortedEntries)
        }
    }

    fun includeEntry(entryUuid: Uuid) {
        _includedEntriesUuid.add(entryUuid)
    }

    fun removeEntry(entryUuid: Uuid) {
        _includedEntriesUuid.remove(entryUuid)
    }

    fun createFolder(context: Context) {
        val db = DatabaseProvider.getDatabase(context)

        val newFolder = Folder(
            uuid = Uuid.random(),
            name = newFolderName.value.text,
            entries = _includedEntriesUuid,
            dateCreated = System.currentTimeMillis()
        )

        FolderManager.createFolder(db, newFolder)
    }
}
