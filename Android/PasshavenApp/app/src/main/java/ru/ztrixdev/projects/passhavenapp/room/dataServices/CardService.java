package ru.ztrixdev.projects.passhavenapp.room.dataServices;

import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto;
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Card;

public class CardService {
    static final SodiumCrypto sodium = SodiumCrypto.INSTANCE;

    public static Card encrypt(Card card, byte[] key) {
        String additionalNote = null;
        if (card.getAdditionalNote() != null) {
            additionalNote = sodium.encrypt(card.getAdditionalNote(), key);
        }

        return new Card(
                card.getUuid(),
                card.getReprompt(),
                sodium.encrypt(card.getName(), key),
                sodium.encrypt(card.getNumber(), key),
                sodium.encrypt(card.getExpirationDate(), key),
                sodium.encrypt(card.getCvcCvv(), key),
                sodium.encrypt(card.getBrand(), key),
                sodium.encrypt(card.getCardholder(), key),
                additionalNote,
                card.getDateCreated()
        );
    }

    public static Card decrypt(Card card, byte[] key) {
        String additionalNote = null;
        if (card.getAdditionalNote() != null) {
            additionalNote = sodium.decrypt(card.getAdditionalNote(), key);
        }

        return new Card(
                card.getUuid(),
                card.getReprompt(),
                sodium.decrypt(card.getName(), key),
                sodium.decrypt(card.getNumber(), key),
                sodium.decrypt(card.getExpirationDate(), key),
                sodium.decrypt(card.getCvcCvv(), key),
                sodium.decrypt(card.getBrand(), key),
                sodium.decrypt(card.getCardholder(), key),
                additionalNote,
                card.getDateCreated()
        );
    }
}
