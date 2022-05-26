package com.haybble.access.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AccessLimitUtils {
    public  final String datePattern = "yyyy-MM-dd HH:mm:ss";
    public LocalDateTime getEndDateTime(String startDate, String duration) {
        return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(datePattern)).plusHours(getDurationInHours(duration));
    }
    public  int getDurationInHours(String duration) {
        return (duration == "hourly") ? 1 : 24;
    }

    public  String formatStartDateTime(String startDate) {
        return startDate.replace(".", " ");
    }

}
