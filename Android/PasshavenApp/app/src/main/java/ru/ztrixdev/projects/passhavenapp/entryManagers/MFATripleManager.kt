package ru.ztrixdev.projects.passhavenapp.entryManagers

import ru.ztrixdev.projects.passhavenapp.room.AppDatabase

object MFATripleManager {
    suspend fun getAllMFATriples(database: AppDatabase, encryptionKey: ByteArray): List<MFATriple> {
        val accounts = AccountManager.getAllAccounts(database, encryptionKey)
        val mfaTriples = mutableListOf<MFATriple>()

        for (account in accounts) {
            if (account.mfaSecret == null)
                continue
            else
                mfaTriples.add(MFATriple(account.uuid,account.name, account.mfaSecret!!))
        }

        return mfaTriples
    }
}