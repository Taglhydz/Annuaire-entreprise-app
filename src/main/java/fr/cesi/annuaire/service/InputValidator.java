package fr.cesi.annuaire.service;

import java.util.Locale;
import java.util.regex.Pattern;

public final class InputValidator {

    private static final Pattern PERSON_NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ][A-Za-zÀ-ÿ' -]*$");
    private static final Pattern CITY_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ][A-Za-zÀ-ÿ' -]*$");
    private static final Pattern DEPARTMENT_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ0-9][A-Za-zÀ-ÿ0-9' -]*$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private InputValidator() {
    }

    public static String validatePersonName(String value, String label) {
        String normalized = normalizeRequiredText(value, label);
        if (!PERSON_NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(label + " invalide");
        }
        return normalized;
    }

    public static String validateCity(String value) {
        String normalized = normalizeRequiredText(value, "Ville");
        if (!CITY_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Ville invalide");
        }
        return normalized;
    }

    public static String validateDepartmentName(String value) {
        String normalized = normalizeRequiredText(value, "Service");
        if (!DEPARTMENT_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Service invalide");
        }
        return normalized;
    }

    public static String normalizeEmail(String value) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            return null;
        }
        if (!EMAIL_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("Email invalide");
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    public static String normalizeFrenchPhone(String value, String label) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            return null;
        }

        String compact = normalized.replace(" ", "");
        if (!compact.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException(label + " invalide (10 chiffres, espaces autorises)");
        }
        return compact;
    }

    private static String normalizeRequiredText(String value, String label) {
        String normalized = normalizeOptionalText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(label + " est obligatoire");
        }
        return normalized;
    }

    private static String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isEmpty() ? null : normalized;
    }
}
