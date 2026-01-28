package ru.ztrixdev.projects.passhavenapp.preferences;

import android.content.Context;

import ru.ztrixdev.projects.passhavenapp.ui.theme.AppThemeType;

public class ThemePrefs {
    private static final String PREFS_NAME = "app_theme_prefs";
    private static final String PREF_THEME_KEY = "selected_theme";
    private static final String PREF_DARK_BOOL_KEY = "dark_theme";
    private static final String PREF_DYNAMIC_COLORS_BOOL_KEY = "use_dynamic_colors";

    public static void saveSelectedTheme(Context context, AppThemeType theme) {
        PreferencesMaster.put(PREFS_NAME, PREF_THEME_KEY, theme.name(), context);
    }

    public static AppThemeType getSelectedTheme(Context context) {
        String themeName = PreferencesMaster.get(PREFS_NAME, PREF_THEME_KEY, AppThemeType.Monochroma.name(), context);
        return AppThemeType.valueOf(themeName);
    }

    public static void saveDarkThemeBool(Context context, Boolean val) {
        PreferencesMaster.put(PREFS_NAME, PREF_DARK_BOOL_KEY, val, context);
    }

    public static Boolean getDarkThemeBool(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREF_DARK_BOOL_KEY, false, context);
    }

    public static void saveDynamicColorsBool(Context context, Boolean val) {
        PreferencesMaster.put(PREFS_NAME, PREF_DYNAMIC_COLORS_BOOL_KEY, val, context);
    }

    public static Boolean getDynamicColorsBool(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREF_DYNAMIC_COLORS_BOOL_KEY, false, context);
    }

}
