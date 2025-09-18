package ru.ztrixdev.projects.passhavenapp.pHbeKt;

import java.security.SecureRandom;
import java.util.*;

public class PasswordGenerator {
    private static final Integer DEFAULT_PASSWORD_LENGTH = 16;
    private static final Integer DEFAULT_ALLOW_DIGITS = 1;
    private static final Integer DEFAULT_ALLOW_LATIN = 1;
    private static final Integer DEFAULT_ALLOW_UPPERCASE = 1;
    private static final Integer DEFAULT_ALLOW_SPECIAL_CHARACTERS = 0;
    private static final Integer DEFAULT_ALLOW_SPACES = 0;
    private static final Integer DEFAULT_ALLOW_CYRILLIC = 0;
    private static final Integer MAX_PASSWORD_LENGTH = 196;

    private static final Map<PasswordGeneratorSettings, Integer> _defaultOptions = new HashMap<>() {
        {
            put(PasswordGeneratorSettings.Length, DEFAULT_PASSWORD_LENGTH);
            put(PasswordGeneratorSettings.AllowDigits, DEFAULT_ALLOW_DIGITS);
            put(PasswordGeneratorSettings.AllowLatin, DEFAULT_ALLOW_LATIN);
            put(PasswordGeneratorSettings.AllowUppercase, DEFAULT_ALLOW_UPPERCASE);
            put(PasswordGeneratorSettings.AllowSpecialCharacters, DEFAULT_ALLOW_SPECIAL_CHARACTERS);
            put(PasswordGeneratorSettings.AllowSpaces, DEFAULT_ALLOW_SPACES);
            put(PasswordGeneratorSettings.AllowCyrillic, DEFAULT_ALLOW_CYRILLIC);
        }
    };

    private Map<PasswordGeneratorSettings, Integer> _options = new HashMap<>() {};

    public void setDefaultOptions() {
        _options = _defaultOptions;
    }

    private Map<PasswordGeneratorSettings, Integer> validateOptions(Map<PasswordGeneratorSettings, Integer> customOptions) {
        Map<PasswordGeneratorSettings, Integer> validatedOptions;

        // Sets non-specified settings to default values
        validatedOptions = _defaultOptions;
        for (PasswordGeneratorSettings setting : customOptions.keySet()) {
            if (customOptions.containsKey(setting)) {
                validatedOptions.replace(setting, customOptions.get(setting));
            }
        }

        if (validatedOptions.get(PasswordGeneratorSettings.Length) > MAX_PASSWORD_LENGTH) {
            validatedOptions.replace(PasswordGeneratorSettings.Length, DEFAULT_PASSWORD_LENGTH);
        }

        return validatedOptions;
    }

    public void setCustomOptions(Map<PasswordGeneratorSettings, Integer> customOptions) {
        _options = validateOptions(customOptions);
    }

    public String generate() {
        ArrayList<Character> charsPool = new ArrayList<>();

        if (_options.get(PasswordGeneratorSettings.AllowDigits) >= 1) {
            Collections.addAll(charsPool, PasswordGeneratorCharsets.digits);
        }
        if (_options.get(PasswordGeneratorSettings.AllowSpecialCharacters) >= 1) {
            Collections.addAll(charsPool, PasswordGeneratorCharsets.specialCharacters);
        }
        if (_options.get(PasswordGeneratorSettings.AllowLatin) >= 1) {
            Collections.addAll(charsPool, PasswordGeneratorCharsets.latinLowercase);
        }
        if (_options.get(PasswordGeneratorSettings.AllowCyrillic) >= 1) {
            Collections.addAll(charsPool, PasswordGeneratorCharsets.russianCyrillicLowercase);
        }
        if (_options.get(PasswordGeneratorSettings.AllowUppercase) >= 1) {
            if (charsPool.contains('q')) {
                Collections.addAll(charsPool, PasswordGeneratorCharsets.latinUppercase);
            }
            if (charsPool.contains('ы')) {
                Collections.addAll(charsPool, PasswordGeneratorCharsets.russianCyrillicUppercase);
            }
        }
        if (_options.get(PasswordGeneratorSettings.AllowSpaces) >= 1) {
            Collections.addAll(charsPool, PasswordGeneratorCharsets.spaces);
        }
        int charsPoolLen = charsPool.toArray().length;

        StringBuilder result = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < _options.get(PasswordGeneratorSettings.Length); i++) {
            result.append(charsPool.get(rnd.nextInt(charsPoolLen)));
        }

        return result.toString();
    }
}
