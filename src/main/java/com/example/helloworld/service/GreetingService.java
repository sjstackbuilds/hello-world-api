package com.example.helloworld.service;

import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * Business rules for greeting a person by name.
 *
 * <p>A name is accepted only when, after trimming surrounding whitespace, it is non-empty and
 * its first character is an English letter in the first half of the alphabet (A–M, either case).
 */
@Service
public class GreetingService {

    private static final String GREETING_PREFIX = "Hello ";

    /**
     * Builds a greeting for the given name.
     *
     * @param name the raw name supplied by the caller; may be {@code null}
     * @return the greeting, e.g. {@code "Hello Alice"} for {@code "alice"}
     * @throws InvalidNameException if the name is missing, blank, or does not start with A–M
     */
    public String greet(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidNameException("Name is missing or blank");
        }

        String trimmed = name.strip();
        char first = trimmed.charAt(0);
        if (!isInFirstHalfOfAlphabet(first)) {
            throw new InvalidNameException("Name must start with a letter between A and M");
        }

        return GREETING_PREFIX + capitalize(trimmed);
    }

    /**
     * Checks the raw character against explicit ASCII ranges. We deliberately avoid
     * {@link Character#toLowerCase(char)} here: some non-English letters (e.g. Turkish 'İ')
     * lower-case to ASCII letters and would otherwise be accepted by mistake.
     */
    private static boolean isInFirstHalfOfAlphabet(char c) {
        return (c >= 'A' && c <= 'M') || (c >= 'a' && c <= 'm');
    }

    /** Upper-cases the first letter and keeps the rest as given (so "McDonald" stays intact). */
    private static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}
