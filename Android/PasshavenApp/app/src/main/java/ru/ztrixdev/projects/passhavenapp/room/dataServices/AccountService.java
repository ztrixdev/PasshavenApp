package ru.ztrixdev.projects.passhavenapp.room.dataServices;

import ru.ztrixdev.projects.passhavenapp.pHbeKt.crypto.SodiumCrypto;
import ru.ztrixdev.projects.passhavenapp.room.dataModels.Account;

public class AccountService {
    static final SodiumCrypto sodium = SodiumCrypto.INSTANCE;

    public static Account encrypt(Account account, byte[] key) {
        String additionalNote = null;
        if (account.getAdditionalNote() != null)
            additionalNote = sodium.encrypt(account.getAdditionalNote(), key);

        String mfaSecret = null;
        if (account.getMfaSecret() != null)
            mfaSecret = sodium.encrypt(account.getMfaSecret(), key);

        return new Account(
                account.getUuid(),
                account.getReprompt(),
                sodium.encrypt(account.getName(), key),
                sodium.encrypt(account.getUsername(), key),
                sodium.encrypt(account.getPassword(), key),
                mfaSecret,
                null,
                additionalNote,
                account.getDateCreated()
        );
    }

    public static Account decrypt(Account account, byte[] key) {
        String additionalNote = null;
        if (account.getAdditionalNote() != null)
            additionalNote = sodium.decrypt(account.getAdditionalNote(), key);

        String mfaSecret = null;
        if (account.getMfaSecret() != null)
            mfaSecret = sodium.decrypt(account.getMfaSecret(), key);

        return new Account(
                account.getUuid(),
                account.getReprompt(),
                sodium.decrypt(account.getName(), key),
                sodium.decrypt(account.getUsername(), key),
                sodium.decrypt(account.getPassword(), key),
                mfaSecret,
                null,
                additionalNote,
                account.getDateCreated()
        );
    }
}
