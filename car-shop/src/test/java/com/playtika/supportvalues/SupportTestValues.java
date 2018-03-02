package com.playtika.supportvalues;

import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.CarShopEntity;
import com.playtika.carshop.dao.entity.DealEntity;
import com.playtika.carshop.dao.entity.PersonEntity;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import lombok.Data;

@Data
public class SupportTestValues {

    public Car generateCar(String registration){
        return new Car("BMW", 2017, registration, "black");
    }

    public PersonEntity generatePersonEntity(){
        return new PersonEntity("contact");
    }

    public CarEntity generateCarEntity(String registration){
        return new CarEntity("BMW", 2017, registration, "black");
    }

    public CarSaleInfo generateCarSaleInfo(long id, String registration) {
        return new CarSaleInfo(id, new Car("BMW", 2017, registration, "black"),
                2000, "contact");
    }

    public Deal generateDeal(long id, DealStatus dealStatus, CarSaleInfo carSaleInfo) {
        String contact = ("contact" + id).toString();
               return new Deal(id, carSaleInfo, contact, 2000, dealStatus);
    }

    public CarShopEntity generateCarShopEntity(long id, String registration) {
        return new CarShopEntity(id, new CarEntity("BMW", 2017, registration, "black"),
                2000, new PersonEntity("contact"));
    }

    public DealEntity generateDealEntity(long id, CarShopEntity carShopEntity, DealStatus dealStatus) {
        String contact = ("contact"+id).toString();
        return new DealEntity(id, carShopEntity, new PersonEntity(contact), 2000, dealStatus);
    }

}
