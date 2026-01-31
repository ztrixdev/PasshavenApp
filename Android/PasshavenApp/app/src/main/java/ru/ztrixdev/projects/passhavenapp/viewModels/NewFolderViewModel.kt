package ru.ztrixdev.projects.passhavenapp.viewModels

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.entryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.SortingKeys
import ru.ztrixdev.projects.passhavenapp.handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Folder
import kotlin.uuid.Uuid

class NewFolderViewModel: ViewModel() {
    val folderCreated = mutableStateOf(false)

    val _selectedSortingKey = mutableStateOf(SortingKeys.ByAlphabet)

    // Public, for the UI
    var entries = mutableStateListOf<Any>()

    // Private, for folder creation
    private val _includedEntryUuids =  mutableStateOf<Set<Uuid>>(emptySet())

    fun setSelectedSortingKey(key: SortingKeys) {
        _selectedSortingKey.value = key
    }

    val newFolderName = mutableStateOf(TextFieldValue(""))
    val reversedSorting = mutableStateOf(false)

    suspend fun loadEntries(context: Context) {
        val db = DatabaseProvider.getDatabase(context)
        val key = VaultHandler.getEncryptionKey(context)

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
        _includedEntryUuids.value += entryUuid
    }

    fun removeEntry(entryUuid: Uuid) {
        _includedEntryUuids.value -= entryUuid
    }

    fun getIncludedUuids(): Set<Uuid> {
        return _includedEntryUuids.value
    }

    suspend fun createFolder(context: Context) {
        val db = DatabaseProvider.getDatabase(context)

        val newFolder = Folder(
            uuid = Uuid.random(),
            name = newFolderName.value.text,
            entries = _includedEntryUuids.value.toList(),
            dateCreated = System.currentTimeMillis()
        )

        FolderManager.createFolder(db, newFolder)
        folderCreated.value = true
    }
    /*
    fun toggleEntryInclusion(entryUuid: Uuid) {
        val currentSet = _includedEntryUuids.value
        if (currentSet.contains(entryUuid)) {
            removeEntry(entryUuid)
        } else {
            includeEntry(entryUuid)
        }
    }
    */
    // no idea what is the code above doing but i'll save it bcs you never know
}
