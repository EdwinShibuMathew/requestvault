package com.application.requestvault;

import java.util.List;

public record ValidationErrorResponse(
        String message,
    List<String> errors
) {
}
