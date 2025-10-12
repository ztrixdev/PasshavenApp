package ru.ztrixdev.projects.passhavenapp;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateTimeProcessor {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm")
                    .withZone(ZoneId.systemDefault())
                    .withLocale(Locale.getDefault());
    public String convertToHumanReadable(long currentTimeMillis) {
        Instant instant = Instant.ofEpochMilli(currentTimeMillis);

        return FORMATTER.format(instant);
    }
}
