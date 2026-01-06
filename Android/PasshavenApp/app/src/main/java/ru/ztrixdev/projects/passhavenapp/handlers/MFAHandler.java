package ru.ztrixdev.projects.passhavenapp.handlers;

import androidx.annotation.NonNull;


import java.security.InvalidKeyException;

import ru.ztrixdev.projects.passhavenapp.Utils;
import ru.ztrixdev.projects.passhavenapp.pHbeKt.TOTP;

public class MFAHandler {
    public static boolean verifySecret(@NonNull String secret) {
        TOTP totp = new TOTP();
        try {
            totp.compute(secret);
            return true;
        } catch (InvalidKeyException exception) {
            return false;
        }
    }

    public static long getTotpCode(@NonNull String secret) {
        TOTP totp = new TOTP();
        try {
            return totp.compute(secret);
        } catch (InvalidKeyException exception) {
            return -1;
        }
    }

    public static class MFAQRData {
        public String label;
        public String issuer;
        public String secret;
    }

    private static final String MFAURI_PREFIX = "otpauth://totp/";
    private static final String MFAURI_SEPARATOR = "?";
    private static final String MFAURI_TOTP = "totp/";
    private static final String MFAURI_ISSUER = "issuer";
    private static final String MFAURI_SECRET = "secret";

    public static MFAQRData processQR(String contents) {
        MFAQRData data = new MFAQRData();

        if (!contents.startsWith(MFAURI_PREFIX))
            return null;

        data.label = contents.substring(contents.indexOf(MFAURI_TOTP) + 5, contents.indexOf(MFAURI_SEPARATOR));
        var params = Utils.getQueryParams(contents);
        if (!params.containsKey(MFAURI_SECRET))
            return null;

        if (params.get(MFAURI_ISSUER) != null && !params.get(MFAURI_ISSUER).isEmpty())
            data.issuer = params.get(MFAURI_ISSUER).get(0);
        if (params.get(MFAURI_SECRET) != null && !params.get(MFAURI_SECRET).isEmpty())
            data.secret = params.get(MFAURI_SECRET).get(0);

        if (!verifySecret(data.secret))
            return null;

        return data;
    }
}
