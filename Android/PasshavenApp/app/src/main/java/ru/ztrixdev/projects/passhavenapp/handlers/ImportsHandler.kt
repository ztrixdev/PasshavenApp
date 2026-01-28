package ru.ztrixdev.projects.passhavenapp.handlers

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.ztrixdev.projects.passhavenapp.entryManagers.AccountManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.CardManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.room.AppDatabase

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
        try {
            val type = object : TypeToken<List<ExportsHandler.ExportWrapper>>() {}.type
            return Gson().fromJson(import, type)
        } catch (ex: NullPointerException) {
            return emptyList()
        }
    }


    suspend fun __apply__(entries: List<ExportsHandler.ExportWrapper>, db: AppDatabase, encryptionKey: ByteArray) {
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

