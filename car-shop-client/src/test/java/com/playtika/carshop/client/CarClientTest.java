package com.playtika.carshop.client;//package com.playtika.carshop.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CarClientTest.TestContext.class)
public class CarClientTest {
    @Autowired
    private CarClient carClient;
    @Autowired
    private ObjectMapper mapper;
    private static int port = 8092;
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(port);

    TestSupportValues values = new TestSupportValues();

    @Test
    public void shouldReturnCarIdAfterAddingCarSaleInfo() throws Exception {
        Car car = new Car("Ford", 2017, "AA-0177-BH", "black");
        int price = 200000;
        String contact = "contact";

        stubFor(post(urlPathEqualTo("/cars"))
                .withQueryParam("price", equalTo(Long.toString(price)))
                .withQueryParam("contact", equalTo(contact))
                .withRequestBody(equalToJson(mapper.writeValueAsString(car)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("1")));

        Long id = carClient.addCarSaleInfo(car, price, contact);

        assertThat(id).isNotNull().isEqualTo(1L);
    }

    @Test
    public void shouldReturnDealIdAfterAddingDeal() {
        String buyer = "buyer";
        int price = 200;
        long careSaleId = 1L;

        stubFor(post(urlPathEqualTo("/deals"))
                .withQueryParam("buyer", equalTo("buyer"))
                .withQueryParam("price", equalTo("200"))
                .withQueryParam("carSaleId", equalTo("1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("1")));

        Long id = carClient.addDeal(buyer, price, careSaleId);

        assertThat(id).isNotNull().isEqualTo(1L);
    }

    @Test
    public void shouldReturnAllDealTheSameCar() {
        long cadId = 1L;
        long dealId = 1L;
        long dealIdSecond = 2L;
        CarSaleInfo carSaleInfo = values.generateCarSaleInfo(cadId, "AA-0177");
        Deal dealFirst = values.generateDeal(dealId, DealStatus.ACTIVE, carSaleInfo);
        Deal dealSecond = values.generateDeal(dealIdSecond, DealStatus.ACTIVE, carSaleInfo);
        Collection<Deal> deals = Arrays.asList(dealFirst, dealSecond);
        String returned = new Gson().toJson(deals);

        stubFor(get(urlPathEqualTo("/deals/carDeals/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(returned)));
        Collection<Deal> dealToCompare = carClient.getAllDealsTheSameCar(cadId);
        String expected = new Gson().toJson(dealToCompare);

        assertThat(returned).isEqualTo(expected);
    }

    @Test
    public void shouldReturnAcceptedForResponse() {
        long carId = 1L;

        stubFor(post(urlPathEqualTo("/deals/acceptCarDeal/1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("ACCEPTED")));

        String result = carClient.autoAcceptBestDealOfCar(carId);

        assertThat(result).isEqualTo("ACCEPTED");
    }

    public static class TestSupportValues {
        public CarSaleInfo generateCarSaleInfo(long id, String registration) {
            return new CarSaleInfo(id, new Car("BMW", 2017, registration, "black"),
                    2000, "contact");
        }

        public Deal generateDeal(long id, DealStatus dealStatus, CarSaleInfo carSaleInfo) {
            String contact = ("contact" + id).toString();
            return new Deal(id, carSaleInfo, contact, 2000, dealStatus);
        }
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableFeignClients(clients = {CarClient.class})
    public static class TestContext {
    }

}
