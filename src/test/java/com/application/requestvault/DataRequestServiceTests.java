package com.application.requestvault;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
                "customer-004",
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

    @Test
    void pendingRequestCanBeApproved() {
        DataRequestRepository repository = mock(DataRequestRepository.class);
        DataRequestService service = new DataRequestService(repository);

        DataRequest approved = new DataRequest(
                42L,
                "customer-005",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                RequestStatus.APPROVED
        );

        when(repository.updateStatusIfPending(42L, RequestStatus.APPROVED))
                .thenReturn(true);

        when(repository.findById(42L))
                .thenReturn(Optional.of(approved));

        DataRequest result = service.approveRequest(42L);

        assertEquals(approved, result);
        verify(repository).updateStatusIfPending(42L, RequestStatus.APPROVED);
        verify(repository).findById(42L);
    }

    @Test
    void approvingMissingRequestThrowsNotFound() {
        DataRequestRepository repository = mock(DataRequestRepository.class);
        DataRequestService service = new DataRequestService(repository);

        when(repository.updateStatusIfPending(999L, RequestStatus.APPROVED))
                .thenReturn(false);

        when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                RequestNotFoundException.class,
                () -> service.approveRequest(999L)
        );
    }

    @Test
    void approvingRejectedRequestThrowsAlreadyDecided() {
        DataRequestRepository repository = mock(DataRequestRepository.class);
        DataRequestService service = new DataRequestService(repository);

        DataRequest rejected = new DataRequest(
                42L,
                "customer-006",
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 15),
                RequestStatus.REJECTED
        );

        when(repository.updateStatusIfPending(42L, RequestStatus.APPROVED))
                .thenReturn(false);

        when(repository.findById(42L))
                .thenReturn(Optional.of(rejected));

        assertThrows(
                RequestAlreadyDecidedException.class,
                () -> service.approveRequest(42L)
        );
    }
}
