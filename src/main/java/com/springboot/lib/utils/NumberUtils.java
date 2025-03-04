package com.springboot.lib.utils;

public class NumberUtils {
    public static Long tryToGetLong(String numberStr){
        return tryToGetLong(numberStr, 0L);
    }

    public static Long tryToGetLong(String numberStr, Long defaultValue){
        try {
            return Long.parseLong(numberStr);
        } catch (Exception ignored){
        }
        return defaultValue;
    }

    public static Integer  tryToGetInteger(String numberStr){
        return tryToGetInteger(numberStr, 0);
    }

    public static Integer tryToGetInteger(long numberLong){
        return tryToGetInteger(numberLong, 0);
    }

    public static Integer tryToGetInteger(String numberStr, Integer defaultValue){
        try {
            return Integer.parseInt(numberStr);
        } catch (Exception ignored){
        }
        return defaultValue;
    }

    public static Integer tryToGetInteger(long numberLong, Integer defaultValue){
        try {
            return Integer.parseInt(String.valueOf(numberLong));
        } catch (Exception ignored){
        }
        return defaultValue;
    }
}
