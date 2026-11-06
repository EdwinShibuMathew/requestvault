package com.application.requestvault;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DataRequestService {

    private final DataRequestRepository repository;

    public DataRequestService(DataRequestRepository repository) {
        this.repository = repository;
    }

    public DataRequest createRequest(CreateDataRequest input) {
        return repository.create(input);
    }

    public DataRequest approveRequest(long id) {
        return decideRequest(id, RequestStatus.APPROVED);
    }

    public DataRequest rejectRequest(long id) {
        return decideRequest(id, RequestStatus.REJECTED);
    }

    private DataRequest decideRequest(long id, RequestStatus decision) {
        boolean updated = repository.updateStatusIfPending(id, decision);

        DataRequest request = repository.findById(id)
                .orElseThrow(() -> new RequestNotFoundException(id));

        if (!updated) {
            throw new RequestAlreadyDecidedException(id);
        }

        return request;
    }

    public List<DataRequest> getRequests() {
        return repository.findAll();
    }

    public Optional<DataRequest> getRequestById(long id) {
        return repository.findById(id);
    }

}
