package ru.ztrixdev.projects.passhavenapp.handlers;

import android.content.Context;

import ru.ztrixdev.projects.passhavenapp.preferences.SessionPrefs;
import ru.ztrixdev.projects.passhavenapp.TimeInMillis;

public class SessionHandler {
    public static boolean isSessionExpired(Context context) {
        return System.currentTimeMillis() - SessionPrefs.getLastLoginTimestamp(context)
                > TimeInMillis.FortyFiveMinutes;
    }
}
