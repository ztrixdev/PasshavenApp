package ru.ztrixdev.projects.passhavenapp.Preferences;

import android.content.Context;

public class SessionPrefs {

    private static final String PREFS_NAME = "session_prefs";

    private static final String PREFS_LAST_LOGIN_TIMESTAMP_KEY = "last_login_timestamp";

    private static final long DEFAULT_LAST_LOGIN_TIMESTAMP = 0L;

    public static void saveLastLoginTimestamp(Context context, long timestamp) {
        PreferencesMaster.put(PREFS_NAME, PREFS_LAST_LOGIN_TIMESTAMP_KEY, timestamp, context);
    }

    public static long getLastLoginTimestamp(Context context) {
        return PreferencesMaster.get(PREFS_NAME, PREFS_LAST_LOGIN_TIMESTAMP_KEY, DEFAULT_LAST_LOGIN_TIMESTAMP, context);
    }
}
