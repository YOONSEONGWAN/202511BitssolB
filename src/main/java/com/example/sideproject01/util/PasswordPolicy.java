package com.example.sideproject01.util;

import java.util.regex.Pattern;

public final class PasswordPolicy {
    private PasswordPolicy() {}

    // 8~16, 영문 1+, 숫자 1+, 특수문자 1+
    private static final Pattern P =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,16}$");

    public static boolean isValid(String raw) {
        return raw != null && P.matcher(raw).matches();
    }
}
