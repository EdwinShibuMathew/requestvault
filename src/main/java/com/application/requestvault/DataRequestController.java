package com.application.requestvault;

import jakarta.validation.Valid;
import java.util.List;
import java.time.LocalDate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class DataRequestController {
    private final DataRequestService dataRequestService;

    public DataRequestController(DataRequestService dataRequestService) {
        this.dataRequestService = dataRequestService;
    }

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
    public DataRequest createRequest(@Valid @RequestBody CreateDataRequest input){
        return dataRequestService.createRequest(input);
    }

    @GetMapping("/requests")
    public List<DataRequest> getRequest(){
        return dataRequestService.getRequests();
    }

    @GetMapping("/requests/{id}")
    public DataRequest getRequestById(@PathVariable("id") long id) {
        return dataRequestService.getRequestById(id)
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Request not found"
                ));
    }
}