package com.wwewe.auth.utils;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailUtils {

    public static boolean isValid(String email) {
        return EmailValidator.getInstance(false, true).isValid(email);
    }

    public static String getName(String email) {
        return email.split("@")[0];
    }
}
