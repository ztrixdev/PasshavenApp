package ru.ztrixdev.projects.passhavenapp.EntryManagers

import ru.ztrixdev.projects.passhavenapp.Room.AppDatabase
import kotlin.uuid.Uuid

object EntryManager {
    // This function will look through all Room tables the app has to get an entry by it's Uuid.
    // At the moment it will ask Room around if there is an account, a card or a folder with the Uuid provided and return the sought object on detection.
    // If found none, it will return null.
    fun getEntryByUuid(database: AppDatabase, soughtUuid: Uuid): Any? {
        val folderDao = database.folderDao()
        val folderSearchResult = folderDao.getFolderByUuid(soughtUuid)
        if (folderSearchResult != null)
            return folderSearchResult

        val accountDao = database.accountDao()
        val accountSearchResult = accountDao.getAccountByUuid(soughtUuid)
        if (accountSearchResult != null)
            return accountSearchResult

        val cardDao = database.cardDao()
        val cardSearchResult = cardDao.getCardByUuid(soughtUuid)
        if (cardSearchResult != null)
            return cardSearchResult

        return null
    }
}
