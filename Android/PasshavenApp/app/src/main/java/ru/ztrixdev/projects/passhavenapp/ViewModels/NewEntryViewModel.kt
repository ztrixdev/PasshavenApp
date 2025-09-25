package ru.ztrixdev.projects.passhavenapp.ViewModels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import ru.ztrixdev.projects.passhavenapp.EntryManagers.CardManager
import ru.ztrixdev.projects.passhavenapp.EntryManagers.FolderManager
import ru.ztrixdev.projects.passhavenapp.Handlers.VaultHandler
import ru.ztrixdev.projects.passhavenapp.Room.Account
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.DatabaseProvider
import ru.ztrixdev.projects.passhavenapp.Room.Folder
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardBrands
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.CardCredentials
import ru.ztrixdev.projects.passhavenapp.ViewModels.Enums.EntryTypes
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

    fun pushNewEntry(account: Account, context: Context): Boolean {
        // todo

        return false
    }

    fun pushNewEntry(card: Card, context: Context): Uuid {
        val key = VaultHandler().getEncryptionKey(context)
        val res = CardManager.createCard(DatabaseProvider.getDatabase(context), card, key)
        return res
    }

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

        return true
    }
}

