package ru.ztrixdev.projects.passhavenapp.handlers

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import ru.ztrixdev.projects.passhavenapp.TimeInMillis
import ru.ztrixdev.projects.passhavenapp.entryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Checksum
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.AndroidCrypto
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.CryptoNames
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.Keygen
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.keystoreInstanceName
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.mpHashProtectingKeyName
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.mpProtectingKeyName
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.pinHashProtectingKeyName
import ru.ztrixdev.projects.passhavenapp.preferences.VaultPrefs
import ru.ztrixdev.projects.passhavenapp.room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.room.Vault
import ru.ztrixdev.projects.passhavenapp.room.dao.VaultDao
import java.security.Key
import java.security.KeyStore
import javax.crypto.SecretKey
import kotlin.uuid.Uuid

object VaultHandler {
    suspend fun createVault(password: String, PIN: String, context: Context): List<Vault> {
        if (!MasterPassword.verify(passwd = password))
            throw IllegalArgumentException("The provided password doesn't meet the requirements. Check the requirements and try again.")
        if (!MasterPassword.verifyPIN(PIN))
            throw IllegalArgumentException("The provided PIN doesn't meet the requirements. Check the requirements and try again.")

        // Generates a new key in KeyStore to protect the MP-derived key.
        Keygen.generateAndroidKey(mpProtectingKeyName)
        Keygen.generateAndroidKey(mpHashProtectingKeyName)
        Keygen.generateAndroidKey(pinHashProtectingKeyName)

        val keystore: KeyStore = KeyStore.getInstance(keystoreInstanceName).apply { load(null) }
        val mpProtectingKey: Key? = keystore.getKey(mpProtectingKeyName, null)
        val mpHashProtectingKey: Key? = keystore.getKey(mpHashProtectingKeyName, null)
        val pinHashProtectingKey: Key? = keystore.getKey(pinHashProtectingKeyName, null)
        if (mpProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpProtectingKeyName key! A $mpProtectingKeyName key might not have been generated...")
        if (mpHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpHashProtectingKeyName key! A $mpHashProtectingKeyName key might not have been generated...")
        if (pinHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might nott have been generated...")

        val mpDerivedKeySaltPair = Keygen.deriveKeySaltPairFromMP(password)
        val key = mpDerivedKeySaltPair[CryptoNames.key]
        val salt = mpDerivedKeySaltPair[CryptoNames.salt]
        val encryptedKeyCipherAndIV= AndroidCrypto.encrypt(key, mpProtectingKey as SecretKey)

        val mpHash = Checksum.keccak512(password)
        val pinHash = Checksum.keccak512(PIN)

        val encryptedMPHashCipherAndIV = AndroidCrypto.encrypt(mpHash, mpHashProtectingKey as SecretKey)
        val encryptedPINHashCipherAndIV = AndroidCrypto.encrypt(pinHash, pinHashProtectingKey as SecretKey)

        val vault = Vault(uuid = Uuid.random(),
            mpKey = encryptedKeyCipherAndIV[CryptoNames.cipher]!!,
            mpSalt = salt!!, mpIv = encryptedKeyCipherAndIV[CryptoNames.iv]!!,
            mpHash = encryptedMPHashCipherAndIV[CryptoNames.cipher]!!,
            mpHashIv = encryptedMPHashCipherAndIV[CryptoNames.iv]!!,
            pinHash = encryptedPINHashCipherAndIV[CryptoNames.cipher]!!,
            pinHashIv = encryptedPINHashCipherAndIV[CryptoNames.iv]!!
        )

        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        // Deletes all the previous vaults to avoid having more than one vault, which will result in errors.
        if (!selfDestroy(vaultDao))
            throw RuntimeException("Cannot self-destroy (delete previous vaults). Maybe, an error occurred.")

        vaultDao.insert(vault)
        return vaultDao.getVault()
    }

    suspend fun loginByPassword(passwd: String, context: Context): Boolean {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()
        if (vault.isEmpty())
            return false

        if (VaultPrefs.getFlabsr(context) <= 0)
            if (selfDestroy(vaultDao)) return false

        val keystore: KeyStore = KeyStore.getInstance(keystoreInstanceName).apply { load(null) }
        val mpHashProtectingKey: Key = keystore.getKey(mpHashProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $mpHashProtectingKeyName key! A $mpHashProtectingKeyName key might not have been generated...")

        val decryptedMPHash = AndroidCrypto.decrypt(mapOf(CryptoNames.cipher to vault[0].mpHash, CryptoNames.iv to vault[0].mpHashIv), mpHashProtectingKey as SecretKey)

        val loginResult = Checksum.keccak512(passwd).contentEquals(decryptedMPHash)
        if (!loginResult) {
            val remainingAttempts = VaultPrefs.getFlabsr(context) - 1
            VaultPrefs.saveFlabsr(context, remainingAttempts)
        } else {
            VaultPrefs.saveFlabsr(context, VaultPrefs.getFlabs(context))
        }

        return loginResult
    }


    suspend fun loginByPIN(PIN: String, context: Context): Boolean {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()

        if (vault.isEmpty())
            return false

        val remainingAttempts = VaultPrefs.getFlabsr(context)
        if (remainingAttempts <= 0)
            if (selfDestroy(vaultDao)) return false

        val keystore: KeyStore = KeyStore.getInstance(keystoreInstanceName).apply { load(null) }
        val pinHashProtectingKey: Key = keystore.getKey(pinHashProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might not have been generated...")

        val decryptedPINHash = AndroidCrypto.decrypt(mapOf(CryptoNames.cipher to vault[0].pinHash, CryptoNames.iv to vault[0].pinHashIv), pinHashProtectingKey as SecretKey)
        val loginResult = Checksum.keccak512(PIN).contentEquals(decryptedPINHash)
        if (!loginResult) {
            VaultPrefs.saveFlabsr(context, remainingAttempts - 1)
        } else {
            VaultPrefs.saveFlabsr(context, VaultPrefs.getFlabs(context))
        }
        return loginResult
    }

    suspend fun changePIN(newPIN: String, context: Context): Boolean {
        // If the new PIN doesn't match the requirements, the function exits immediately without changing anything
        if (!MasterPassword.verifyPIN(newPIN))
            return false

        val keystore: KeyStore = KeyStore.getInstance(keystoreInstanceName).apply { load(null) }
        // Fixes an android.security.KeyStoreException
        keystore.deleteEntry(pinHashProtectingKeyName)
        // Generate a new key to protect the PIN's hash.
        Keygen.generateAndroidKey(pinHashProtectingKeyName)
        val pinHashProtectingKey: Key = keystore.getKey(pinHashProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might not have been generated...")

        val encryptedPIN = AndroidCrypto.encrypt(Checksum.keccak512(newPIN), pinHashProtectingKey as SecretKey)
        // Won't proceed if the encryption failed.
        if (encryptedPIN[CryptoNames.cipher] == null || encryptedPIN[CryptoNames.iv] == null)
            return false

        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        var vault = vaultDao.getVault()[0]
        // saving the old PIN for a fail safe.
        val oldPin = listOf(vault.pinHash, vault.pinHashIv)

        vault.pinHash = encryptedPIN[CryptoNames.cipher] as ByteArray
        vault.pinHashIv = encryptedPIN[CryptoNames.iv] as ByteArray

        vaultDao.update(vault)
        // the fail safe:
        if (!loginByPIN(newPIN, context)) {
            vault = vaultDao.getVault()[0]
            vault.pinHash = oldPin[0]
            vault.pinHashIv = oldPin[1]
            vaultDao.update(vault)

            return false
        }

        return !false        // you can't deny this looks badass.
    }

    suspend fun changeMP(newMP: String, context: Context): List<Vault> {
        // Alright, so this will be done in steps, come on, you know the drill)

        val db = DatabaseProvider.getDatabase(context)
        val currentKey = getEncryptionKey(context)
        val keystore: KeyStore = KeyStore.getInstance(keystoreInstanceName).apply { load(null) }

        // Step 0: exit immediately if a backup wasn't done recently
        val NO_BACKUP_EXCEPTION = Exception("A backup hasn't been done in the last 8 hours, cannot continue.")
        if (VaultPrefs.getBackupFolder(context) == "".toUri())
            throw NO_BACKUP_EXCEPTION
        if ((System.currentTimeMillis() - VaultPrefs.getLastBackupTimestamp(context)) > TimeInMillis.EightHours)
            throw NO_BACKUP_EXCEPTION

        // Step 1: get a full vault export
        val entries = EntryManager.getAllEntriesForExport(database = db, encryptionKey = currentKey)
        val folders = FolderManager.getFolders(context = context)
        val export = ExportsHandler.getExport(template = ExportTemplates.Passhaven, entries = entries, folders = folders)

        // Step 1.5: get the keys
        val mpProtectingKey: Key? = keystore.getKey(mpProtectingKeyName, null)
        val mpHashProtectingKey: Key? = keystore.getKey(mpHashProtectingKeyName, null)
        val pinHashProtectingKey: Key? = keystore.getKey(pinHashProtectingKeyName, null)
        if (mpProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpProtectingKeyName key! A $mpProtectingKeyName key might not have been generated...")
        if (mpHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $mpHashProtectingKeyName key! A $mpHashProtectingKeyName key might not have been generated...")
        if (pinHashProtectingKey == null)
            throw RuntimeException("Cannot retrieve the $pinHashProtectingKeyName key! A $pinHashProtectingKeyName key might nott have been generated...")

        // Step 2: get the PIN hash
        val vault = db.vaultDao().getVault()[0]
        val pinHash = AndroidCrypto.decrypt(encryptionResults = mapOf(
            CryptoNames.cipher to vault.pinHash,
            CryptoNames.iv to vault.pinHashIv
        ), pinHashProtectingKey as SecretKey)

        // Step 3: purge the vault
        for (entry in entries) {
            EntryManager.deleteEntry(database = db, entry = entry)
        } ; for (folder in folders) {
            EntryManager.deleteEntry(database = db, entry = folder)
        }
        selfDestroy(db.vaultDao())

        // Step 4: derive a key salt pair from an MP, encrypt credentials
        val mpDerivedKeySaltPair = Keygen.deriveKeySaltPairFromMP(newMP)

        val key = mpDerivedKeySaltPair[CryptoNames.key]
        val salt = mpDerivedKeySaltPair[CryptoNames.salt]
        val encryptedKeyCipherAndIV = AndroidCrypto.encrypt(key, mpProtectingKey as SecretKey)

        val mpHash = Checksum.keccak512(newMP)
        val encryptedMPHashCipherAndIV = AndroidCrypto.encrypt(mpHash, mpHashProtectingKey as SecretKey)
        val encryptedPINHashCipherAndIV = AndroidCrypto.encrypt(pinHash, pinHashProtectingKey)

        // Step 5: get a new vault!!
        val newVault = Vault(
            uuid = Uuid.random(),
            mpKey = encryptedKeyCipherAndIV[CryptoNames.cipher]!!,
            mpSalt = salt!!,
            mpIv = encryptedKeyCipherAndIV[CryptoNames.iv]!!,
            mpHash = encryptedMPHashCipherAndIV[CryptoNames.cipher]!!,
            mpHashIv = encryptedMPHashCipherAndIV[CryptoNames.iv]!!,
            pinHash = encryptedPINHashCipherAndIV[CryptoNames.cipher]!!,
            pinHashIv = encryptedPINHashCipherAndIV[CryptoNames.iv]!!,
        )

        db.vaultDao().insert(newVault)
        val import = ImportsHandler.getImport(template = ExportTemplates.Passhaven, import = export)
        ImportsHandler.__apply__(
            entries = import,
            db = db,
            encryptionKey = key!!
        )

        return db.vaultDao().getVault()
    }

    suspend fun selfDestroy(dao: VaultDao): Boolean {
        var vaults = dao.getVault()
        while (vaults.isNotEmpty()) {
            for (vlt: Vault in vaults) {
                dao.delete(vlt)
            }
            vaults = dao.getVault()
        }
        return true
    }

    fun setBackupFolder(uri: Uri, context: Context) {
        VaultPrefs.saveBackupFolder(context, uri)
    }

    suspend fun getEncryptionKey(context: Context): ByteArray {
        val vaultDao = DatabaseProvider.getDatabase(context).vaultDao()
        val vault = vaultDao.getVault()
        if (vault.isEmpty())
            throw RuntimeException("No vault found!")

        val keystore: KeyStore = KeyStore.getInstance(keystoreInstanceName).apply { load(null) }
        val mpProtectingKey: Key = keystore.getKey(mpProtectingKeyName, null)
            ?: throw RuntimeException("Cannot retrieve the $mpProtectingKeyName key! A $mpProtectingKeyName key might not have been generated...")

        return AndroidCrypto.decrypt(mapOf(CryptoNames.cipher to vault[0].mpKey, CryptoNames.iv to vault[0].mpIv), mpProtectingKey as SecretKey)
    }

    fun updateFlabs(flabs: Int, context: Context) {
        if (flabs !in 10..30) return

        VaultPrefs.saveFlabs(context, flabs)
        VaultPrefs.saveFlabsr(context, flabs)
    }

    fun getBackupInfo(context: Context): Triple<Uri, Long, Long> {
        val backupFolder = VaultPrefs.getBackupFolder(context)
        val backupEvery = VaultPrefs.getBackupEvery(context)
        val lastBackup = VaultPrefs.getLastBackupTimestamp(context)

        return Triple(backupFolder, backupEvery, lastBackup)
    }

    fun updateTV(timeVariant: Long, context: Context) {
        VaultPrefs.saveBackupEvery(context, timeVariant)
    }
}
