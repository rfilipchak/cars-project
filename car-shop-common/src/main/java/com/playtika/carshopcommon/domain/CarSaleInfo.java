package com.playtika.carshopcommon.domain;

import lombok.Data;
import lombok.NonNull;

@Data
public class CarSaleInfo {
    @NonNull
    private final long id;
    @NonNull
    private final Car car;
    @NonNull
    private final int price;
    @NonNull
    private final String contact;
}
