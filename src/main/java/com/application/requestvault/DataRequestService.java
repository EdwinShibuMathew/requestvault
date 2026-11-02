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

    public List<DataRequest> getRequests() {
        return repository.findAll();
    }

    public Optional<DataRequest> getRequestById(long id) {
        return repository.findById(id);
    }

}
