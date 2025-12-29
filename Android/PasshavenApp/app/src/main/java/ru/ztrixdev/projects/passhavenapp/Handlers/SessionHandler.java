package ru.ztrixdev.projects.passhavenapp.Handlers;

import android.content.Context;
import android.content.Intent;

import ru.ztrixdev.projects.passhavenapp.Activities.LoginActivity;
import ru.ztrixdev.projects.passhavenapp.Preferences.SessionPrefs;
import ru.ztrixdev.projects.passhavenapp.TimeInMillis;

public class SessionHandler {
    public static boolean isSessionExpired(Context context) {
        return System.currentTimeMillis() - SessionPrefs.getLastLoginTimestamp(context)
                > TimeInMillis.FortyFiveMinutes;
    }
}
