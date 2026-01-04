package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.EntryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.Handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Utils
import kotlin.uuid.Uuid

class VaultOverviewViewModel() : ViewModel() {
     fun getTOTP(secret: String): Long {
        return MFAHandler.getTotpCode(secret)
    }

    fun copy(text: String, context: Context) {
        Utils.copyToClipboard(context, text)
    }

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

