package ru.ztrixdev.projects.passhavenapp.EntryManagers

import ru.ztrixdev.projects.passhavenapp.Room.AppDatabase
import kotlin.uuid.Uuid

object MFATripleManager {
    data class MFATriple (
        val originalUuid: Uuid,
        val name: String,
        val secret: String
    )

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