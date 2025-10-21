package ru.ztrixdev.projects.passhavenapp.Handlers

import com.google.gson.Gson
import com.goterl.lazysodium.exceptions.SodiumException
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.CryptoNames
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.Keygen
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.SodiumCrypto

object ExportsHandler {
    fun export(template: ExportTemplates, entries: List<Any>, folders: List<Folder>): String {
        when (template) {
            ExportTemplates.Passhaven -> {
                return _exportPH(entries, folders)
            }
            else -> {

            }
        }

        return ""
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
    private val _beginSaltStr = "{beginsalt}"
    private val _endSaltStr = "{endsalt}"
    fun protectExport(export: String, password: String): String {
        var blob = "${_beginSaltStr}"
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