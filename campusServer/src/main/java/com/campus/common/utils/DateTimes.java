package com.campus.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateTimes {

    private static final DateTimeFormatter DEFAULT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private DateTimes() {
    }

    public static String format(LocalDateTime time) {
        return time == null ? null : DEFAULT.format(time);
    }
}

