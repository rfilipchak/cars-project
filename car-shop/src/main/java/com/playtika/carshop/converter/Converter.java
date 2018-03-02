package com.playtika.carshop.converter;

import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.CarShopEntity;
import com.playtika.carshop.dao.entity.DealEntity;
import com.playtika.carshop.dao.entity.PersonEntity;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
@NoArgsConstructor
public class Converter {

    public CarEntity domainToCarEntity(Car car) {
        return new CarEntity(car.getBrand(),
                car.getYear(), car.getRegistration(), car.getColor());
    }

    public PersonEntity domainToPersonEntity(String contact) {
        return new PersonEntity(contact);
    }

    public CarShopEntity domainToCarShopEntity(CarSaleInfo carSaleInfo) {
        return new CarShopEntity(carSaleInfo.getId(),
                domainToCarEntity(carSaleInfo.getCar()),
                carSaleInfo.getPrice(),
                domainToPersonEntity(carSaleInfo.getContact()));
    }

    public Deal dealEntityToDeal(DealEntity deal) {
        return new Deal(deal.getId(),
                carShopEntityToCarSaleInfo(deal.getCarShopEntity()),
                deal.getPerson().getContact(),
                deal.getBuyerPrice(), deal.getDealStatus());
    }

    public CarSaleInfo carShopEntityToCarSaleInfo(CarShopEntity car) {
        return new CarSaleInfo(car.getId(),
                new Car(car.getCar().getBrand(), car.getCar().getYear()
                        , car.getCar().getRegistration(), car.getCar().getColor())
                , car.getPrice(), car.getPerson().getContact());
    }

    public Collection<CarSaleInfo> carShopEntitiesToCarSaleInfoList(Collection<CarShopEntity> list) {
        Collection<CarSaleInfo> carSaleInfos = new ArrayList<>();
        for (CarShopEntity carShopEntity : list) {
            carSaleInfos.add(carShopEntityToCarSaleInfo(carShopEntity));
        }
        return carSaleInfos;
    }

    public Collection<Deal> DealEntitiesToDealsList(Collection<DealEntity> list) {
        Collection<Deal> allDeals = new ArrayList<>();
        for (DealEntity dealEntity : list) {
            allDeals.add(dealEntityToDeal(dealEntity));
        }
        return allDeals;
    }
}