package com.playtika.carshop.exeptions;

public class CarNotFoundException extends RuntimeException {
    private final long requestedId;

    public CarNotFoundException(String message, long requestedId) {
        super(message);
        this.requestedId = requestedId;
    }

    public long getRequestedId() {
        return requestedId;
    }
}
