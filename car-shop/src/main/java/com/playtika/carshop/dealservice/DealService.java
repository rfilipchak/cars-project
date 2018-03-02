package com.playtika.carshop.dealservice;

import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;

import java.util.Collection;
import java.util.Optional;

public interface DealService {

    Optional<Deal> addDeal(CarSaleInfo carSaleInfo, String buyerContact, int price);

    Collection<Deal> getAllDeals();

    Collection<Deal> getAllDealsTheSameCar(long carSaleId);

    void acceptDeal(long carSaleId);

    void removeDealByCarShopId(long carSaleId);

    DealStatus getDealStatusById(long carSaleId);

    Optional<Deal> getBestDeal(long carSaleId);

    boolean checkDealForAccept(long carSaleId);
}
