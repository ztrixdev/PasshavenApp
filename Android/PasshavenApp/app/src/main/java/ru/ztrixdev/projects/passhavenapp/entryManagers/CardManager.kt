package ru.ztrixdev.projects.passhavenapp.entryManagers

import ru.ztrixdev.projects.passhavenapp.room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.room.Card
import ru.ztrixdev.projects.passhavenapp.room.decrypt
import ru.ztrixdev.projects.passhavenapp.room.encrypt
import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto
import kotlin.uuid.Uuid

object CardManager {
    suspend fun createCard(database: AppDatabase, card: Card, encryptionKey: ByteArray): Uuid {
        card.encrypt(encryptionKey)

        database.cardDao().insert(card)

        return card.uuid
    }

    suspend fun retrieveCardByUuid(database: AppDatabase, uuid: Uuid): Card? {
        val card = database.cardDao().getCardByUuid(cardUuid = uuid) ?: return null

        return card
    }

    suspend fun getAllCards(database: AppDatabase, encryptionKey: ByteArray): List<Card> {
        val cards = database.cardDao().getALl()
        cards.forEach { it.decrypt(encryptionKey) }

        return cards
    }
    
    suspend fun editCard(database: AppDatabase, editedCard: Card, encryptionKey: ByteArray) {
        val cardDao = database.cardDao()

        // If no card in the database matches the Uuid of the editedCard, the function exits immediately
        retrieveCardByUuid(database, editedCard.uuid) ?: return

        var isEditedCardEncrypted = true
        try {
            // If a field can be decrypted - the Card object is encrypted
            // (Card.encrypt() encrypts pretty much all the fields)
            SodiumCrypto.decrypt(editedCard.number, encryptionKey)
        } catch (_: IllegalArgumentException) {
            isEditedCardEncrypted = false
        }
        if (!isEditedCardEncrypted)
            editedCard.encrypt(encryptionKey)

        cardDao.update(editedCard)
    }

    suspend fun deleteCard(database: AppDatabase, uuid: Uuid) {
        // If no card in the database matches the provided Uuid, the function exits immediately
        val card = retrieveCardByUuid(database, uuid) ?: return
        database.cardDao().delete(card)
    }
}
