package com.application.requestvault;

public class RequestNotFoundException extends RuntimeException {

    public RequestNotFoundException(long id) {
        super("Request " + id + " was not found");
    }
}