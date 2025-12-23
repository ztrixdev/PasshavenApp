package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.journeyapps.barcodescanner.ScanOptions
import ru.ztrixdev.projects.passhavenapp.EntryManagers.AccountManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.CardManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.EntryManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.Handlers.MFAHandler
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.Room.decrypt
import ru.ztrixdev.projects.passhavenapp.Utils
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardBrands
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardCredentials
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Generators.PasswordGenerator
import kotlin.uuid.Uuid

class ViewEntryViewModel : ViewModel() {

    val defaultQRScanOpts = ScanOptions().apply {
        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        setCameraId(0)
        setBeepEnabled(false)
    }

    var entryUuid: String? = ""

    var inFolder by mutableStateOf<Folder?>(null)
    var selectedFolderUuid by mutableStateOf<Uuid?>(null)

    var editMode by mutableStateOf(false)

    fun setSelectedFolder(folder: Folder) {
        selectedFolderUuid = folder.uuid
    }

    suspend fun getSelectedFolder(context: Context): Folder? {
        return FolderManager.getFolderByUuid(context, selectedFolderUuid as Uuid)
    }

    var dataFetchDone by mutableStateOf(false)

    var newEntryName by mutableStateOf(TextFieldValue(""))
    var additionalNote by mutableStateOf(TextFieldValue(""))
    var type by mutableStateOf(EntryTypes.Account)
    var createdOn by mutableLongStateOf(0L)

    // account specific fields
    var username by mutableStateOf(TextFieldValue(""))
    var password by mutableStateOf(TextFieldValue(""))
    var mfaSecret by mutableStateOf(TextFieldValue(""))
    var recoveryCodesAmount by mutableIntStateOf(1)
    var recoveryCodes = mutableStateListOf(TextFieldValue(""))


    // card specific fields
    var selectedCardBrand by mutableStateOf(CardBrands.Visa)

    var cardNumber by mutableStateOf(TextFieldValue(""))
    var expirationMMYY by mutableStateOf(TextFieldValue(""))
    var cardholderName by mutableStateOf(TextFieldValue(""))
    var cvcCVV by mutableStateOf(TextFieldValue(""))

    suspend fun getFolders(context: Context): List<Folder> {
        val folders = FolderManager.getFolders(context)
        return folders
    }

    fun updateMFA() {
        currentMFAValue = TextFieldValue(MFAHandler.getTotpCode(mfaSecret.text).toString())
    }

    suspend fun getCurrentData(context: Context) {
        if (entryUuid == null)
            return
        if (entryUuid!!.length != Utils.UUID_ALPHANUMERIC_STRING_LENGTH)
            return

        val entry = EntryManager.getEntryByUuid(DatabaseProvider.getDatabase(context), Uuid.parse(entryUuid!!))
        type = when (entry) {
            is Card -> EntryTypes.Card
            is Account -> EntryTypes.Account
            else -> return
        }

        val key = VaultHandler().getEncryptionKey(context)
        when (entry) {
            is Card -> entry.decrypt(key)
            is Account -> entry.decrypt(key)
            else -> return
        }

        when (entry) {
            is Card -> {
                newEntryName = TextFieldValue(entry.name)
                cardNumber = TextFieldValue(entry.number)
                cardholderName = TextFieldValue(entry.cardholder)
                expirationMMYY = TextFieldValue(entry.expirationDate)
                cvcCVV = TextFieldValue(entry.cvcCvv)
                createdOn = entry.dateCreated
                entry.additionalNote?.let { additionalNote = TextFieldValue(it) }
            }
            is Account -> {
                newEntryName = TextFieldValue(entry.name)
                username = TextFieldValue(entry.username)
                entry.mfaSecret?.let {
                    mfaSecret = TextFieldValue(it)
                    currentMFAValue = TextFieldValue(MFAHandler.getTotpCode(mfaSecret.text).toString())
                }
                password = TextFieldValue(entry.password)
                entry.recoveryCodes?.let {
                    recoveryCodes.removeAt(0)
                    recoveryCodesAmount = it.size
                    it.forEach { code ->
                        recoveryCodes.add(TextFieldValue(code))
                    }
                }
                createdOn = entry.dateCreated
                entry.additionalNote?.let { additionalNote = TextFieldValue(it) }
            }
        }

        val folders =  getFolders(context)
        folders.forEach { folder ->
            if (folder.entries.contains(Uuid.parse(entryUuid!!))) {
                inFolder = folder
            }
        }

        dataFetchDone = true
    }

    var isPasswordVisible by mutableStateOf(false)
    var currentMFAValue by mutableStateOf(TextFieldValue(""))

    fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
    }

    fun copyPassword(context: Context) {
        Utils.copyToClipboard(context, password.text)
    }

    fun copyMFACode(context: Context) {
        Utils.copyToClipboard(context, currentMFAValue.text)
    }

    fun generatePassword() {
        val passwordGenerator = PasswordGenerator()
        passwordGenerator.setDefaultOptions()

        password = TextFieldValue(passwordGenerator.generate())
    }

    fun addRecoveryCode() {
        recoveryCodesAmount++
        recoveryCodes.add(TextFieldValue(""))
    }

    fun deleteRecoveryCode(index: Int) {
        recoveryCodes.removeAt(index)
        recoveryCodesAmount--
    }

    // Might be used later
    // val CARD_NUMBER_INVALID_EXCEPTION = IllegalArgumentException("The provided card number cannot be converted to a valid number")
    val CARD_NUMBER_TOO_SHORT_EXCEPTION = IllegalArgumentException("The provided card number is too short")
    fun autoDetectCardBrand(cardNumber: String): CardBrands {
        if (cardNumber.length < 4) {
            throw CARD_NUMBER_TOO_SHORT_EXCEPTION
        }

        val firstFourDigits = cardNumber.take(4).toInt()
        return when (firstFourDigits) {
            in 4000..4999 -> CardBrands.Visa
            in 2200..2204 -> CardBrands.MIR
            in 5100..5599, in 2221..2720 -> CardBrands.Mastercard
            in 3400 ..3499, in 3700..3799 -> CardBrands.AmericanExpress
            in 6200..6299 -> CardBrands.UnionPay
            in 5000..6999 -> CardBrands.Maestro
            else -> CardBrands.Huh
        }
    }


    fun _setSelectedCardBrand(brand: CardBrands) {
        selectedCardBrand = brand
    }

    fun validateCardCredentials(credType: CardCredentials, text: String): Boolean {
        when (credType) {
            CardCredentials.Number -> {
                if (text.length >= 23)
                    return false
                _setSelectedCardBrand(autoDetectCardBrand(text))
            }
            CardCredentials.CVC_CVV -> {
                if (!text.isDigitsOnly() || text.length > 3)
                    return false
            }
            CardCredentials.ExpirationDate -> {
                // a normal expir. date should look like this: 08/28
                // checks if the first two symbols are digits
                if (text.length > 1 && !text[1].toString().isDigitsOnly())
                    return false
                // checks if the third symbol, if exists, is a slash or a backslash
                if (text.length > 2 && (text[2] != '/'  && text[2] != '\\'))
                    return false
                // checks if the last two symbols are digits
                if (text.length == 5 && !text[3].toString().isDigitsOnly())
                    return false
                if (text.length > 5)
                    return false
            }
        }
        return true
    }

    var allRequiredFieldsAreFilled by mutableStateOf(checkRequiredFields())
    fun checkRequiredFields(): Boolean {
        if (newEntryName.text.isEmpty())
            return false

        if (type == EntryTypes.Card)
            return when {
                cardNumber.text.isEmpty() -> false
                expirationMMYY.text.isEmpty() -> false
                cvcCVV.text.isEmpty() -> false
                cardholderName.text.isEmpty() -> false
                else -> true
            }

        if (type == EntryTypes.Account)
            return when {
                username.text.isEmpty() -> false
                password.text.isEmpty() -> false
                else -> true
            }

        return true
    }

    fun createAccount(): Account? {
        if (entryUuid == null) return null

        val roomEdibleCodes = emptyList<String>().toMutableList()
        if (recoveryCodes.size > 1) {
            recoveryCodes.forEach {
                roomEdibleCodes += it.text
            }
        }

        return Account(
            uuid = Uuid.parse(entryUuid!!),
            name = newEntryName.text,
            username = username.text,
            password = password.text,
            // todo: add reprompt functionality
            reprompt = false,
            mfaSecret = mfaSecret.text,
            recoveryCodes = roomEdibleCodes,
            additionalNote = additionalNote.text,
            dateCreated = createdOn
        )
    }

    fun createCard(): Card? {
        if (entryUuid == null) return null

        val uuid = Uuid.parse(entryUuid!!)
        val name = newEntryName.text
        val number = cardNumber.text
        val expirationDate = expirationMMYY.text
        val cvcCvv = cvcCVV.text
        val brand = selectedCardBrand.toString()
        val cardholder = cardholderName.text
        val additionalNote = additionalNote.text
        val dateCreated = createdOn

        return Card(uuid, false, name, number, expirationDate, cvcCvv, brand, cardholder, additionalNote, dateCreated)
    }

    var entryUpdated by mutableStateOf(false)
    suspend fun updateEntry(card: Card?, context: Context): Uuid {
        if (card == null) return Uuid.random()

        val database = DatabaseProvider.getDatabase(context)
        val entryUuid = Uuid.parse(this.entryUuid!!)

        CardManager.editCard(
            database = database,
            editedCard = card,
            encryptionKey = VaultHandler().getEncryptionKey(context)
        )

        if (inFolder != null) {
            FolderManager.performEntryFolderOper(
                database = database,
                operation = FolderManager.EntryFolderOperations.Remove,
                entryUuid = entryUuid,
                targetFolderUuid = inFolder!!.uuid
            )
        }

        if (selectedFolderUuid != null) {
            FolderManager.performEntryFolderOper(
                database = database,
                operation = FolderManager.EntryFolderOperations.Add,
                entryUuid = entryUuid,
                targetFolderUuid = selectedFolderUuid as Uuid
            )
        }

        entryUpdated = true
        return entryUuid
    }

    suspend fun updateEntry(account: Account?, context: Context): Uuid {
        val database = DatabaseProvider.getDatabase(context)
        val entryUuid = Uuid.parse(this.entryUuid!!)

        if (account == null) return Uuid.random()

        AccountManager.editAccount(
            database = database,
            editedAccount = account,
            encryptionKey = VaultHandler().getEncryptionKey(context)
        )

        if (inFolder != null) {
            FolderManager.performEntryFolderOper(
                database = database,
                operation = FolderManager.EntryFolderOperations.Remove,
                entryUuid = entryUuid,
                targetFolderUuid = inFolder!!.uuid
            )
        }

        if (selectedFolderUuid != null) {
            FolderManager.performEntryFolderOper(
                database = database,
                operation = FolderManager.EntryFolderOperations.Add,
                entryUuid = entryUuid,
                targetFolderUuid = selectedFolderUuid as Uuid
            )
        }

        entryUpdated = true
        return entryUuid
    }
}