package com.feniksovich.bankcards.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TransliterationUtil {

    private static final Map<Character, String> TRANSLITERATION_MAP = new HashMap<>();

    static {
        TRANSLITERATION_MAP.put('А', "A");
        TRANSLITERATION_MAP.put('Б', "B");
        TRANSLITERATION_MAP.put('В', "V");
        TRANSLITERATION_MAP.put('Г', "G");
        TRANSLITERATION_MAP.put('Д', "D");
        TRANSLITERATION_MAP.put('Е', "E");
        TRANSLITERATION_MAP.put('Ё', "E");
        TRANSLITERATION_MAP.put('Ж', "ZH");
        TRANSLITERATION_MAP.put('З', "Z");
        TRANSLITERATION_MAP.put('И', "I");
        TRANSLITERATION_MAP.put('Й', "Y");
        TRANSLITERATION_MAP.put('К', "K");
        TRANSLITERATION_MAP.put('Л', "L");
        TRANSLITERATION_MAP.put('М', "M");
        TRANSLITERATION_MAP.put('Н', "N");
        TRANSLITERATION_MAP.put('О', "O");
        TRANSLITERATION_MAP.put('П', "P");
        TRANSLITERATION_MAP.put('Р', "R");
        TRANSLITERATION_MAP.put('С', "S");
        TRANSLITERATION_MAP.put('Т', "T");
        TRANSLITERATION_MAP.put('У', "U");
        TRANSLITERATION_MAP.put('Ф', "F");
        TRANSLITERATION_MAP.put('Х', "KH");
        TRANSLITERATION_MAP.put('Ц', "TS");
        TRANSLITERATION_MAP.put('Ч', "CH");
        TRANSLITERATION_MAP.put('Ш', "SH");
        TRANSLITERATION_MAP.put('Щ', "SCH");
        TRANSLITERATION_MAP.put('Ы', "Y");
        TRANSLITERATION_MAP.put('Э', "E");
        TRANSLITERATION_MAP.put('Ю', "YU");
        TRANSLITERATION_MAP.put('Я', "YA");
    }

    public static String transliterate(String cyrillicText) {
        Objects.requireNonNull(cyrillicText, "cyrillicText");
        cyrillicText = cyrillicText.trim().toUpperCase();

        final StringBuilder sb = new StringBuilder();
        for (final char c : cyrillicText.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                continue;
            }
            final String replacement = TRANSLITERATION_MAP.get(c);
            if (replacement != null) {
                sb.append(replacement);
            }
        }

        return sb.toString();
    }
}
