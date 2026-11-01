package com.application.requestvault;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record CreateDataRequest(
        @NotBlank(message = "customerId must not be blank")
        String customerId,
        LocalDate fromDate,
        LocalDate toDate
) {
}
