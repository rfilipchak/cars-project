package com.playtika.carshopproposition.carpropositionservice;

import com.playtika.carshopproposition.service.CarPropositionService;
import com.playtika.carshopproposition.service.CarPropositionServiceImpl;
import com.playtika.carshop.client.CarClient;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CarPropositionServiceTest {

    private CarPropositionService service;
    private TestSupportValues values = new TestSupportValues();
    @Mock
    private CarClient client;

    @Before
    public void init() {
        service = new CarPropositionServiceImpl(client);
    }

    @Test
    public void shouldGetIdFromClientAfterParsingFile() throws IOException {
        Car car = values.generateCar("AA-111");
        int price = 200;
        String contact = "contact";
        String filePath = "D:\\cars-project\\car-shop-propositions\\src\\test\\resources\\data.csv";

        when(client.addCarSaleInfo(car, price, contact)).thenReturn(1L);

        List<Long> addedId = service.carsFileProcessing(filePath);
        long idd = addedId.get(0);
        assertThat(idd).isEqualTo(1L);
    }

    @Test(expected = FileNotFoundException.class)
    public void shouldReturnEnExceptionIfFileDoesNotExist() throws IOException {

        String filePath = "D:\\cars-project\\car-shop-propositions\\src\\test\\resources\\notExist.csv";

        service.carsFileProcessing(filePath);
    }

    @Test
    public void shouldReturnTheSameDealForCarId() {
        long carId = 1L;
        long firstDeal = 1L;
        long secondDeal = 1L;
        CarSaleInfo carSaleInfo = values.generateCarSaleInfo(carId, "AA-112");
        Deal dealFirst = values.generateDeal(firstDeal, DealStatus.ACTIVE, carSaleInfo);
        Deal dealSecond = values.generateDeal(secondDeal, DealStatus.ACTIVE, carSaleInfo);
        Collection<Deal> deals = Arrays.asList(dealFirst, dealSecond);

        when(client.getAllDealsTheSameCar(carId)).thenReturn(deals);

        Collection<Deal> expected = service.getAllDealsTheSameCar(carId);

        assertThat(expected).isEqualTo(deals);
    }

    @Test
    public void shouldReturnAcceptedForDeal() {
        long carId = 1L;

        when(client.autoAcceptBestDealOfCar(carId)).thenReturn("ACCEPTED");

        String expected = service.acceptBestDealOfCar(carId);

        assertThat(expected).isEqualTo("ACCEPTED");
    }

    @Test
    public void shouldReturnIdAfterAddingDeal() {
        long carId = 1L;
        String buyer = "contact";
        int price = 2000;
        CarSaleInfo carSaleInfo = values.generateCarSaleInfo(carId, "AA-112");

        when(client.addDeal(buyer, price, carSaleInfo.getId())).thenReturn(1L);

        Long result = service.addDeal(buyer, price, carSaleInfo.getId());

        assertThat(result).isEqualTo(1L);
    }

    public static class TestSupportValues {

        Car generateCar(String registration) {
            return new Car("BMW", 2017, registration, "black");
        }

        CarSaleInfo generateCarSaleInfo(long id, String registration) {
            return new CarSaleInfo(id, new Car("BMW", 2017, registration, "black"),
                    2000, "contact");
        }

        Deal generateDeal(long id, DealStatus dealStatus, CarSaleInfo carSaleInfo) {
            String contact = ("contact" + id).toString();
            return new Deal(id, carSaleInfo, contact, 2000, dealStatus);
        }
    }
}
