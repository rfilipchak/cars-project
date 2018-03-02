package com.playtika.carshop.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.CarShopEntity;
import com.playtika.carshop.dao.entity.DealEntity;
import com.playtika.carshop.dao.entity.PersonEntity;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DealsDaoTest extends AbstractDaoTest<DealsDao> {

    @Test
    @DataSet(value = "default-deal.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldReturnIdAfterSavingDealEntity() {
        CarEntity ford = new CarEntity("Ford", 2017, "AA-0188-BH", "black");
        CarShopEntity carShopEntity = new CarShopEntity(2L, ford, 3000, new PersonEntity("contact1"));
        DealEntity dealEntity = new DealEntity(carShopEntity, new PersonEntity(1L, "contact2"), 3000);

        DealEntity savedDealEntity = dao.save(dealEntity);

        assertThat(savedDealEntity.getId()).isEqualTo(5L);
        assertThat(savedDealEntity.getCarShopEntity().getId()).isEqualTo(dealEntity.getCarShopEntity().getId());
    }

    @Test
    @DataSet(value = "default-deal.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldReturnFirstDealEntityWithCarShopId() {
        long carId = 2L;
        long expectedId = 3L;
        long expectedCarShopId = 2L;
        int expectedBuyerPrice = 3000;
        String expectedRegistration = "AA-0188-BH";
        DealStatus expectedStatus = DealStatus.ACTIVE;

        DealEntity deal = dao.findFirstByCarShopEntityId(carId);

        assertThat(deal.getId()).isNotNull().isEqualTo(expectedId);
        assertThat(deal.getCarShopEntity().getId()).isNotNull().isEqualTo(expectedCarShopId);
        assertThat(deal.getCarShopEntity().getCar().getRegistration()).isNotNull().isEqualTo(expectedRegistration);
        assertThat(deal.getBuyerPrice()).isNotNull().isEqualTo(expectedBuyerPrice);
        assertThat(deal.getDealStatus()).isNotNull().isEqualTo(expectedStatus);
    }

    @Test
    @DataSet(value = "default-deal.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldReturnNullIfCarShopIdDoesNotExist() {
        long carId = 3L;

        DealEntity deal = dao.findFirstByCarShopEntityId(carId);

        assertThat(deal).isNull();
    }

    @Test
    @DataSet(value = "default-deal.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldReturnHigherPriceDealForCarShopId() throws Exception {
        long carId = 2L;
        long expectedId = 4L;
        int expectedPrice = 3010;

        DealEntity deal = dao.getFirstByCarShopEntityIdOrderByBuyerPriceDesc(carId);

        assertThat(deal.getId()).isEqualTo(expectedId);
        assertThat(deal.getBuyerPrice()).isEqualTo(expectedPrice);
    }

    @Test
    @DataSet(value = "default-deal.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldReturnDealWithTheSameCarShopId() throws Exception {
        long carId = 2L;
        List<DealEntity> deals = (ArrayList) dao.getAllByCarShopEntityId(carId);
        DealEntity first = deals.get(0);
        DealEntity second = deals.get(1);

        assertThat(deals.size()).isEqualTo(2);
        assertThat(first.getCarShopEntity().getId()).isEqualTo(second.getCarShopEntity().getId());
    }

    @Test
    @DataSet(value = "default-deal.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldGetAllByCarShopEntityId() throws Exception {
        long carId = 1L;
        long expectedDealId = 2L;

        List<DealEntity> deals = (ArrayList) dao.getAllByCarShopEntityIdAndDealStatus(carId, DealStatus.ACTIVE);
        long dealId = deals.get(0).getId();

        assertThat(deals.size()).isEqualTo(1);
        assertThat(dealId).isEqualTo(expectedDealId);
    }

    @Test
    @DataSet(value = "default-deal.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldDeleteDealEntitiesByCarShopEntityId() throws Exception {
        long carId = 2L;
        long dealIdFirst = 3L;
        long dealIdSecond = 4L;

        dao.deleteDealEntitiesByCarShopEntityId(carId);
        boolean checkFirst = dao.exists(dealIdFirst);
        boolean checkSecond = dao.exists(dealIdSecond);

        assertThat(checkFirst).isFalse();
        assertThat(checkSecond).isFalse();
    }
}