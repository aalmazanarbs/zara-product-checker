package com.gade.zaraproductchecker.util;

public final class StringUtil {

    public static boolean isEmpty(final String s) {
        return s == null || s.isEmpty();
    }

    public static boolean isNotEmpty(final String s) {
        return !isEmpty(s);
    }
}
