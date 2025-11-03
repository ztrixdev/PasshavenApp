package ru.ztrixdev.projects.passhavenapp.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

import ru.ztrixdev.projects.passhavenapp.ui.theme.AppThemeType;

public class ThemePrefs {
    private static final String PREFS_NAME = "app_theme_prefs";
    private static final String PREF_THEME_KEY = "selected_theme";
    private static final String PREF_DARK_BOOL_KEY = "dark_theme";

    public static void saveSelectedTheme(Context context, AppThemeType theme) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PREF_THEME_KEY, theme.name());
        editor.apply();
    }

    public static AppThemeType getSelectedTheme(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String themeName = sharedPreferences.getString(PREF_THEME_KEY, AppThemeType.W10.name());
        return AppThemeType.valueOf(themeName);
    }

    public static void saveDarkThemeBool(Context context, Boolean val) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_DARK_BOOL_KEY, val);
        editor.apply();
    }

    public static Boolean getDarkThemeBool(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_DARK_BOOL_KEY, false);
    }
}
