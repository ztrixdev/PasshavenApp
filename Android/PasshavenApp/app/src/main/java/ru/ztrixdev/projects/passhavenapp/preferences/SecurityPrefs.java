package ru.ztrixdev.projects.passhavenapp.preferences;

import android.content.Context;

public class SecurityPrefs {
    private static final String PREFS_NAME = "security_prefs";

    private static final String PREFS_LAST_PIN_CHANGE_KEY = "last_pin_change";
    private static final String PREFS_BACKUP_PASSWORD_KEY = "backup_password";

    public static void saveLastPINChange(Long time, Context context) {
        PreferencesMaster.put(PREFS_NAME, PREFS_LAST_PIN_CHANGE_KEY, time, context);
    }

    public static Long getLastPINChange(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREFS_LAST_PIN_CHANGE_KEY, 0L, context);
    }

    public static void saveLastMPChange(Long time, Context context) {
        PreferencesMaster.put(PREFS_NAME, PREFS_LAST_PIN_CHANGE_KEY, time, context);
    }

    public static Long getLastMPChange(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREFS_LAST_PIN_CHANGE_KEY, 0L, context);
    }

    public static void saveBackupPassword(String hexedPassword, Context context) {
        PreferencesMaster.put(PREFS_NAME, PREFS_BACKUP_PASSWORD_KEY, hexedPassword, context);
    }

    public static String getBackupPassword(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREFS_BACKUP_PASSWORD_KEY, "", context);
    }
}
