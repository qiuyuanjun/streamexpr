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

    public static boolean isEmpty(String str) {
        return Objects.isNull(str) || str.isEmpty();
    }

    public static String[] split(Object obj, String separator) {
        String toBeSplitString;
        if (Objects.nonNull(obj) && isNotEmpty(toBeSplitString = obj.toString())) {
            return isEmpty(separator)
                    ? new String[] { toBeSplitString }
                    : toBeSplitString.split(separator);
        }
        return null;
    }
}
