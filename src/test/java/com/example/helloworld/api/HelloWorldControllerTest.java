package com.example.helloworld.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.helloworld.service.GreetingService;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Web-layer tests: verifies HTTP status codes, JSON bodies and parameter handling.
 * The real {@link GreetingService} is used since it is a pure, dependency-free component.
 */
@WebMvcTest(HelloWorldController.class)
@Import(GreetingService.class)
class HelloWorldControllerTest {

    private static final String INVALID_INPUT_JSON = "{\"error\":\"Invalid Input\"}";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void returns200WithGreetingForNameInFirstHalfOfAlphabet() throws Exception {
        mockMvc.perform(get("/hello-world").param("name", "alice"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"message\":\"Hello Alice\"}", true));
    }

    @Test
    void returnsGreetingForCommaContainingNameWithoutSplittingIt() throws Exception {
        mockMvc.perform(get("/hello-world").param("name", "doe,jane"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Hello Doe,jane\"}", true));
    }

    @Test
    void decodesUrlEncodedName() throws Exception {
        mockMvc.perform(get(URI.create("/hello-world?name=%20alice%20")))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Hello Alice\"}", true));
    }

    @ParameterizedTest
    @ValueSource(strings = {"nancy", "Zoe", "n", "z"})
    void returns400ForNameInSecondHalfOfAlphabet(String name) throws Exception {
        expectInvalidInput(mockMvc.perform(get("/hello-world").param("name", name)));
    }

    @Test
    void returns400WhenNameParameterIsMissing() throws Exception {
        expectInvalidInput(mockMvc.perform(get("/hello-world")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/hello-world?name=", "/hello-world?name", "/hello-world?name=%20%20"})
    void returns400WhenNameIsEmptyOrBlank(String uri) throws Exception {
        expectInvalidInput(mockMvc.perform(get(URI.create(uri))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1alice", "_alice", "Ángel"})
    void returns400WhenNameDoesNotStartWithAnEnglishLetter(String name) throws Exception {
        expectInvalidInput(mockMvc.perform(get("/hello-world").param("name", name)));
    }

    @Test
    void returns400WhenNameParameterIsRepeated() throws Exception {
        expectInvalidInput(mockMvc.perform(get("/hello-world").param("name", "alice", "bob")));
    }

    @Test
    void ignoresUnrelatedQueryParameters() throws Exception {
        mockMvc.perform(get("/hello-world").param("name", "alice").param("lang", "en"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Hello Alice\"}", true));
    }

    @Test
    void parameterNameIsCaseSensitive() throws Exception {
        expectInvalidInput(mockMvc.perform(get("/hello-world").param("NAME", "alice")));
    }

    @Test
    void rejectsNonGetMethods() throws Exception {
        mockMvc.perform(post("/hello-world").param("name", "alice"))
                .andExpect(status().isMethodNotAllowed());
    }

    private static void expectInvalidInput(ResultActions result) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(INVALID_INPUT_JSON, true));
    }
}
