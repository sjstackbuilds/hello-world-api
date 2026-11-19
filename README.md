# Hello World API

A small Spring Boot web service exposing a single endpoint, `GET /hello-world`, that greets
names starting with **A–M** and rejects everything else.

| Request                            | Status | Body                           |
|------------------------------------|--------|--------------------------------|
| `GET /hello-world?name=alice`      | 200    | `{ "message": "Hello Alice" }` |
| `GET /hello-world?name=zoe`        | 400    | `{ "error": "Invalid Input" }` |
| `GET /hello-world` / `?name=`      | 400    | `{ "error": "Invalid Input" }` |

## Tech stack

- Java 17 (any JDK 17+ works; tested on 17 in CI and locally on 25)
- Spring Boot 3.5 (Spring Web, embedded Tomcat)
- Maven (wrapper included, no local Maven install needed)
- JUnit 5, AssertJ, Spring MockMvc

## Running the application

```bash
./mvnw spring-boot:run
```

Or build and run the executable JAR:

```bash
./mvnw clean package
java -jar target/hello-world-api-1.0.0-SNAPSHOT.jar
```

The server listens on port `8080` by default; override it with the `PORT` environment variable
(`PORT=9090 ./mvnw spring-boot:run`).

```bash
curl -i "http://localhost:8080/hello-world?name=alice"
# HTTP/1.1 200
# {"message":"Hello Alice"}

curl -i "http://localhost:8080/hello-world?name=zoe"
# HTTP/1.1 400
# {"error":"Invalid Input"}
```

On Windows use `mvnw.cmd` instead of `./mvnw`.

## Running the tests

```bash
./mvnw test        # unit + web-layer + end-to-end tests
./mvnw verify      # full build: compile, test, package
```

| Test class                    | Level        | What it covers                                                           |
|-------------------------------|--------------|--------------------------------------------------------------------------|
| `GreetingServiceTest`         | Unit         | Business rule: A–M boundaries, N–Z, null/blank, trimming, non-English and non-letter input, capitalisation |
| `HelloWorldControllerTest`    | Web slice    | HTTP status codes, exact JSON bodies, missing/empty/repeated params, URL decoding, 405 for non-GET |
| `HelloWorldApplicationTests`  | End-to-end   | Full application on a random port, called over real HTTP                 |

## Project structure

```
src/main/java/com/example/helloworld
├── HelloWorldApplication.java      # Spring Boot entry point
├── api/
│   ├── HelloWorldController.java   # HTTP layer: reads the query param, returns DTO
│   ├── ApiExceptionHandler.java    # Maps InvalidNameException -> 400 {"error":"Invalid Input"}
│   ├── GreetingResponse.java       # {"message": ...}
│   └── ErrorResponse.java          # {"error": ...}
└── service/
    ├── GreetingService.java        # Business rules (framework-agnostic logic)
    └── InvalidNameException.java   # Domain exception for rule violations
```

## Assumptions

1. **Whitespace is trimmed.** `?name=%20alice%20` is treated as `alice`. A name that is only
   whitespace counts as empty and returns 400.
2. **Capitalisation.** Only the first letter is upper-cased; the rest is kept exactly as sent
   (`alice` → `Alice`, `mcDonald` → `McDonald`, `ALICE` → `ALICE`). This avoids mangling names
   like "McDonald" or "DeShawn".
3. **English letters only.** The rule is about the first half of the *English* alphabet, so a
   name must start with an ASCII letter `A–M`/`a–m`. Names starting with digits, symbols,
   emoji, accented letters (`Ángel`) or other scripts (`Алиса`) return 400. The check uses
   explicit ASCII ranges rather than `Character.toLowerCase`, because some non-English letters
   (e.g. Turkish `İ`) lower-case to ASCII and would otherwise slip through.
4. **Only the first character is validated.** Characters after the first are not checked, so
   `jean-luc`, `o'brien`-style names and `Doe, Jane` are accepted when they start with A–M.
5. **A repeated parameter is invalid.** `?name=alice&name=bob` is ambiguous and returns 400.
   (Spring's default `String` binding would otherwise join it into `"alice,bob"`.) Commas
   inside a single value are not treated as separators.
6. **The parameter name is case-sensitive** (`?NAME=alice` counts as missing), following
   normal HTTP query conventions. Unknown extra parameters are ignored.
7. **Error details are not exposed.** Every validation failure returns the same generic
   `{"error":"Invalid Input"}` body as the spec requires. The specific reason is logged at
   `DEBUG` level for troubleshooting.
8. **Other HTTP errors keep Spring Boot's defaults.** For example, `POST /hello-world` returns
   405 and unknown paths return 404, using Spring's standard error format, since the spec only
   defines `GET /hello-world`.
9. **No length limit** is applied beyond the servlet container's default URL size limit, since
   the spec doesn't define one.
