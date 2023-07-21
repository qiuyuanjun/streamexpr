package com.qiuyj.streamexpr.utils;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2023-07-16
 */
public abstract class ParseUtils {

    private ParseUtils() { /* for private */ }

    public static boolean stringIsNotBlank(String string) {
        return Objects.nonNull(string) && !string.isBlank();
    }
}
