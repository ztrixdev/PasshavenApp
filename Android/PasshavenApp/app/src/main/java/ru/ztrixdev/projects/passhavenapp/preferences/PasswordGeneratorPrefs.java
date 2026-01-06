package ru.ztrixdev.projects.passhavenapp.preferences;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

import ru.ztrixdev.projects.passhavenapp.Utils;
import ru.ztrixdev.projects.passhavenapp.pHbeKt.generators.PasswordGenerator;
import ru.ztrixdev.projects.passhavenapp.pHbeKt.generators.PasswordGeneratorSettings;

public class PasswordGeneratorPrefs {
    private static final String PREFS_NAME = "password_generator_prefs";
    private static final String PREF_ALLOW_UPPERCASE_KEY = "allow_uppercase";
    private static final String PREF_ALLOW_CYRILLIC_KEY = "allow_cyrillic";
    private static final String PREF_ALLOW_LATIN_KEY = "allow_latin";
    private static final String PREF_ALLOW_DIGITS_KEY = "allow_digits";
    private static final String PREF_ALLOW_SPECIAL_CHARS_KEY = "allow_special_characters";
    private static final String PREF_ALLOW_SPACES_KEY = "allow_spaces";
    private static final String PREF_LENGTH_KEY = "length";

    private static final Map<PasswordGeneratorSettings, String> PGS_TO_PREF_KEYS = new HashMap<>() {
        {
            put(PasswordGeneratorSettings.AllowUppercase, PREF_ALLOW_UPPERCASE_KEY);
            put(PasswordGeneratorSettings.AllowCyrillic, PREF_ALLOW_CYRILLIC_KEY);
            put(PasswordGeneratorSettings.AllowLatin, PREF_ALLOW_LATIN_KEY);
            put(PasswordGeneratorSettings.AllowDigits, PREF_ALLOW_DIGITS_KEY);
            put(PasswordGeneratorSettings.AllowSpecialCharacters, PREF_ALLOW_SPECIAL_CHARS_KEY);
            put(PasswordGeneratorSettings.AllowSpaces, PREF_ALLOW_SPACES_KEY);
            put(PasswordGeneratorSettings.Length, PREF_LENGTH_KEY);
        }
    };

    /// The two functions below are responsible for saving PasswordGenerator settings
    /// The first function only works with parameters that need a boolean.
    /// The second function only works with the password's length parameter, since it's the only Integer in the Settings.
    public static void saveSettings(PasswordGeneratorSettings key, Boolean value, Context context) {
        if (key == PasswordGeneratorSettings.Length) return;
        PreferencesMaster.put(PREFS_NAME, PGS_TO_PREF_KEYS.get(key), value, context);
    }

    public static void saveSettings(PasswordGeneratorSettings key, Integer value, Context context) {
        if (key != PasswordGeneratorSettings.Length) return;
        PreferencesMaster.put(PREFS_NAME, PGS_TO_PREF_KEYS.get(key), value, context);
    }

    public static Map<PasswordGeneratorSettings, Integer> loadSettings(Context context) {
        Map<PasswordGeneratorSettings, Integer> pwdGenSettings = new HashMap<>();

        for (PasswordGeneratorSettings setting : PGS_TO_PREF_KEYS.keySet()) {
            String key = PGS_TO_PREF_KEYS.get(setting);

            if (setting == PasswordGeneratorSettings.Length) {
                Integer defaultValue = PasswordGenerator.defaultOptions.get(setting);
                pwdGenSettings.put(setting, PreferencesMaster.get(PREFS_NAME, key, defaultValue, context));
            } else {
                Boolean defaultBool = Utils.IntegerToBoolean(PasswordGenerator.defaultOptions.get(setting));
                Boolean storedBool = PreferencesMaster.get(PREFS_NAME, key, defaultBool, context);
                pwdGenSettings.put(setting, Utils.BooleanToInteger(storedBool));
            }
        }

        return pwdGenSettings;
    }
}
