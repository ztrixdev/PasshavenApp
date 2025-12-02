package ru.ztrixdev.projects.passhavenapp.Handlers;

import androidx.annotation.NonNull;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorException;

import org.kotlincrypto.core.xof.Xof;

import java.security.Provider;
import java.security.Security;

import ru.ztrixdev.projects.passhavenapp.Utils;

public class MFAHandler {
    // !!! This is crucial to call every time you use GoogleAuth, otherwise the app will CRASH !!!
    // This function swaps the default SUN algorithm provider with the SecureRandom.SHA1PRNG provider.
    // It is crucial because SOMEONE didn't bother to update his library since 2018 and to test whether or not his codepiece works on Android. (https://github.com/wstrange/GoogleAuth/issues/65#issuecomment-362588108)
    // I might consider forking it, rewriting the whole thing in Kotlin and making it viable for Android folks. Check out https://gitlab.com/faulhaj/ for updates :3
    private static void setValidSecureRandomProvider() {
        final String algorithmProviderFilter = "SecureRandom.SHA1PRNG";
        final String algorithmProviderProperty = "com.warrenstrange.googleauth.rng.algorithmProvider";

        Provider[] secureRandomProviders = Security.getProviders(algorithmProviderFilter);
        String name = secureRandomProviders[0].getName();
        System.setProperty(algorithmProviderProperty, name);
    }

    public static boolean verifySecret(@NonNull String secret) {
        // So that it won't crash out when the non-required MFA secret field is not filled.
        if (secret.isBlank()) {
            return true;
        }

        setValidSecureRandomProvider();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        try {
            gAuth.getTotpPassword(secret);
        } catch (IllegalArgumentException | GoogleAuthenticatorException exception) {
            return false;
        }

        return true;
    }

    public static long getTotpCode(@NonNull String secret) {
        if (!verifySecret(secret))
            return 0L;

        setValidSecureRandomProvider();
        GoogleAuthenticator gAuth = new GoogleAuthenticator();

        return gAuth.getTotpPassword(secret);
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
