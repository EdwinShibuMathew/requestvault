package com.application.requestvault;

import java.time.LocalDate;

public record CreateDataRequest(
        String customerId,
        LocalDate fromDate,
        LocalDate toDate
) {
}
