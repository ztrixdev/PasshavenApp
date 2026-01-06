package ru.ztrixdev.projects.passhavenapp.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesMaster {
    private static SharedPreferences getPrefs (String prefsName, Context context) {
        return context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    public static void put(String prefsName, String key, String value, Context context) {
        SharedPreferences.Editor editor = getPrefs(prefsName, context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void put(String prefsName, String key, Integer value, Context context) {
        SharedPreferences.Editor editor = getPrefs(prefsName, context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static void put(String prefsName, String key, Boolean value, Context context) {
        SharedPreferences.Editor editor = getPrefs(prefsName, context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void put(String prefsName, String key, Long value, Context context) {
        SharedPreferences.Editor editor = getPrefs(prefsName, context).edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static void put(String prefsName, String key, Float value, Context context) {
        SharedPreferences.Editor editor = getPrefs(prefsName, context).edit();
        editor.putFloat(key, value);
        editor.apply();
    }

    public static String get(String prefsName, String key, String defaultValue, Context context) {
        return getPrefs(prefsName, context).getString(key, defaultValue);
    }

    public static Integer get(String prefsName, String key, Integer defaultValue, Context context) {
        return getPrefs(prefsName, context).getInt(key, defaultValue);
    }

    public static Boolean get(String prefsName, String key, Boolean defaultValue, Context context) {
        return getPrefs(prefsName, context).getBoolean(key, defaultValue);
    }

    public static Long get(String prefsName, String key, Long defaultValue, Context context) {
        return getPrefs(prefsName, context).getLong(key, defaultValue);
    }

    public static Float get(String prefsName, String key, Float defaultValue, Context context) {
        return getPrefs(prefsName, context).getFloat(key, defaultValue);
    }
}
