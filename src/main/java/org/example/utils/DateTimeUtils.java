package org.example.utils;

import java.time.Duration;

public class DateTimeUtils {
    public static String getReadableDuration(long startTime, long endTime) {
        if (endTime < startTime) {
            throw new IllegalArgumentException("End time must be greater than or equal to start time");
        }

        // Convert nanoseconds to milliseconds
        long elapsedMillis = (endTime - startTime) / 1_000_000;
        Duration duration = Duration.ofMillis(elapsedMillis);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        long millis = duration.toMillisPart();

        StringBuilder result = new StringBuilder();
        if (hours > 0) result.append(hours).append("h ");
        if (minutes > 0) result.append(minutes).append("m ");
        if (seconds > 0) result.append(seconds).append("s ");
        if (millis > 0 || result.isEmpty()) result.append(millis).append("ms");

        return result.toString().trim();
    }
}
