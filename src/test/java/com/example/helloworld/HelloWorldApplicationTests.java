package com.example.helloworld;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/** End-to-end test: boots the full application on a random port and calls it over real HTTP. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HelloWorldApplicationTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void greetsValidName() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello-world?name=alice", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(response.getBody()).isEqualTo("{\"message\":\"Hello Alice\"}");
    }

    @Test
    void rejectsNameInSecondHalfOfAlphabet() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello-world?name=zoe", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Invalid Input\"}");
    }

    @Test
    void rejectsMissingName() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello-world", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("{\"error\":\"Invalid Input\"}");
    }
}
