package com.acat.firebase.rtdbbe.utils;

public class StringUtils {

    public static String getString(int value) {
        if (value==0) {
            return "";
        }
        else {
            return String.valueOf(value);
        }
    }
}
