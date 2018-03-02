package com.playtika.carshopcommon.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class Car {
    @NonNull
    private final String brand;
    @NonNull
    private final int year;
    @NonNull
    private final String registration;
    @NonNull
    private final String color;
}
