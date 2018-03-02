package com.playtika.carshop.exeptions;


public class DealNotFoundException extends RuntimeException {
    private final long requestedId;

    public DealNotFoundException(String message, long requestedId) {
        super(message);
        this.requestedId = requestedId;
    }

    public long getRequestedId() {
        return requestedId;
    }
}
