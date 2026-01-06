package ru.ztrixdev.projects.passhavenapp.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.entryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.room.Account
import ru.ztrixdev.projects.passhavenapp.room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Utils
import kotlin.uuid.Uuid

class VaultOverviewViewModel() : ViewModel() {
     fun getTOTP(secret: String): Long {
        return MFAHandler.getTotpCode(secret)
    }
    fun copy(text: String, context: Context) {
        Utils.copyToClipboard(context, text)
    }

    enum class Views {
        Overview, MFA, Generator
    }

    var currentView by mutableStateOf<Views>(Views.Overview)

    suspend fun getUsernameByUuid(uuid: Uuid, context: Context): String {
        val entry = EntryManager.getEntryByUuid(
            database = DatabaseProvider.getDatabase(context = context),
            soughtUuid = uuid
        )
        return if (entry is Account)
            entry.username
        else ""
    }

}

