package com.akexorcist.myapplication.utility;

/**
 * Created by Akexorcist on 6/24/2016 AD.
 */

public class Utility {
    public static boolean isMessageValidated(String message) {
        return !(message == null || message.isEmpty());
    }

    public static boolean isUsernameAndPasswordEmpty(String username, String password) {
        return username.isEmpty() || password.isEmpty();
    }

    public static boolean isUsernameAndPasswordLessThan6Charactor(String username, String password) {
        return username.length() < 6 || password.length() < 6;
    }
}
