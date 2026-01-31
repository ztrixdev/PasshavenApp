package ru.ztrixdev.projects.passhavenapp.entryManagers

import ru.ztrixdev.projects.passhavenapp.room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Account
import ru.ztrixdev.projects.passhavenapp.room.dataServices.AccountService
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto
import kotlin.uuid.Uuid

object AccountManager {
    suspend fun createAccount(database: AppDatabase, account: Account, encryptionKey: ByteArray): Uuid {
        val encryptedAccount = AccountService.encrypt(account, encryptionKey)

        database.accountDao().insert(encryptedAccount)

        return account.uuid
    }

    suspend fun getAllAccounts(database: AppDatabase, encryptionKey: ByteArray): List<Account> {
        val accounts = database.accountDao().getALl()
        return accounts.map { AccountService.decrypt(it, encryptionKey) }
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
            SodiumCrypto.decrypt(editedAccount.username, encryptionKey)
        } catch (_: IllegalArgumentException) {
            isEditedAccountEncrypted = false
        }
        
        val accountToUpdate = if (!isEditedAccountEncrypted)
            AccountService.encrypt(editedAccount, encryptionKey)
        else
            editedAccount

        accountDao.update(accountToUpdate)
    }

    suspend fun deleteAccount(database: AppDatabase, uuid: Uuid) {
        // If no account in the database matches the provided Uuid, the function exits immediately
        val account = retrieveAccountByUuid(database, uuid) ?: return
        database.accountDao().delete(account)
    }
}