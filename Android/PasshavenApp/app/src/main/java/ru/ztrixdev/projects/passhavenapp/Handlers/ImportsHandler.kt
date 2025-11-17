package ru.ztrixdev.projects.passhavenapp.Handlers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.ztrixdev.projects.passhavenapp.EntryManagers.AccountManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.CardManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.Room.AppDatabase

object ImportsHandler {
    fun getImport(template: ExportTemplates, import: String): List<ExportsHandler.ExportWrapper> {
        return when (template) {
            ExportTemplates.Passhaven -> {
                _processPH(import)
            }

            else -> {
                listOf()
            }
        }
    }

    private fun _processPH(import: String): List<ExportsHandler.ExportWrapper> {
        val type = object : TypeToken<List<ExportsHandler.ExportWrapper>>() {}.type
        return Gson().fromJson(import, type)
    }

    suspend fun apply(entries: List<ExportsHandler.ExportWrapper>, db: AppDatabase, encryptionKey: ByteArray) {
        for (category in entries) {
            if (category.Folder != null) {
                for (folder in category.Folder) {
                    FolderManager.createFolder(db, folder)
                }
            }
            if (category.Account != null) {
                for (account in category.Account) {
                    AccountManager.createAccount(db, account, encryptionKey)
                }
            }
            if (category.Card != null) {
                for (card in category.Card) {
                    CardManager.createCard(db, card, encryptionKey)
                }
            }
        }
    }
}

