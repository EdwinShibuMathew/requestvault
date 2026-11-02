package com.application.requestvault;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Testcontainers
@SpringBootTest
@Transactional
class DataRequestRepositoryTests {

    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DataRequestRepository repository;

    @Test
    void createdRequestCanBeRetrievedById() {
        CreateDataRequest input = new CreateDataRequest(
                "customer-003",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        DataRequest created = repository.create(input);

        Optional<DataRequest> retrieved =
                repository.findById(created.id());

        assertEquals(Optional.of(created), retrieved);
    }

    @Test
    void createdRequestsHaveDifferentIds() {
        CreateDataRequest firstInput = new CreateDataRequest(
                "customer-004",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );


        DataRequest firstCreated = repository.create(firstInput);
        DataRequest secondCreated = repository.create(firstInput);

        assertNotEquals(firstCreated.id(), secondCreated.id());
    }

    @Test
    void reversedDatesAreRejectedByDatabase() {
        CreateDataRequest input = new CreateDataRequest(
                "customer-004",
                LocalDate.of(2026, 10, 31),
                LocalDate.of(2026, 10, 1)
        );

        assertThrows(
                DataIntegrityViolationException.class,
                () -> repository.create(input)
        );
    }
}