package com.playtika.carshopcommon.domain;

import com.playtika.carshopcommon.dealstatus.DealStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Deal {
    @NonNull
    private final long id;
    @NonNull
    private final CarSaleInfo carSaleInfo;
    @NonNull
    private final String buyerContact;
    @NonNull
    private final int buyerPrice;
    @NonNull
    private final DealStatus dealStatus;
}
