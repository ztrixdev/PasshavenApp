package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.EntryManagers.AccountManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.CardManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.Room.encrypt
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardBrands
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardCredentials
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Generators.PasswordGenerator
import kotlin.uuid.Uuid

class NewEntryViewModel: ViewModel() {
    var selectedFolderUuid by mutableStateOf<Uuid?>(null)

    fun setSelectedFolder(folder: Folder) {
        selectedFolderUuid = folder.uuid
    }

    fun getFolders(context: Context): List<Folder> {
        return FolderManager.getFolders(context)
    }

    fun getSelectedFolder(context: Context): Folder? {
        return FolderManager.getFolderByUuid(context, selectedFolderUuid as Uuid)
    }

    var newEntryName by mutableStateOf(TextFieldValue(""))
    var additionalNote by mutableStateOf(TextFieldValue(""))

    var selectedEntryType by mutableStateOf(EntryTypes.Account)

    // the _ is crucial because otherwise it's gonna throw because of JVM signature error.
    // швабры держат потолок: https://pikabu.ru/story/chuzhoy_kod_5762938
    fun _setSelectedEntryType(type: EntryTypes) {
        selectedEntryType = type
    }

    // Account-related code starts here:
    var username by mutableStateOf(TextFieldValue(""))
    var password by mutableStateOf(TextFieldValue(""))
    var mfaSecret by mutableStateOf(TextFieldValue(""))

    var recoveryCodesAmount by mutableIntStateOf(1)
    var recoveryCodes = mutableStateListOf<TextFieldValue>(TextFieldValue(""))

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

    fun createAccount(): Account {
        val roomEdibleCodes = emptyList<String>().toMutableList()
        if (recoveryCodes.size > 1) {
            recoveryCodes.forEach {
                roomEdibleCodes += it.text
            }
        }

        return Account(
            uuid = Uuid.random(),
            name = newEntryName.text,
            username = username.text,
            password = password.text,
            // todo: add reprompt functionality
            reprompt = false,
            mfaSecret = mfaSecret.text,
            recoveryCodes = roomEdibleCodes,
            additionalNote = additionalNote.text,
        )
    }

    fun pushNewEntry(account: Account, context: Context): Uuid {
        val database = DatabaseProvider.getDatabase(context)

        val newAccUuid = AccountManager.createAccount(
            database = database,
            account = account,
            encryptionKey = VaultHandler().getEncryptionKey(context)
        )

        if (selectedFolderUuid != null) {
            FolderManager.performEntryFolderOper(
                database = database,
                operation = FolderManager.EntryFolderOperations.Add,
                entryUuid = newAccUuid,
                targetFolderUuid = selectedFolderUuid as Uuid
            )
        }

        return newAccUuid
    }


    // Card-related code starts here:
    var selectedCardBrand by mutableStateOf(CardBrands.Visa)

    var cardNumber by mutableStateOf(TextFieldValue(""))
    var expirationMMYY by mutableStateOf(TextFieldValue(""))
    var cardholderName by mutableStateOf(TextFieldValue(""))
    var cvcCVV by mutableStateOf(TextFieldValue(""))

    fun _setSelectedCardBrand(brand: CardBrands) {
        selectedCardBrand = brand
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
                if (text.length > 1 && !text.substring(0, 1).isDigitsOnly())
                    return false
                // checks if the third symbol, if exists, is a slash or a backslash
                if (text.length > 2 && (text[2] != '/'  && text[2] != '\\'))
                    return false
                // checks if the last two symbols are digits
                if (text.length == 5 && !text.substring(3, 4).isDigitsOnly())
                    return false
                if (text.length > 5)
                    return false
            }
        }
        return true
    }

    fun createCard(): Card {
        val uuid = Uuid.random()
        val name = newEntryName.text
        val number = cardNumber.text
        val expirationDate = expirationMMYY.text
        val cvcCvv = cvcCVV.text
        val brand = selectedCardBrand.toString()
        val cardholder = cardholderName.text
        val additionalNote = additionalNote.text

        return Card(uuid, false, name, number, expirationDate, cvcCvv, brand, cardholder, additionalNote)
    }

    fun pushNewEntry(card: Card, context: Context): Uuid {
        val database = DatabaseProvider.getDatabase(context)

        val newCardUuid = CardManager.createCard(
            database = database,
            card = card,
            encryptionKey = VaultHandler().getEncryptionKey(context)
        )

        if (selectedFolderUuid != null) {
            FolderManager.performEntryFolderOper(
                database = database,
                operation = FolderManager.EntryFolderOperations.Add,
                entryUuid = newCardUuid,
                targetFolderUuid = selectedFolderUuid as Uuid
            )
        }

        return newCardUuid
    }

    var allRequiredFieldsAreFilled by mutableStateOf(checkRequiredFields())
    fun checkRequiredFields(): Boolean {
        if (newEntryName.text.isEmpty())
            return false

        if (selectedEntryType == EntryTypes.Card)
            return when {
                cardNumber.text.isEmpty() -> false
                expirationMMYY.text.isEmpty() -> false
                cvcCVV.text.isEmpty() -> false
                cardholderName.text.isEmpty() -> false
                else -> true
            }

        if (selectedEntryType == EntryTypes.Account)
            return when {
                username.text.isEmpty() -> false
                password.text.isEmpty() -> false
                else -> true
            }

        return true
    }
}

