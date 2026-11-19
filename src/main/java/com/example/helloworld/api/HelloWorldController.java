package com.example.helloworld.api;

import com.example.helloworld.service.GreetingService;
import com.example.helloworld.service.InvalidNameException;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    static final String NAME_PARAM = "name";

    private final GreetingService greetingService;

    public HelloWorldController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * {@code GET /hello-world?name=alice} → {@code 200 { "message": "Hello Alice" }}.
     *
     * <p>Query parameters are read as raw values rather than bound to {@code String}/{@code List}:
     * Spring would otherwise join repeated parameters ({@code ?name=a&name=b} → "a,b") or split
     * a single value on commas. An ambiguous, repeated {@code name} is rejected instead.
     * All validation failures are translated to a 400 by {@link ApiExceptionHandler}.
     */
    @GetMapping(path = "/hello-world", produces = MediaType.APPLICATION_JSON_VALUE)
    public GreetingResponse helloWorld(@RequestParam MultiValueMap<String, String> queryParams) {
        return new GreetingResponse(greetingService.greet(singleName(queryParams.get(NAME_PARAM))));
    }

    private static String singleName(List<String> values) {
        if (values == null || values.isEmpty()) {
            return null;
        }
        if (values.size() > 1) {
            throw new InvalidNameException(
                    "Expected exactly one '" + NAME_PARAM + "' parameter but got " + values.size());
        }
        return values.get(0);
    }
}
