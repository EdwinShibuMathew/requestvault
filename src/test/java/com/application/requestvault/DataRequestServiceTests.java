package com.application.requestvault;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class DataRequestServiceTests {

    @Test
    void requestLookupReturnsRepositoryResult() {
        DataRequestRepository repository = mock(DataRequestRepository.class);
        DataRequestService service = new DataRequestService(repository);

        DataRequest expected = new DataRequest(
                42L,
                "customer-003",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                RequestStatus.PENDING
        );

        when(repository.findById(42L))
                .thenReturn(Optional.of(expected));

        Optional<DataRequest> actual = service.getRequestById(42L);

        assertEquals(Optional.of(expected), actual);
        verify(repository).findById(42L);
    }

    @Test
    void unknownRequestIdReturnsEmpty() {
        DataRequestRepository repository = mock(DataRequestRepository.class);
        DataRequestService service = new DataRequestService(repository);

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertTrue(service.getRequestById(999L).isEmpty());

        verify(repository).findById(999L);
    }

    @Test
    void createRequestReturnsRepositoryResult() {
        DataRequestRepository repository = mock(DataRequestRepository.class);
        DataRequestService service = new DataRequestService(repository);

        CreateDataRequest input = new CreateDataRequest(
                "customer-003",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15)
        );

        DataRequest expected = new DataRequest(
                42L,
                input.customerId(),
                input.fromDate(),
                input.toDate(),
                RequestStatus.PENDING
        );

        when(repository.create(input)).thenReturn(expected);

        DataRequest actual = service.createRequest(input);

        assertEquals(expected, actual);
        verify(repository).create(input);
    }
}
