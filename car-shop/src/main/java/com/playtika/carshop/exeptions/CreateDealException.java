package com.playtika.carshop.exeptions;

import lombok.Getter;

@Getter
public class CreateDealException extends RuntimeException {
    public CreateDealException(String message) {
        super(message);
    }
}
