package ru.ztrixdev.projects.passhavenapp.EntryManagers

import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.Room.decrypt
import ru.ztrixdev.projects.passhavenapp.Room.encrypt
import kotlin.uuid.Uuid

object AccountManager {
    suspend fun createAccount(database: AppDatabase, account: Account, encryptionKey: ByteArray): Uuid {
        account.uuid = Uuid.random()
        account.encrypt(encryptionKey)

        database.accountDao().insert(account)

        return account.uuid
    }

    suspend fun getAllAccounts(database: AppDatabase, encryptionKey: ByteArray): List<Account> {
        val accounts = database.accountDao().getALl()
        accounts.forEach { it.decrypt(encryptionKey) }

        return accounts
    }

    suspend fun retrieveAccountByUuid(database: AppDatabase, uuid: Uuid, encryptionKey: ByteArray): Account? {
        val account = database.accountDao().getAccountByUuid(accountUuid = uuid)
        if (account == null)
            return null
        account.decrypt(encryptionKey)

        return account
    }
}