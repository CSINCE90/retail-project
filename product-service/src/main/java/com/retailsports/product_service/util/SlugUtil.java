package com.retailsports.product_service.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class per generare slug URL-friendly da stringhe
 */
public class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");

    /**
     * Genera uno slug URL-friendly da un testo
     * Esempio: "Nike Air Max 2024" -> "nike-air-max-2024"
     */
    public static String generateSlug(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        String noWhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhitespace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        slug = EDGES_DASHES.matcher(slug).replaceAll("");

        return slug.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Genera uno slug unico aggiungendo un suffisso numerico se necessario
     */
    public static String generateUniqueSlug(String input, int suffix) {
        String baseSlug = generateSlug(input);
        return suffix == 0 ? baseSlug : baseSlug + "-" + suffix;
    }
}
