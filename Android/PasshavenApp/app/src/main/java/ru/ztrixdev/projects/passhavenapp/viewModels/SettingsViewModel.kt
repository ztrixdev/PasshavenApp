package ru.ztrixdev.projects.passhavenapp.viewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.net.toUri
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.SpecialCharNames
import ru.ztrixdev.projects.passhavenapp.Utils
import ru.ztrixdev.projects.passhavenapp.entryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.entryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.handlers.ExportTemplates
import ru.ztrixdev.projects.passhavenapp.handlers.ExportsHandler
import ru.ztrixdev.projects.passhavenapp.handlers.ImportsHandler
import ru.ztrixdev.projects.passhavenapp.handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.pHbeKt.MasterPassword
import ru.ztrixdev.projects.passhavenapp.pHbeKt.PIN_LENGTH_LIMIT
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto
import ru.ztrixdev.projects.passhavenapp.preferences.SecurityPrefs
import ru.ztrixdev.projects.passhavenapp.room.Account
import ru.ztrixdev.projects.passhavenapp.room.Card
import ru.ztrixdev.projects.passhavenapp.room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.room.Folder
import ru.ztrixdev.projects.passhavenapp.specialCharacters

class SettingsViewModel: ViewModel() {
    val openAppearance = mutableStateOf(false)
    val openSecurity = mutableStateOf(false)
    val openExports = mutableStateOf(false)
    val openImports = mutableStateOf(false)
    val openInfo = mutableStateOf(false)
    val openPINChange = mutableStateOf(false)

    var currentImportStage by mutableStateOf(ImportStages.Main)
    var importFileContents by mutableStateOf("")

    enum class ImportStages {
        Main, FileSelect, EntrySelect, Done, CheckPassword
    }


    val currentPINConfirmed = mutableStateOf(false)
    val currentPIN = mutableStateOf("")
    val firstPromptDone = mutableStateOf(false)
    val firstPromptPin = mutableStateOf("")
    val secondPromptDone = mutableStateOf(false)
    val secondPromptPin = mutableStateOf("")
    var pinLastChanged = mutableLongStateOf(0L)
    var mpLastChanged = mutableLongStateOf(0L)

    fun onSecurityOpened(context: Context) {
        pinLastChanged.longValue = SecurityPrefs.getLastPINChange(context)
        mpLastChanged.longValue = SecurityPrefs.getLastMPChange(context)
    }

