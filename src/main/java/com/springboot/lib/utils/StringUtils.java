package com.springboot.lib.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Map<Pattern, String> accentMap = new HashMap<>();
    private static final Set<Character> SPECIAL_CHARACTERS = new HashSet<>(Set.of(
            '~', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+',
            '{', '}', '<', '>', '?', '/', '[', ']', ',', '.', ';', ':', '\'', '\"', '-',
            '|'
    ));

    public static boolean isNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static String sanitizeName(String name) {
        StringBuilder sb = new StringBuilder();

        for (char c : name.toCharArray()) {
            if (!SPECIAL_CHARACTERS.contains(c)) {
                sb.append(c);
            }
        }

        // Giữ chỉ một khoảng trắng giữa các từ
        return sb.toString().replaceAll("\\s{2,}", " ")
                .trim();
    }

    public static String removeAccentsManual(String input) {
        String result = input;
        for (Map.Entry<Pattern, String> entry : accentMap.entrySet()) {
            Matcher matcher = entry.getKey().matcher(result);
            result = matcher.replaceAll(entry.getValue());
        }
        return result;
    }


    public static String removeAccentsNormalized(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "");
    }

    static {
        accentMap.put(Pattern.compile("[àáạảãâầấậẩẫăằắặẳẵÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴå]"), "a");
        accentMap.put(Pattern.compile("[èéẹẻẽêềếệểễÈÉẸẺẼÊỀẾỆỂỄë]"), "e");
        accentMap.put(Pattern.compile("[ìíịỉĩÌÍỊỈĨî]"), "i");
        accentMap.put(Pattern.compile("[òóọỏõôồốộổỗơờớợởỡÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠø]"), "o");
        accentMap.put(Pattern.compile("[ùúụủũưừứựửữÙÚỤỦŨƯỪỨỰỬỮůû]"), "u");
        accentMap.put(Pattern.compile("[ỳýỵỷỹỲÝỴỶỸ]"), "y");
        accentMap.put(Pattern.compile("[đĐ]"), "d");
        accentMap.put(Pattern.compile("ç"), "c");
        accentMap.put(Pattern.compile("ñ"), "n");
        accentMap.put(Pattern.compile("[äæ]"), "ae");
        accentMap.put(Pattern.compile("ö"), "oe");
        accentMap.put(Pattern.compile("ü"), "ue");
        accentMap.put(Pattern.compile("Ä"), "Ae");
        accentMap.put(Pattern.compile("Ü"), "Ue");
        accentMap.put(Pattern.compile("Ö"), "Oe");
        accentMap.put(Pattern.compile("ß"), "ss");
    }
}
