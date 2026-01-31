package ru.ztrixdev.projects.passhavenapp.entryManagers

import ru.ztrixdev.projects.passhavenapp.room.dataModels.Account
import ru.ztrixdev.projects.passhavenapp.room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Card
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Folder
import kotlin.uuid.Uuid

object EntryManager {
    // This function will look through all Room tables the app has to get an entry by it's Uuid.
    // At the moment it will ask Room around if there is an account, a card or a folder with the Uuid provided and return the sought object on detection.
    // If found none, it will return null.
    suspend fun getEntryByUuid(database: AppDatabase, soughtUuid: Uuid): Any? {
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

    suspend fun deleteEntry(database: AppDatabase, entry: Any) {
        when (entry) {
            is Card -> database.cardDao().delete(entry)
            is Account -> database.accountDao().delete(entry)
            is Folder -> database.folderDao().delete(entry)
        }
    }

    suspend fun deleteEntryByUuid(database: AppDatabase, entryUuid: Uuid) {
        when (val entry = getEntryByUuid(database, entryUuid)) {
            is Card -> database.cardDao().delete(entry)
            is Account -> database.accountDao().delete(entry)
            is Folder -> database.folderDao().delete(entry)
        }
    }


    suspend fun getAllEntriesForUI(database: AppDatabase, encryptionKey: ByteArray): List<Any> {
        val allEntries = emptyList<Any>().toMutableList()
        allEntries.addAll(AccountManager.getAllAccounts(database, encryptionKey))
        allEntries.addAll(CardManager.getAllCards(database, encryptionKey))

        val desecretified = emptyList<Any>().toMutableList()
        allEntries.forEach { desecretified.add(desecretify(it)) }

        return desecretified
    }

    suspend fun getAllEntriesForExport(database: AppDatabase, encryptionKey: ByteArray): List<Any> {
        val allEntries = emptyList<Any>().toMutableList()
        allEntries.addAll(AccountManager.getAllAccounts(database, encryptionKey))
        allEntries.addAll(CardManager.getAllCards(database, encryptionKey))

        return allEntries
    }

    fun desecretify(entry: Any): Any {
        when (entry) {
            is Account -> {
                return Account(
                    uuid = entry.uuid,
                    reprompt = entry.reprompt,
                    name = entry.name,
                    username = entry.username,
                    password = entry.password.substring(0,2) + "*****",
                    mfaSecret = "",
                    recoveryCodes = emptyList(),
                    additionalNote = "",
                    dateCreated = entry.dateCreated
                )
            }
            is Card -> {
                return Card(
                    uuid = entry.uuid,
                    reprompt = entry.reprompt,
                    name = entry.name,
                    number = "***" + entry.number.takeLast(4),
                    expirationDate = "**/" + entry.expirationDate.takeLast(2),
                    cvcCvv = "***",
                    brand = entry.brand,
                    cardholder = entry.cardholder,
                    additionalNote = "",
                    dateCreated = entry.dateCreated
                )
            }
        }
        return "ERR_UNRECOGNIZABLE_TYPE"
    }

    fun sortEntries(entries: List<Any>, sortingKey: SortingKeys): MutableList<Any> {
        var sortedList: MutableList<Any> = emptyList<Any>().toMutableList()
        val entriesFiltered = entries.filter { it is Account || it is Card }
        when (sortingKey) {
            SortingKeys.ByAlphabet -> {
                val entriesNames = entriesFiltered
                    .map {
                        when (it) {
                            is Account -> it.name
                            is Card -> it.name
                            else -> ""  // ts won't get called bcs of the filter above, but just in case.
                        }
                    }
                val sortedNamesList = entriesNames.sorted()
                sortedNamesList.forEach { name ->
                    entriesFiltered.forEach { entry ->
                        if ((entry is Account && entry.name == name) || (entry is Card && entry.name == name))
                            sortedList.add(entry)
                    }
                }
                return sortedList
            }
            SortingKeys.ByType -> {
                // Entries are sorted by type in an order that developers will. If you want, you can freely change the order here, xd.
                // 1. Accounts.
                // 2. Cards.
                // 3. The Greatest Type That's Ever Lived. (!!! some newer types that will come after come here !!!)
                val accounts = entries.filter { it is Account }
                val cards = entries.filter { it is Card }

                sortedList = emptyList<Any>().toMutableList()
                sortedList.addAll(accounts)
                sortedList.addAll(cards)
                return sortedList
            }
            SortingKeys.ByDate -> {
                val entriesDates = entriesFiltered
                    .map {
                        when (it) {
                            is Account -> it.dateCreated
                            is Card -> it.dateCreated
                            else -> 0
                        }
                    }
                val sortedDatesList = entriesDates.sorted()
                sortedDatesList.forEach { date ->
                    entriesFiltered.forEach { entry ->
                        if ((entry is Account && entry.dateCreated == date) || (entry is Card && entry.dateCreated == date))
                            sortedList.add(entry)
                    }
                }
                return sortedList
            }
        }
    }

    fun getUuids(entries: List<Any>): List<Uuid> {
        val uuids: MutableList<Uuid> = mutableListOf()
        entries.forEach {
            if (it is Card) uuids.add(it.uuid)
            if (it is Account) uuids.add(it.uuid)
        }
        return uuids.toList()
    }
}
