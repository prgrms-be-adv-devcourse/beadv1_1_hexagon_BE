package com.example.contractservice.common.util;

import org.slf4j.helpers.MessageFormatter;

public abstract class StringUtil {

    private StringUtil() {
    }

    public static String format(String format, Object... objects) {
        return MessageFormatter.arrayFormat(format, objects).getMessage();
    }
}
