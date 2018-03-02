package com.playtika.carshop.carshopservice;

import com.playtika.carshop.converter.Converter;
import com.playtika.carshop.dao.CarDao;
import com.playtika.carshop.dao.CarShopDao;
import com.playtika.carshop.dao.PersonDao;
import com.playtika.carshop.dao.entity.CarEntity;
import com.playtika.carshop.dao.entity.CarShopEntity;
import com.playtika.carshop.dao.entity.PersonEntity;
import com.playtika.carshop.dealservice.DealService;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarServiceWithConverterIntegrationTest {

    private CarService service;
    private DealService dealService;
    @Mock
    private CarDao carDao;
    @Mock
    private CarShopDao carShopDao;
    @Mock
    private PersonDao personDao;

    @Before
    public void init() {
        Converter converter = new Converter();
        service = new CarServiceImpl(converter, carDao, carShopDao, personDao);
    }

    @Test
    public void shouldReturnCarById() {
        long id = 1L;
        CarShopEntity bmw = new CarShopEntity(id, new CarEntity("BMW", 2017, "AA-0177-BH", "black"),
                2000, new PersonEntity("contact"));
        CarSaleInfo expectedBmw = new CarSaleInfo(1L, new Car("BMW", 2017, "AA-0177-BH", "black"),
                2000, "contact");
        when(carShopDao.findOne(anyLong())).thenReturn(bmw);

        Optional<CarSaleInfo> car = service.getCar(id);

        assertThat(car).isEqualTo(Optional.of(expectedBmw));
    }
}
