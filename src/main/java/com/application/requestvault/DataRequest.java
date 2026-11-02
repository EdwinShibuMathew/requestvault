package com.application.requestvault;

import java.time.LocalDate;

public record DataRequest(
        long id,
        String customerId,
        LocalDate fromDate,
        LocalDate toDate,
        RequestStatus status
){
}
