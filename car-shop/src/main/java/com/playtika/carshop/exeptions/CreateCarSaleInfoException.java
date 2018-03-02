package com.playtika.carshop.exeptions;

import lombok.Getter;

@Getter
public class CreateCarSaleInfoException extends RuntimeException {
    public CreateCarSaleInfoException(String message) {
        super(message);
    }
}
