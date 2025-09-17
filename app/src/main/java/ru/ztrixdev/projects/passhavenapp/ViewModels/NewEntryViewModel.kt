package ru.ztrixdev.projects.passhavenapp.ViewModels;

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import kotlin.uuid.Uuid

class NewEntryViewModel: ViewModel() {


    var selectedFolderUuid by mutableStateOf<Uuid?>(null)
    var selectedEntryType by mutableStateOf(EntryTypes.Account)

    // the _ is crucial because otherwise it's gonna throw because of JVM signature error.
    // швабры держат потолок: https://pikabu.ru/story/chuzhoy_kod_5762938
    fun _setSelectedEntryType(type: EntryTypes) {
        selectedEntryType = type
    }

    fun setSelectedFolder(folder: Folder) {
        selectedFolderUuid = folder.uuid
    }

    fun getFolders(context: Context): List<Folder> {
        val db = DatabaseProvider.getDatabase(context = context)
        return db.folderDao().getALl()
    }

    fun getSelectedFolder(context: Context): Folder {
        val db = DatabaseProvider.getDatabase(context = context)
        return db.folderDao().getFolderByUuid(selectedFolderUuid as Uuid) as Folder
    }

    fun pushNewEntry(account: Account, context: Context): Boolean {

        return false
    }

    fun pushNewEntry(card: Card, context: Context): Boolean {

        return false
    }

}

