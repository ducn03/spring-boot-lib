package com.springboot.lib.utils;

import java.util.Random;

public class OtpUtils {
    private static final Random random = new Random();

    public static String generate() {

        int randomNumber = random.nextInt(1000000);

        return format(randomNumber);
    }

    private static String format(int randomNumber) {
        if (randomNumber < 10) {
            return "00000" + randomNumber;
        }
        if (randomNumber < 100) {
            return "0000" + randomNumber;
        }
        if (randomNumber < 1000) {
            return "000" + randomNumber;
        }
        if (randomNumber < 10000) {
            return "00" + randomNumber;
        }
        if (randomNumber < 100000) {
            return "0" + randomNumber;
        }
        return "" + randomNumber;
    }
}
