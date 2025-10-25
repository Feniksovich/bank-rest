package com.feniksovich.bankcards.util;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Утилиты для работы с банковскими картами (генерация PAN по алгоритму Луна).
 */
public class CardUtil {

    private static final ThreadLocalRandom LOCAL_RANDOM = ThreadLocalRandom.current();

    /**
     * Генерирует валидный 16-значный PAN с контрольной суммой Луна.
     *
     * @return строка PAN
     */
    public static String generateCardPan() {
        final int[] digits = new int[16];
        final int controlNumIndex = 15;

        for (int i = 0; i < 15; i++) {
            digits[i] = LOCAL_RANDOM.nextInt(10);
        }

        int sum = 0;
        for (int i = 14; i >= 0; i--) {
            int digit = digits[i];
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }

        digits[controlNumIndex] = (10 - (sum % 10)) % 10;

        final StringBuilder cardNumber = new StringBuilder();
        for (final int digit : digits) {
            cardNumber.append(digit);
        }

        return cardNumber.toString();
    }
}
