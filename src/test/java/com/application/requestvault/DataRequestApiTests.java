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

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DataRequestApiTests {

    @Autowired
    private DataRequestRepository repository;

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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

    @Test
    void validRequestIsAccepted() throws Exception {
        String body = """
        {
            "customerId": "customer-005",
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

            assertEquals(200, response.statusCode());
        }
    }

    @Test
    void approvingMissingRequestReturnsNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/requests/-1/approve"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(404, response.statusCode());
            assertTrue(
                    response.body().contains("Request -1 was not found"),
                    "The response should explain that the request is missing"
            );
        }
    }

    @Test
    void pendingRequestCanBeApproved() throws Exception {
        DataRequest created = repository.create(new CreateDataRequest(
                "customer-approval-test",
                java.time.LocalDate.of(2026, 10, 1),
                java.time.LocalDate.of(2026, 10, 15)
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:" + port
                                + "/requests/" + created.id() + "/approve"
                ))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            assertEquals(200, response.statusCode());

            DataRequest stored = repository.findById(created.id())
                    .orElseThrow();

            assertEquals(RequestStatus.APPROVED, stored.status());
        }
    }

    @Test
    void approvingAlreadyApprovedRequestReturnsConflict() throws Exception {
        DataRequest created = repository.create(new CreateDataRequest(
                "customer-approval-test-already-approved",
                java.time.LocalDate.of(2026, 10, 1),
                java.time.LocalDate.of(2026, 10, 15)
        ));

        boolean updated = repository.updateStatusIfPending(
                created.id(),
                RequestStatus.APPROVED
        );

        assertTrue(updated);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/requests/" + created.id()+ "/approve"))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );


            assertEquals(409, response.statusCode());
            assertTrue(
                    response.body().contains("has already been approved or rejected")
            );

        }

        DataRequest stored = repository.findById(created.id())
                .orElseThrow();

        assertEquals(RequestStatus.APPROVED, stored.status());
    }
}
