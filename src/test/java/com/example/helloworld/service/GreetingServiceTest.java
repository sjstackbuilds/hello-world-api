package com.example.helloworld.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class GreetingServiceTest {

    private final GreetingService service = new GreetingService();

    // ---- valid names ------------------------------------------------------

    @Test
    void greetsWithCapitalisedName() {
        assertThat(service.greet("alice")).isEqualTo("Hello Alice");
    }

    @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
    @CsvSource(delimiter = '|', value = {
            "a      | Hello A",       // lower boundary, single letter
            "m      | Hello M",       // upper boundary of first half
            "A      | Hello A",
            "M      | Hello M",
            "Alice  | Hello Alice",   // already capitalised
            "ALICE  | Hello ALICE",   // rest of the name is preserved as given
            "mcDonald | Hello McDonald",
            "'jean-luc' | Hello Jean-luc",
            "'Doe, Jane' | Hello Doe, Jane",
            "'ab1'  | Hello Ab1"
    })
    void acceptsNamesStartingWithAThroughM(String name, String expected) {
        assertThat(service.greet(name)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"  alice", "alice  ", "\talice\n", "\u2003alice"})
    void trimsSurroundingWhitespace(String name) {
        assertThat(service.greet(name)).isEqualTo("Hello Alice");
    }

    // ---- invalid names ----------------------------------------------------

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = {"n", "N", "z", "Z", "nancy", "Zoe", "oscar"})
    void rejectsNamesStartingWithNThroughZ(String name) {
        assertRejected(name);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n", "\u2003"}) // em space
    void rejectsMissingOrBlankNames(String name) {
        assertRejected(name);
    }

    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = {
            "1alice",   // digit
            "_alice",   // underscore
            "-alice",   // punctuation
            "@",        // symbol only
            "Ángel",    // accented letter outside the English alphabet
            "İlker",    // Turkish dotted capital I (lower-cases to ASCII 'i')
            "Алиса",    // Cyrillic
            "爱丽丝",     // CJK
            "\uD83D\uDE00alice" // emoji (surrogate pair)
    })
    void rejectsNamesNotStartingWithAnEnglishLetter(String name) {
        assertRejected(name);
    }

    private void assertRejected(String name) {
        assertThatThrownBy(() -> service.greet(name)).isInstanceOf(InvalidNameException.class);
    }
}
