package com.application.requestvault;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DataRequestApiTests {

    @LocalServerPort
    private int port;

    @Test
    void blankCustomerIdReturnsBadRequest() throws Exception {

        String body = """
            {       
                "customerId": "",
                "fromDate": "2026-10-01",
                "toDate": "2026-10-15"                
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/requests"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(400, response.statusCode());

            assertTrue(
                    response.body().contains("customerId must not be blank"),
                    "The response should explain that the customer ID is blank"
            );
        }
    }

    @Test
    void reversedDatesReturnBadRequest() throws Exception {
        String body = """
            {       
                "customerId": "customer-004",
                "fromDate": "2026-10-31",
                "toDate": "2026-10-15"                
            }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/requests"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(400, response.statusCode());

            assertTrue(
                    response.body().contains("toDate must be on or after fromDate"),
                    "The response should explain that the toDate must be on or after fromDate"
            );
        }
    }
}
