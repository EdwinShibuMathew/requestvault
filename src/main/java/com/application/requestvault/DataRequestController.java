package com.application.requestvault;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class DataRequestController {

    private final List<DataRequest> requests = new CopyOnWriteArrayList<>();

    @GetMapping("/customer/{customerId}/requests/sample")
    public DataRequest getSampleRequest(
        @PathVariable("customerId") String customerId) {

        return new DataRequest(
                1L,
                customerId,
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 10, 31)
        );
    }

    @PostMapping("/requests")
    public DataRequest createRequest(@RequestBody DataRequest request){
        requests.add(request);
        return request;
    }

    @GetMapping("/requests")
    public List<DataRequest> getRequest(){
        return List.copyOf(requests);
    }
}
