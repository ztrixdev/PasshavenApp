package ru.ztrixdev.projects.passhavenapp.Preferences;

import android.content.Context;
import android.net.Uri;

import ru.ztrixdev.projects.passhavenapp.TimeInMillis;

public class VaultPrefs {
    private static final String PREFS_NAME = "vault_prefs";

    // Failed Login Attempts Before Suicide
    private static final String PREFS_FLABS_KEY = "flabs";
    // Failed Login Attempts Before Suicide Remaining
    private static final String PREFS_FLABSR_KEY = "flabsr";

    private static final String PREFS_BACKUP_FOLDER_KEY = "backup_folder";
    private static final String PREFS_BACKUP_EVERY_KEY = "backup_every";
    private static final String PREFS_LAST_BACKUP_TIMESTAMP_KEY = "last_backup_timestamp";

    private static final int DEFAULT_FLABS = 20;
    private static final int DEFAULT_FLABSR = DEFAULT_FLABS;
    private static final long DEFAULT_BACKUP_EVERY = TimeInMillis.ThreeDays;


    public static void saveFlabs(Context context, int value) {
        PreferencesMaster.put(PREFS_NAME, PREFS_FLABS_KEY, value, context);
    }

    public static int getFlabs(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREFS_FLABS_KEY, 0, context);
    }

    public static void saveFlabsr(Context context, int value) {
        PreferencesMaster.put(PREFS_NAME, PREFS_FLABSR_KEY, value, context);
    }

    public static int getFlabsr(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREFS_FLABSR_KEY, 0, context);
    }

    public static void saveBackupFolder(Context context, Uri uri) {
        PreferencesMaster.put(PREFS_NAME, PREFS_BACKUP_FOLDER_KEY, uri.toString(), context);
    }

    public static Uri getBackupFolder(Context context) {
        String uristr = PreferencesMaster.get(PREFS_NAME, PREFS_BACKUP_FOLDER_KEY, "", context);
        return Uri.parse(uristr);
    }

    public static void saveBackupEvery(Context context, long value) {
        PreferencesMaster.put(PREFS_NAME, PREFS_BACKUP_EVERY_KEY, value, context);
    }

    public static long getBackupEvery(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREFS_BACKUP_EVERY_KEY, 0L, context);
    }
    public static void saveLastBackupTimestamp(Context context, long timestamp) {
        PreferencesMaster.put(PREFS_NAME, PREFS_LAST_BACKUP_TIMESTAMP_KEY, timestamp, context);
    }

    public static long getLastBackupTimestamp(Context context) {
        // Defaulting to 0L, indicating no backup has been made yet.
        return PreferencesMaster.get(PREFS_NAME, PREFS_LAST_BACKUP_TIMESTAMP_KEY, 0L, context);
    }
}
