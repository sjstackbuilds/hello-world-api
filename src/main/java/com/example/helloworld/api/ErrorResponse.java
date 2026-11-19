package com.example.helloworld.api;

/** Error response body: {@code { "error": "Invalid Input" }}. */
public record ErrorResponse(String error) {

    public static final String INVALID_INPUT = "Invalid Input";

    public static ErrorResponse invalidInput() {
        return new ErrorResponse(INVALID_INPUT);
    }
}
