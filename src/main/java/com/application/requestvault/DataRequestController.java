package com.application.requestvault;

import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class DataRequestController {

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
}
