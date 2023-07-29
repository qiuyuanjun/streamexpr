package com.qiuyj.streamexpr.api.utils;

import java.util.Objects;

/**
 * @author qiuyj
 * @since 2023-07-29
 */
public abstract class StringUtils {

    private StringUtils() { /* for private */ }

    public static boolean isNotEmpty(String str) {
        return Objects.nonNull(str) && !str.isEmpty();
    }
}
