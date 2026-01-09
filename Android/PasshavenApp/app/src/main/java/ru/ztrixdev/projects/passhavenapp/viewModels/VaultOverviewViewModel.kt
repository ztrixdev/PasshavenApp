package ru.ztrixdev.projects.passhavenapp.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.Utils
import ru.ztrixdev.projects.passhavenapp.entryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.MFATriple
import ru.ztrixdev.projects.passhavenapp.entryManagers.MFATripleManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.SortingKeys
import ru.ztrixdev.projects.passhavenapp.handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.room.Account
import ru.ztrixdev.projects.passhavenapp.room.Card
import ru.ztrixdev.projects.passhavenapp.room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.room.Folder
import ru.ztrixdev.projects.passhavenapp.room.decrypt
import kotlin.uuid.Uuid

class VaultOverviewViewModel() : ViewModel() {
     fun getTOTP(secret: String): Long {
        return MFAHandler.getTotpCode(secret)
    }
    fun copy(text: String, context: Context) {
        Utils.copyToClipboard(context, text)
    }

    private val _entries = mutableStateListOf<Any>()
    private val _cards = mutableStateListOf<Card>()
    private val _accounts = mutableStateListOf<Account>()
    private val _folders = mutableStateListOf<Folder>()

    fun getFolders(): List<Folder> {
        return _folders
    }

    val visibleEntries = mutableStateListOf<Any>()
    val visibleMFA = mutableStateListOf<MFATriple>()
    var selectedFolderUuid by mutableStateOf<Uuid?>(null)

    enum class Views {
        Overview, MFA, Generator
    }

    enum class ViewMode {
        Card, Row
    }

    var currentView by mutableStateOf<Views>(Views.Overview)
    var viewMode by mutableStateOf<ViewMode>(ViewMode.Card)
    private var _mfaList = mutableStateListOf<MFATriple>()


    private var _selectedSortingKey by mutableStateOf<SortingKeys>(SortingKeys.ByAlphabet)

    fun setSelectedSortingKey(key: SortingKeys) {
        _selectedSortingKey = key
    }

    fun getSelectedSortingKey(): SortingKeys {
        return _selectedSortingKey
    }

    var reverseSorting by mutableStateOf(false)

    fun toggleReverseSorting() {
        reverseSorting = !reverseSorting
    }

    suspend fun getUsernameByUuid(uuid: Uuid, context: Context): String {
        val key = VaultHandler.getEncryptionKey(context)
        val entry = EntryManager.getEntryByUuid(
            database = DatabaseProvider.getDatabase(context = context),
            soughtUuid = uuid
        )
        if (entry is Account) {
            entry.decrypt(key)
            return entry.username
        }
        else return ""
    }

    fun clearEntriesAndFolders() {
        _mfaList.clear()
        _entries.clear()
        _accounts.clear()
        _cards.clear()
        _folders.clear()
    }

    fun getCategorizedItemsFromEntryList() {
        for (entry in _entries) {
            when (entry) {
                is Card -> _cards.add(entry)
                is Account -> _accounts.add(entry)
            }
        }
    }

    suspend fun fetchEntries(context: Context) {
        clearEntriesAndFolders()

        val database = DatabaseProvider.getDatabase(context)
        val key = VaultHandler.getEncryptionKey(context)

        _entries.addAll(EntryManager.getAllEntriesForUI(database, key))
        _mfaList.addAll(MFATripleManager.getAllMFATriples(database, key))
        getCategorizedItemsFromEntryList()

        _folders.addAll(FolderManager.getFolders(database))
    }

    fun showFolderContents(folder: Folder) {
        val tempEntries = mutableListOf<Any>()
        val tempMFA = mutableListOf<MFATriple>()
        for (uuid in folder.entries) {
            _cards.forEach { if (it.uuid == uuid) tempEntries.add(it) }
            _accounts.forEach { if (it.uuid == uuid) tempEntries.add(it) }
            _mfaList.forEach { if (it.originalUuid == uuid) tempMFA.add(it) }
        }

        visibleEntries.clear()
        visibleEntries.addAll(tempEntries)
        visibleMFA.clear()
        visibleMFA.addAll(tempMFA)

        sortVisibles()
    }

    fun sortVisibles() {
        val tempEntries = mutableListOf<Any>()
        tempEntries.addAll(visibleEntries)

        val tempMFA = mutableListOf<MFATriple>()
        tempMFA.addAll(visibleMFA)

        visibleEntries.clear()
        var sortedList = EntryManager.sortEntries(tempEntries, _selectedSortingKey).toList()
        if (reverseSorting)
            sortedList = sortedList.reversed()
        visibleEntries.addAll(sortedList)

        visibleMFA.clear()
        sortedList = MFATripleManager.sortMFATriples(tempMFA,  reverseSorting).toList()
        visibleMFA.addAll(sortedList)
    }

    fun showAll() {
        visibleEntries.clear()
        visibleEntries.addAll(_entries)
        visibleMFA.clear()
        visibleMFA.addAll(_mfaList)
        sortVisibles()
    }
}



