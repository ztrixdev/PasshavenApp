package ru.ztrixdev.projects.passhavenapp.Handlers

import android.content.ContentResolver
import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.google.gson.Gson
import com.goterl.lazysodium.exceptions.SodiumException
import ru.ztrixdev.projects.passhavenapp.DateTimeProcessor
import ru.ztrixdev.projects.passhavenapp.Preferences.VaultPrefs
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.CryptoNames
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.Keygen
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.SodiumCrypto


object ExportsHandler {
    data class ExportWrapper(
        val Folder: List<Folder>?,
        val Card: List<Card>?,
        val Account: List<Account>?
    )

    fun getExport(template: ExportTemplates, entries: List<Any>, folders: List<Folder>): String {
        when (template) {
            ExportTemplates.Passhaven -> {
                return _exportPH(entries, folders)
            }
            else -> {

            }
        }

        return ""
    }

    private const val _export_filename_part_1 = "export_"
    private const val _export_filename_part_2 = "_full.phbckp"

    fun exportToFolder(resolver: ContentResolver, export: String, context: Context): Boolean {
        val path = VaultPrefs.getBackupFolder(context)
        if (path == "".toUri())
            return false
        // The filename looks like this
        // export_05_22_2025_17_45_31_full.phbckp
        // for a backup made on May 22nd 2025
        // why MM/DD/YYYY you may ask?
        // because
        /*
        Oh say! Can you see
        by the dawn's early light
        What so proudly we hailed
        at the twilight's last gleaming;
        Whose broad stripes and bright stars,
        Through the perilous fight,
        O'er the ramparts we watched
        were so gallantly streaming?
        🇺🇸 🦅 💯
         */
        val today = DateTimeProcessor.convertForFIlename(System.currentTimeMillis())
        val filename = "$_export_filename_part_1${today}$_export_filename_part_2"

        val directory = DocumentFile.fromTreeUri(context, path)
        if (directory?.findFile(filename)?.exists() == true) {
            return false
        }

        val newFile: DocumentFile? = directory?.createFile("application/octet-stream", filename)

        if (newFile != null) {
            resolver.openOutputStream(newFile.uri)?.use { outputStream ->
                outputStream.write(export.toByteArray())
                outputStream.flush()
            }
        } else {
            return false
        }

        VaultPrefs.saveLastBackupTimestamp(context, System.currentTimeMillis())
        return true
    }

    fun checkIfABackupIsDue(context: Context): Boolean {
        return ((System.currentTimeMillis() - VaultPrefs.getLastBackupTimestamp(context)) > VaultPrefs.getBackupEvery(context))
    }

    private fun _exportPH(entries: List<Any>, folders: List<Folder>): String {
        val export: MutableList<Map<EntryTypes, Any>> = mutableListOf()
        val cards = entries.filter { it is Card }
        val accounts = entries.filter { it is Account }

        export.add(mapOf(EntryTypes.Folder to folders))
        export.add(mapOf(EntryTypes.Card to cards))
        export.add(mapOf(EntryTypes.Account to accounts))

        return Gson().toJson(export)
    }

    // This function makes the export encrypted by turning it into a nonsense-carrying blob.
    // The final blob looks like this:
    // {beginsalt}HEX_SALT_STRING{endsalt}
    // ENCRYPTED_EXPORT_HEX_STRING
    private const val _beginSaltStr = "{beginsalt}"
    private const val _endSaltStr = "{endsalt}"
    fun protectExport(export: String, password: String): String {
        var blob = _beginSaltStr
        val keyFromPWD = Keygen.deriveKeySaltPairFromMP(password)
        if (keyFromPWD[CryptoNames.key] != null) {
            val encryptedExport = SodiumCrypto.encrypt(export, keyFromPWD[CryptoNames.key] as ByteArray)
            blob += SodiumCrypto.sodium.toHexStr(keyFromPWD[CryptoNames.salt])
            blob += _endSaltStr
            blob += encryptedExport
            return blob
        }
        return "Failed to password-protect the export!"
    }

    fun getProtectedExport(export: String, password: String): String {
        val INCORRECT_PASSWORD_EXCEPTION = Exception("Incorrect password!")

        val salt = export.substring(export.indexOf(_beginSaltStr) + _beginSaltStr.length, export.indexOf(_endSaltStr))
        val key = Keygen.getKeyWithMPnSalt(password, SodiumCrypto.sodium.toBinary(salt))

        val exportBlob = export.substring(export.indexOf(_endSaltStr) + _endSaltStr.length)
        try {
            val decryptedExport = SodiumCrypto.decrypt(exportBlob, key)
            return decryptedExport
        } catch (se: SodiumException) {
            throw INCORRECT_PASSWORD_EXCEPTION
        }
    }
}