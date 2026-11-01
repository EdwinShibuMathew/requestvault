package com.application.requestvault;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;

public record CreateDataRequest(
        @NotBlank(message = "customerId must not be blank")
        String customerId,

        @NotNull(message = "fromDate is required")
        LocalDate fromDate,

        @NotNull(message = "toDate is required")
        LocalDate toDate
) {
        @AssertTrue(message = "toDate must be on or after fromDate")
        public boolean isDateRangeValid(){
                if(fromDate == null || toDate == null){
                        return true;
                }
                return !toDate.isBefore(fromDate);
        }
}
