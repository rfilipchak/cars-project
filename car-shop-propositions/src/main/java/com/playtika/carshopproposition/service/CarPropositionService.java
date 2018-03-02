package com.playtika.carshopproposition.service;

import com.playtika.carshopcommon.domain.Deal;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface CarPropositionService {
    List<Long> carsFileProcessing(String filePath) throws IOException;

    Long addDeal(String buyer, int price, long carSaleId);

    String acceptBestDealOfCar(long carSaleId);

    Collection<Deal> getAllDealsTheSameCar(long carSaleId);
}
