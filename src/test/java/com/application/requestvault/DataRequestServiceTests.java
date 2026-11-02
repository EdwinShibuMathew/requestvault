package com.application.requestvault;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class DataRequestServiceTests {

    @Test
    void createdRequestCanBeRetrievedById() {
        DataRequestService service = new DataRequestService();

        CreateDataRequest input = new CreateDataRequest(
                "customer-003",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        DataRequest created = service.createRequest(input);

        DataRequest retrieved = service.getRequestById(created.id())
                .orElseThrow();

        assertEquals(created, retrieved);
    }

    @Test
    void unknownRequestIdReturnsEmpty() {
        DataRequestService service = new DataRequestService();

        assertTrue(service.getRequestById(999L).isEmpty());
    }

    @Test
    void createdRequestHaveDifferentIds() {
        DataRequestService service = new DataRequestService();

        CreateDataRequest input = new CreateDataRequest(
                "customer-003",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        DataRequest createdFirst = service.createRequest(input);
        DataRequest createdSecond = service.createRequest(input);


        assertNotEquals(createdFirst.id(), createdSecond.id());

    }
}
