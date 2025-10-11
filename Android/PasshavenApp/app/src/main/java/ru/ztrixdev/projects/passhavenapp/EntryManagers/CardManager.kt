package ru.ztrixdev.projects.passhavenapp.EntryManagers

import ru.ztrixdev.projects.passhavenapp.Room.AppDatabase
import ru.ztrixdev.projects.passhavenapp.Room.Card
import ru.ztrixdev.projects.passhavenapp.Room.decrypt
import ru.ztrixdev.projects.passhavenapp.Room.encrypt
import kotlin.uuid.Uuid

object CardManager {
    fun createCard(database: AppDatabase, card: Card, encryptionKey: ByteArray): Uuid {
        card.uuid = Uuid.random()
        card.encrypt(encryptionKey)

        database.cardDao().insert(card)

        return card.uuid
    }

    fun retrieveCardByUuid(database: AppDatabase, uuid: Uuid, encryptionKey: ByteArray): Card? {
        val card = database.cardDao().getCardByUuid(cardUuid = uuid)
        if (card == null)
            return null
        card.decrypt(encryptionKey)

        return card
    }

    fun getAllCards(database: AppDatabase, encryptionKey: ByteArray): List<Card> {
        val cards = database.cardDao().getALl()
        cards.forEach { it.decrypt(encryptionKey) }

        return cards
    }
}
