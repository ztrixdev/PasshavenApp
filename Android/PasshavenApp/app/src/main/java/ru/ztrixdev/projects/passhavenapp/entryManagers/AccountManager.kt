package ru.ztrixdev.projects.passhavenapp.entryManagers

import ru.ztrixdev.projects.passhavenapp.room.Account
import ru.ztrixdev.projects.passhavenapp.room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.room.decrypt
import ru.ztrixdev.projects.passhavenapp.room.encrypt
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto
import kotlin.uuid.Uuid

object AccountManager {
    suspend fun createAccount(database: AppDatabase, account: Account, encryptionKey: ByteArray): Uuid {
        account.encrypt(encryptionKey)

        database.accountDao().insert(account)

        return account.uuid
    }

    suspend fun getAllAccounts(database: AppDatabase, encryptionKey: ByteArray): List<Account> {
        val accounts = database.accountDao().getALl()
        accounts.forEach { it.decrypt(encryptionKey) }

        return accounts
    }

    suspend fun retrieveAccountByUuid(database: AppDatabase, uuid: Uuid): Account? {
        val account = database.accountDao().getAccountByUuid(accountUuid = uuid) ?: return null

        return account
    }

    suspend fun editAccount(database: AppDatabase, editedAccount: Account, encryptionKey: ByteArray) {
        val accountDao = database.accountDao()

        // If no account in the database matches the Uuid of the editedAccount, the function exits immediately
        retrieveAccountByUuid(database, editedAccount.uuid) ?: return

        var isEditedAccountEncrypted = true
        try {
            // If a field can be decrypted - the Account object is encrypted
            // (Account.encrypt() encrypts pretty much all the fields)
            SodiumCrypto.decrypt(editedAccount.username, encryptionKey)
        } catch (_: IllegalArgumentException) { // caught the wrong exception
            isEditedAccountEncrypted = false
        }
        if (!isEditedAccountEncrypted)
            editedAccount.encrypt(encryptionKey)

        accountDao.update(editedAccount)
    }

    suspend fun deleteAccount(database: AppDatabase, uuid: Uuid) {
        // If no account in the database matches the provided Uuid, the function exits immediately
        val account = retrieveAccountByUuid(database, uuid) ?: return
        database.accountDao().delete(account)
    }
}