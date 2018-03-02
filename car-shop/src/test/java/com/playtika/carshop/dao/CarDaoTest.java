package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.CarEntity;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class CarDaoTest extends AbstractDaoTest<CarDao> {

    @Test
    public void shouldReturnNullWhenCarDoesNotExist() {
        CarEntity notExistingCar = dao.getCarByRegistration("unknown");

        assertThat(notExistingCar, nullValue());
    }

    @Test
    public void shouldReturnIdAfterAddingCarToCars() {
        long id = addCarToCarsDb("AA-0177-BH");

        assertThat(id, is(notNullValue()));
    }

    @Test
    public void shouldFindCarByRegistration() {
        String registration = "AA-0177-BH";
        long id = addCarToCarsDb(registration);
        CarEntity expectedCar = new CarEntity("BMW", 1980, "AA-0177-BH", "black");
        expectedCar.setId(id);

        CarEntity car = dao.getCarByRegistration(registration);

        assertThat(car, samePropertyValuesAs(expectedCar));
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void shouldNotAddTheSameCarToCars() {
        addCarToCarsDb("AA-0177-BH");
        addCarToCarsDb("AA-0177-BH");
    }

    private long addCarToCarsDb(String registration) {
        CarEntity car = new CarEntity("BMW", 1980, registration, "black");
        return dao.save(car).getId();
    }
}