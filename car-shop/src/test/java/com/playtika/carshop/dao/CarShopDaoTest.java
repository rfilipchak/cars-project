package com.playtika.carshop.dao;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.google.common.collect.ImmutableMap;
import com.playtika.carshop.dao.entity.CarShopEntity;
import org.junit.Test;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.annotation.Commit;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class CarShopDaoTest extends AbstractDaoTest<CarShopDao> {

    @Test
    public void shouldReturnNullWhetCarsInShopDoesNotExist() {
        CarShopEntity notExistingCarShopItem = dao.findOne(10000L);

        assertThat(notExistingCarShopItem, nullValue());
    }

    @Test
    @DataSet(value = "expected-car-shop-item.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldReturnCarShopItemById() {
        long id = 1L;
        String registration = "AA-0177-BH";
        int price = 2000;
        String contact = "contact";

        CarShopEntity carShopEntitiesById = dao.findOne(id);

        assertThat(carShopEntitiesById.getCar().getRegistration(),
                samePropertyValuesAs(registration));
        assertThat(carShopEntitiesById.getPrice(),
                samePropertyValuesAs(price));
        assertThat(carShopEntitiesById.getPerson().getContact(),
                samePropertyValuesAs(contact));
    }

    @Test
    @DataSet(value = "empty-car-shop.xml", disableConstraints = true, useSequenceFiltering = false)
    @ExpectedDataSet(value = "expected-car-shop-item.xml")
    @Commit
    public void shouldAddCarShopItem() {
        long id = addCarToCarsDb(1, 2000, 1);

        boolean result = dao.exists(id);

        assertThat(result, is(true));
    }

    @Test
    @DataSet(value = "expected-car-shop-item.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldRemoveCarShopItemById() {
        dao.delete(1L);

        boolean result = dao.exists(1L);

        assertThat(result, is(false));
    }

    @Test
    @DataSet(value = "expected-car-shop-item.xml")
    @ExpectedDataSet(value = "empty-car-shop.xml")
    @Commit
    public void shouldNotRemoveCarAndPersonAfterRemoveCarShopItem() {
        dao.delete(1L);

        boolean result = dao.exists(1L);

        assertThat(result, is(false));
    }

    @Test
    @DataSet(value = "default-car-shop-item.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldGetAllCarShopItems() {
        List<CarShopEntity> cars = dao.findAll();

        CarShopEntity second = cars.get(1);
        CarShopEntity first = cars.get(0);

        assertThat(cars.size(), is(2));
        assertThat(first.getCar().getRegistration(), samePropertyValuesAs("AA-0177-BH"));
        assertThat(second.getCar().getRegistration(), samePropertyValuesAs("AA-0188-BH"));
        assertThat(first.getPrice(), samePropertyValuesAs(2000));
        assertThat(second.getPrice(), samePropertyValuesAs(3000));
    }

    @Test
    @DataSet(value = "empty-car-shop.xml", disableConstraints = true, useSequenceFiltering = false)
    public void shouldGetEmptyIfDbIsEmpty() {
        List<CarShopEntity> allCarShopItem = dao.findAll();

        assertThat(allCarShopItem, is(Collections.EMPTY_LIST));
    }

    @Test(expected = DuplicateKeyException.class)
    @DataSet(value = "empty-car-shop.xml")
    public void shouldNotAddCarShopItemWithTheSameCar() {
        long carId = 1L;
        int price = 2000;
        long personId = 1L;
        long firstId = addCarToCarsDb(carId, price, personId);
        long secondId = addCarToCarsDb(carId, price, personId);

        boolean first = dao.exists(firstId);
        boolean second = dao.exists(secondId);

        assertThat(first, is(true));
        assertThat(second, is(false));
    }

    private long addCarToCarsDb(long carId, int price, long personId) {
        return addRecordToDb("cars_shop", ImmutableMap.of("car_id", carId,
                "price", price, "person_id", personId));
    }
}