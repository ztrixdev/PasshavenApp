package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder

class VaultOverviewViewModel (context: Context, private val vaultHandler: VaultHandler = VaultHandler()) : ViewModel() {
    private val database = DatabaseProvider.getDatabase(context = context)

    fun getFolders(): List<Folder> {
        return database.folderDao().getALl()
    }

}

