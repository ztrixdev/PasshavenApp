package ru.ztrixdev.projects.passhavenapp.Handlers;

import androidx.annotation.NonNull;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorException;

import java.security.Provider;
import java.security.Security;

public class MFAHandler {
    // !!! This is crucial to call every time you use GoogleAuth, otherwise the app will CRASH !!!
    // This function swaps the default SUN algorithm provider with the SecureRandom.SHA1PRNG provider.
    // It is crucial because SOMEONE didn't bother to update his library since 2018 and to test whether or not his codepiece works on Android. (https://github.com/wstrange/GoogleAuth/issues/65#issuecomment-362588108)
    // I might consider forking it, rewriting the whole thing in Kotlin and making it viable for Android folks. Check out https://gitlab.com/faulhaj/ for updates :3
    private void setValidSecureRandomProvider() {
        final String algorithmProviderFilter = "SecureRandom.SHA1PRNG";
        final String algorithmProviderProperty = "com.warrenstrange.googleauth.rng.algorithmProvider";

        Provider[] secureRandomProviders = Security.getProviders(algorithmProviderFilter);
        String name = secureRandomProviders[0].getName();
        System.setProperty(algorithmProviderProperty, name);
    }

    public boolean verifySecret(@NonNull String secret) {
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
}
