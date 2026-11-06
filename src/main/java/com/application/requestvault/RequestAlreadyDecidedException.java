package com.application.requestvault;

public class RequestAlreadyDecidedException extends RuntimeException {

    public RequestAlreadyDecidedException(long id) {
        super("Request " + id + " has already been approved or rejected");
    }
}