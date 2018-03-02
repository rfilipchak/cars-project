package com.playtika.carshop.converter;

import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.CarShopEntity;
import com.playtika.carshop.dao.entity.DealEntity;
import com.playtika.carshop.dao.entity.PersonEntity;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import com.playtika.supportvalues.SupportTestValues;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConverterTest extends SupportTestValues{

    private Converter converter = new Converter();

    @Test
    public void shouldConvertDomainToCarEntity() {
        Car bmw = generateCar("AA-0177-BH");
        CarEntity bmwToCompare = generateCarEntity("AA-0177-BH");

        CarEntity bmwEntity = converter.domainToCarEntity(bmw);

        assertThat(bmwEntity).isEqualTo(bmwToCompare);
    }

    @Test
    public void shouldConvertDomainToPersonEntity() {
        String contact = "contact";
        PersonEntity personToCompare = new PersonEntity(contact);
        
        PersonEntity person = converter.domainToPersonEntity(contact);

        assertThat(person).isEqualTo(personToCompare);
    }

    @Test
    public void shouldConvertCarShopEntityToCarSaleInfo() {
        CarSaleInfo expectedCar = generateCarSaleInfo(1L,"AA-0177-BH");
        CarShopEntity car = generateCarShopEntity(1L,"AA-0177-BH");

        CarSaleInfo carDomain = converter.carShopEntityToCarSaleInfo(car);
        
        assertThat(carDomain).isEqualTo(expectedCar);
    }

    @Test
    public void shouldConvertCarShopEntitiesToCarSaleInfoList() {
        CarShopEntity first = generateCarShopEntity(1L,"AA-0177-BH");
        CarShopEntity second = generateCarShopEntity(2L,"AA-0178-BH");

        CarSaleInfo expectedFirst = generateCarSaleInfo(1L,"AA-0177-BH");
        CarSaleInfo expectedSecond = generateCarSaleInfo(2L,"AA-0178-BH");
        List<CarShopEntity> carShopEntities = Arrays.asList(first, second);
        List<CarSaleInfo> carSaleInfos = Arrays.asList(expectedFirst, expectedSecond);

        Collection<CarSaleInfo> carShopEntityToDomain = converter.carShopEntitiesToCarSaleInfoList(carShopEntities);

        assertThat(carShopEntityToDomain).isEqualTo(carSaleInfos);
    }

    @Test
    public void shouldConvertDealEntityToDeal(){
        Deal deal = generateDeal(1L, DealStatus.ACTIVE, generateCarSaleInfo(2L, "AA-1"));
        DealEntity dealEntity = generateDealEntity(1L,
                generateCarShopEntity(2L, "AA-1"), DealStatus.ACTIVE);

        Deal dealToCheck = converter.dealEntityToDeal(dealEntity);

        assertThat(dealToCheck).isEqualTo(deal);
    }

    @Test
    public void shouldConvertDealEntitiesToDealsList(){
        Deal dealFirst = generateDeal(1L, DealStatus.ACTIVE, generateCarSaleInfo(2L, "AA-1"));
        Deal dealSecond = generateDeal(2L, DealStatus.ACTIVE, generateCarSaleInfo(3L, "AA-12"));
        DealEntity dealEntityFirst = generateDealEntity(1L,
                generateCarShopEntity(2L, "AA-1"), DealStatus.ACTIVE);
        DealEntity dealEntitySecond = generateDealEntity(2L,
                generateCarShopEntity(3L, "AA-12"), DealStatus.ACTIVE);
        List<DealEntity> dealEntities = Arrays.asList(dealEntityFirst,dealEntitySecond);
        List<Deal> dealsToCompare = Arrays.asList(dealFirst, dealSecond);

        Collection<Deal> deals = converter.DealEntitiesToDealsList(dealEntities);

        assertThat(deals).isEqualTo(dealsToCompare);
    }
}