package com.application.requestvault;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class DataRequestService {

    private final List<DataRequest> requests = new CopyOnWriteArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public DataRequest createRequest(CreateDataRequest input){
        DataRequest request = new DataRequest(
                nextId.getAndIncrement(),
                input.customerId(),
                input.fromDate(),
                input.toDate()
        );

        requests.add(request);
        return request;
    }

    public List<DataRequest> getRequests(){
        return List.copyOf(requests);
    }

    public Optional<DataRequest> getRequestById(long id){
        return requests.stream()
                .filter(request -> request.id() == id)
                .findFirst();
    }

}
