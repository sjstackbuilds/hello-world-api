package com.example.helloworld.service;

/**
 * Thrown when a supplied name does not satisfy the greeting rules.
 *
 * <p>The message is intended for logs only; it is never exposed to API clients,
 * who always receive the generic {@code "Invalid Input"} error.
 */
public class InvalidNameException extends RuntimeException {

    public InvalidNameException(String message) {
        super(message);
    }
}
