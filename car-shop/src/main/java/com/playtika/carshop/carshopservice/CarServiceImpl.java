package com.playtika.carshop.carshopservice;

import com.playtika.carshop.converter.Converter;
import com.playtika.carshop.dao.CarDao;
import com.playtika.carshop.dao.CarShopDao;
import com.playtika.carshop.dao.PersonDao;
import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.CarShopEntity;
import com.playtika.carshop.dao.entity.PersonEntity;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class CarServiceImpl implements CarService {

    private final Converter converter;
    private final CarDao carDao;
    private final CarShopDao carShopDao;
    private final PersonDao personDao;

    @Override
    public Optional<Long> addCar(Car car, int price, String contact) {
        if(carShopDao.findCarShopEntityByCar_Registration(car.getRegistration())==null) {
            CarShopEntity carShopEntity = new CarShopEntity(checkCarForExist(car),
                    price, checkPersonForExist(contact));
                    long id = carShopDao.save(carShopEntity).getId();
            return Optional.of(id);
        }
        return Optional.empty();
    }

    @Override
    public Collection<CarSaleInfo> getCars() {
        return converter.carShopEntitiesToCarSaleInfoList(carShopDao.findAll());
    }

    @Override
    public Optional<CarSaleInfo> getCar(long id) {
        CarShopEntity carShopEntity = carShopDao.findOne(id);
        if (carShopEntity != null) {
            return Optional.of(converter.carShopEntityToCarSaleInfo(carShopEntity));
        }
        return Optional.empty();
    }

    @Override
    public boolean removeCar(long id) {
        if (carShopDao.exists(id)) {
            carShopDao.delete(id);
            return true;
        }
        return false;
    }

    private CarEntity checkCarForExist(Car car) {
        CarEntity existCar = carDao.getCarByRegistration(car.getRegistration());
        if (existCar != null) {
            return existCar;
        }
        CarEntity carEntity = converter.domainToCarEntity(car);
        carDao.save(carEntity);
        return carEntity;
    }

    public PersonEntity checkPersonForExist(String contact) {
        PersonEntity existPerson = personDao.getPersonByContact(contact);
        if (existPerson != null) {
            return existPerson;
        }
        PersonEntity personEntity = converter.domainToPersonEntity(contact);
        personDao.save(personEntity);
        return personEntity;
    }
}
