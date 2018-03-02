package com.playtika.carshop.carshopservice;

import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;

import java.util.Collection;
import java.util.Optional;

public interface CarService {

    Optional<Long> addCar(Car car, int price, String contact);

    Collection<CarSaleInfo> getCars();

    Optional<CarSaleInfo> getCar(long id);

    boolean removeCar(long id);
}
