package com.khouss.UsersMicroservice.utils;

import org.springframework.util.StringUtils;

/**
 * Utility methods for phone number normalization.
 */
public final class PhoneNumberUtils {

    private PhoneNumberUtils() {}

    /**
     * Normalise un numéro pour qu'il soit au format +221XXXXXXXXX (9 chiffres après le préfixe)
     * Stratégie : garder les 9 derniers digits du numéro fourni et préfixer par +221.
     * Retourne null si le numéro fourni est trop court.
     */
    public static String normalizeToSenegalFormat(String phone) {
        if (phone == null) return null;
        String digits = phone.replaceAll("\\D", "");
        if (!StringUtils.hasLength(digits) || digits.length() < 7) {
            return null;
        }
        String last9 = digits.length() <= 9 ? digits : digits.substring(digits.length() - 9);
        if (last9.length() < 9) {
            last9 = String.format("%09d", Long.parseLong(last9));
        }
        return "+221" + last9;
    }
}

