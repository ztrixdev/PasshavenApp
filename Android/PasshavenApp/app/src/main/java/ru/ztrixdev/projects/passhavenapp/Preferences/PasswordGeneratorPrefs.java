package ru.ztrixdev.projects.passhavenapp.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import ru.ztrixdev.projects.passhavenapp.Utils;
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Generators.PasswordGenerator;
import ru.ztrixdev.projects.passhavenapp.pHbeKt.Generators.PasswordGeneratorSettings;

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
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // You can't express length via a BOOLEAN 😭
        if (key == PasswordGeneratorSettings.Length)
            return;

        editor.putBoolean(PGS_TO_PREF_KEYS.get(key), value);
        editor.apply();
    }

    public static void saveSettings(PasswordGeneratorSettings key, Integer value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (key != PasswordGeneratorSettings.Length)
            return;

        editor.putInt(PGS_TO_PREF_KEYS.get(key), value);
        editor.apply();
    }

    public static Map<PasswordGeneratorSettings, Integer> loadSettings(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Map<PasswordGeneratorSettings, Integer> pwdGenSettings = new HashMap<>();
        for (PasswordGeneratorSettings setting: PGS_TO_PREF_KEYS.keySet()) {
            if (setting != PasswordGeneratorSettings.Length) {
                pwdGenSettings.put(setting,
                    Utils.BooleanToInteger(
                        sharedPreferences.getBoolean(
                                PGS_TO_PREF_KEYS.get(setting),
                                Utils.IntegerToBoolean(PasswordGenerator.defaultOptions.get(setting))
                        )
                    )
                );
            } else {
                pwdGenSettings.put(setting,
                    sharedPreferences.getInt(
                            PGS_TO_PREF_KEYS.get(setting),
                            PasswordGenerator.defaultOptions.get(setting)
                    )
                );
            }
        }

        return pwdGenSettings;
    }
}