    @OptIn(ExperimentalStdlibApi::class, ExperimentalComposeUiApi::class)
    suspend fun onCPINPadClick(btnClicked: Any, ctx: Context) {
        if (btnClicked.toString().isDigitsOnly()) {
            try {
                val newNumber = btnClicked.toString()
                when {
                    !currentPINConfirmed.value -> if (currentPIN.value.length < PIN_LENGTH_LIMIT) currentPIN.value += newNumber
                    !firstPromptDone.value -> if (firstPromptPin.value.length < PIN_LENGTH_LIMIT) firstPromptPin.value += newNumber
                    !secondPromptDone.value -> if (secondPromptPin.value.length < PIN_LENGTH_LIMIT) secondPromptPin.value += newNumber
                }
            } catch (_: NumberFormatException) {
                println("Somehow the clicked button contains a digit, but, it can't be parsed by Kotlin .toInt extension private function. Weird lol")
            }
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Backspace].toString()) {
            when {
                !currentPINConfirmed.value -> currentPIN.value = currentPIN.value.dropLast(1)
                !firstPromptDone.value -> firstPromptPin.value = firstPromptPin.value.dropLast(1)
                !secondPromptDone.value -> secondPromptPin.value = secondPromptPin.value.dropLast(1)
            }
        } else if (btnClicked.toString() == specialCharacters[SpecialCharNames.Tick].toString()) {
            when {
                !currentPINConfirmed.value -> {
                    val pinMatches = VaultHandler.loginByPIN(currentPIN.value, ctx)
                    if (pinMatches)
                        currentPINConfirmed.value = true
                    else
                        resetPIN()
                }
                !firstPromptDone.value -> {
                    if (!MasterPassword.verifyPIN(firstPromptPin.value))
                        resetPIN()
                    else
                        firstPromptDone.value = true
                }
                !secondPromptDone.value -> {
                    if (firstPromptPin.value.contentEquals(secondPromptPin.value)) {
                        changePIN(ctx)
                        secondPromptDone.value = true
                        openPINChange.value = false
                    }
                    else
                        resetPIN()
                }
            }
        }
    }


    fun _setSelectedFlabs(flabs: Int, context: Context) {
        VaultHandler.updateFlabs(flabs, context)
    }

    val _selectedTimeVariant = mutableLongStateOf(0L)

    val _backupFolder = mutableStateOf("".toUri())

    fun _setSelectedTV(timeVariant: Long, context: Context) {
        VaultHandler.updateTV(timeVariant, context)
        _selectedTimeVariant.longValue = timeVariant
    }


    fun getCurrentlyEditedPINsLen(): Int {
        return when {
            !currentPINConfirmed.value -> currentPIN.value.length
            !firstPromptDone.value -> firstPromptPin.value.length
            !secondPromptDone.value -> secondPromptPin.value.length
            else -> 0
        }
    }



    suspend fun changePIN(ctx: Context) {
        VaultHandler.changePIN(secondPromptPin.value, ctx)
        SecurityPrefs.saveLastPINChange(System.currentTimeMillis(), ctx)
    }

    suspend fun setBackupPassword(password: String, context: Context) {
        val encryptedPassword = SodiumCrypto.encrypt(password, VaultHandler.getEncryptionKey(context))
        SecurityPrefs.saveBackupPassword(encryptedPassword, context)
    }

    suspend fun export(context: Context): Boolean {
        val key = VaultHandler.getEncryptionKey(context)

        var password = SecurityPrefs.getBackupPassword(context)
        val noPasswordSet = password.contentEquals("")
        if (!noPasswordSet)
            password = SodiumCrypto.decrypt(password, key)

        val entries = EntryManager.getAllEntriesForExport(database = DatabaseProvider.getDatabase(context), encryptionKey = key)
        val folders = FolderManager.getFolders(context = context)
        var export = ExportsHandler.getExport(ExportTemplates.Passhaven, entries = entries, folders = folders)
        if (!noPasswordSet) // Protects the export if a password is set, will be changed later.
            export = ExportsHandler.protectExport(export = export, password = password)

        ExportsHandler.exportToFolder(context.contentResolver, export, context)

        return true
    }

    fun resetPIN() {
        currentPINConfirmed.value = false
        currentPIN.value = ""
        firstPromptDone.value = false
        firstPromptPin.value = ""
        secondPromptDone.value = false
        secondPromptPin.value = ""
    }


    var importEntries by mutableStateOf<ExportsHandler.ExportWrapper>( ExportsHandler.ExportWrapper(
        mutableListOf(), mutableListOf(), mutableListOf()
    ))
    var includedImportEntries by mutableStateOf<ExportsHandler.ExportWrapper>( ExportsHandler.ExportWrapper(
    mutableListOf(), mutableListOf(), mutableListOf()
    ))

    fun fetchImportFileEntries() {
        val results = ImportsHandler.getImport(ExportTemplates.Passhaven, importFileContents)

        if (results.isNotEmpty()) {
            importEntries.Folder?.clear()
            importEntries.Card?.clear()
            importEntries.Account?.clear()

            for (category in results) {
                category.Folder?.let { importEntries.Folder?.addAll(it) }
                category.Card?.let { importEntries.Card?.addAll(it) }
                category.Account?.let { importEntries.Account?.addAll(it) }
            }
        }
    }

    fun getEntryFlattenedList(): List<Any> {
        val flattened = mutableListOf<Any>()

        importEntries.Folder?.let { flattened.addAll(it) }
        importEntries.Card?.let { flattened.addAll(it) }
        importEntries.Account?.let { flattened.addAll(it) }

        return flattened
    }


    private fun refreshIncludedEntries() {
        val current = includedImportEntries
        includedImportEntries = ExportsHandler.ExportWrapper(
            Card = current.Card?.toMutableList(),
            Account = current.Account?.toMutableList(),
            Folder = current.Folder?.toMutableList()
        )
    }

    fun includeImportEntry(entry: Any) {
        when (entry) {
            is Card -> importEntries.Card?.find { it.uuid == entry.uuid }?.let {
                includedImportEntries.Card?.add(it)
            }
            is Account -> importEntries.Account?.find { it.uuid == entry.uuid }?.let {
                includedImportEntries.Account?.add(it)
            }
            is Folder -> importEntries.Folder?.find { it.uuid == entry.uuid }?.let {
                includedImportEntries.Folder?.add(it)
            }
        }
        refreshIncludedEntries() // Trigger UI update
    }

    fun excludeImportEntry(entry: Any) {
        when (entry) {
            is Card -> includedImportEntries.Card?.removeAll { it.uuid == entry.uuid }
            is Account -> includedImportEntries.Account?.removeAll { it.uuid == entry.uuid }
            is Folder -> includedImportEntries.Folder?.removeAll { it.uuid == entry.uuid }
        }
        refreshIncludedEntries() // Trigger UI update
    }


    val IMPORTABLE_SIGNAL: String = "importable"
    val ENCRYPTED_SIGNAL: String = "encrypted"
    val CORRUPTED_SIGNAL: String = "corrupted"
    fun checkImportability(fileContents: String): String {
        return if (fileContents == Utils.FILE_NOT_FOUND_SIGNAL || fileContents == Utils.IO_EXCEPTION_SIGNAL)
            fileContents
        else if (fileContents.startsWith(ExportsHandler._beginSaltStr) && fileContents.contains(ExportsHandler._endSaltStr))
            ENCRYPTED_SIGNAL
        else if (ImportsHandler.getImport(ExportTemplates.Passhaven, fileContents).isNotEmpty())
            IMPORTABLE_SIGNAL
        else
           CORRUPTED_SIGNAL
    }

    fun importBackToMain() {
        currentImportStage = ImportStages.Main
    }

    fun clearImport() {
        importEntries = ExportsHandler.ExportWrapper(mutableListOf(), mutableListOf(), mutableListOf())
        importFileContents = ""
        includedImportEntries = ExportsHandler.ExportWrapper(mutableListOf(), mutableListOf(), mutableListOf())
        importBackToMain()
    }

    suspend fun finishImport(context: Context) {
        if (includedImportEntries.Card?.isNotEmpty() == true ||
            includedImportEntries.Account?.isNotEmpty() == true ||
            includedImportEntries.Folder?.isNotEmpty() == true) {

            val db = DatabaseProvider.getDatabase(context = context)
            val key = VaultHandler.getEncryptionKey(context = context)
            ImportsHandler.__apply__(listOf(includedImportEntries), db, key)
        }
    }

}
