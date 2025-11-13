package ru.ztrixdev.projects.passhavenapp;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeProcessor {
    // Human readable formatter
    private static final DateTimeFormatter HRFORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")
                    .withZone(ZoneId.systemDefault())
                    .withLocale(Locale.getDefault());

    public static String convertToHumanReadable(long currentTimeMillis) {
        Instant instant = Instant.ofEpochMilli(currentTimeMillis);
        return HRFORMATTER.format(instant);
    }

    // Filename formatter
    private static final DateTimeFormatter FNFORMATTER =
            DateTimeFormatter.ofPattern("MM_dd_yyyy_HH_mm_ss")
                    .withZone(ZoneId.systemDefault());

    public static String convertForFIlename(long currentTimeMillis) {
        Instant instant = Instant.ofEpochMilli(currentTimeMillis);
        return FNFORMATTER.format(instant);
    }
}
