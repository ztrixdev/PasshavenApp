package ru.ztrixdev.projects.passhavenapp.EntryManagers

import com.goterl.lazysodium.exceptions.SodiumException
import ru.ztrixdev.projects.passhavenapp.Room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.decrypt
import ru.ztrixdev.projects.passhavenapp.Room.encrypt
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Crypto.SodiumCrypto
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
        } catch (_: SodiumException) {
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
