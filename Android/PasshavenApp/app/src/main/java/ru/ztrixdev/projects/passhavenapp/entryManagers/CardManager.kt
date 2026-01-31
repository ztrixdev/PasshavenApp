package ru.ztrixdev.projects.passhavenapp.entryManagers

import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto
import ru.ztrixdev.projects.passhavenapp.room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Card
import ru.ztrixdev.projects.passhavenapp.room.dataServices.CardService
import kotlin.uuid.Uuid

object CardManager {
    suspend fun createCard(database: AppDatabase, card: Card, encryptionKey: ByteArray): Uuid {
        val encryptedCard = CardService.encrypt(card, encryptionKey)

        database.cardDao().insert(encryptedCard)

        return card.uuid
    }

    suspend fun retrieveCardByUuid(database: AppDatabase, uuid: Uuid): Card? {
        val card = database.cardDao().getCardByUuid(cardUuid = uuid) ?: return null

        return card
    }

    suspend fun getAllCards(database: AppDatabase, encryptionKey: ByteArray): List<Card> {
        val cards = database.cardDao().getALl()
        return cards.map { CardService.decrypt(it, encryptionKey) }
    }

    
    suspend fun editCard(database: AppDatabase, editedCard: Card, encryptionKey: ByteArray) {
        val cardDao = database.cardDao()

        // If no card in the database matches the Uuid of the editedCard, the function exits immediately
        retrieveCardByUuid(database, editedCard.uuid) ?: return

        var isEditedCardEncrypted = true
        try {
            // If a field can be decrypted - the Card object is encrypted
            SodiumCrypto.decrypt(editedCard.number, encryptionKey)
        } catch (_: IllegalArgumentException) {
            isEditedCardEncrypted = false
        }

        val cardToUpdate = if (!isEditedCardEncrypted)
            CardService.encrypt(editedCard, encryptionKey)
        else
            editedCard

        cardDao.update(cardToUpdate)
    }

    suspend fun deleteCard(database: AppDatabase, uuid: Uuid) {
        // If no card in the database matches the provided Uuid, the function exits immediately
        val card = retrieveCardByUuid(database, uuid) ?: return
        database.cardDao().delete(card)
    }
}
