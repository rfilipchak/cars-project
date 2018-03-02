package com.playtika.carshopproposition.web;

import com.playtika.carshopproposition.service.CarPropositionService;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PropositionController.class)
public class PropositionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private TestSupportValues values = new TestSupportValues();
    @MockBean
    private CarPropositionService service;

    @Test
    public void shouldReturnAddedCarShopSaleIdFromFile() throws Exception {
        String filePath = "D:\\cars-project\\car-shop-propositions\\src\\test\\resources\\data.csv";

        when(service.carsFileProcessing(filePath)).thenReturn(Arrays.asList(1L));

        mockMvc.perform(post("/proposition/fromFile")
                .content("D:\\cars-project\\car-shop-propositions\\src\\test\\resources\\data.csv")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$[0]").value(1));
    }

    @Test
    public void shouldReturnBadRequestForEmptyFilePath() throws Exception {
        String filePath = "";

        when(service.carsFileProcessing(filePath)).thenReturn(Arrays.asList(1L));

        mockMvc.perform(post("/proposition/fromFile")
                .content("")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnDealIdAfterAdding() throws Exception {
        String buyer = "Buyer";
        int price = 200;
        long carSaleId = 1L;

        when(service.addDeal(buyer, price, carSaleId)).thenReturn(1L);

        mockMvc.perform(post("/proposition/addDeal")
                .param("buyer", "Buyer")
                .param("price", "200")
                .param("carSaleId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value(1));
    }

    @Test
    public void shouldReturnBadRequestIfNoSomeParam() throws Exception {
        String buyer = "Buyer";
        int price = 200;
        long carSaleId = 1L;

        when(service.addDeal(buyer, price, carSaleId)).thenReturn(1L);

        mockMvc.perform(post("/proposition/addDeal")
                .param("buyer", "Buyer")
                .param("price", "")
                .param("carSaleId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnAcceptedWhenAcceptBestDealOfCar() throws Exception {
        long carSaleId = 1L;

        when(service.acceptBestDealOfCar(carSaleId)).thenReturn("ACCEPTED");

        mockMvc.perform(post("/proposition/acceptCarDeal/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value("ACCEPTED"));
    }

    @Test
    public void shouldReturnNotFoundIfNoCarSaleId() throws Exception {
        mockMvc.perform(post("/proposition/acceptCarDeal/")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnDealWithTheSameCar() throws Exception {
        long carSaleId = 1l;
        long cadId = 1L;
        long dealId = 1L;
        long dealIdSecond = 2L;
        CarSaleInfo carSaleInfo = values.generateCarSaleInfo(cadId, "AA-0177");
        Deal dealFirst = values.generateDeal(dealId, DealStatus.ACTIVE, carSaleInfo);
        Deal dealSecond = values.generateDeal(dealIdSecond, DealStatus.ACTIVE, carSaleInfo);
        Collection<Deal> deals = Arrays.asList(dealFirst, dealSecond);

        when(service.getAllDealsTheSameCar(carSaleId)).thenReturn(deals);

        mockMvc.perform(get("/proposition/carDeals/1")
                .contentType("application/json;charset=UTF-8")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$.[0].carSaleInfo.car.registration").value("AA-0177"))
                .andExpect(jsonPath("$.[0].carSaleInfo.id").value("1"))
                .andExpect(jsonPath("$.[1].id").value("2"))
                .andExpect(jsonPath("$.[1].carSaleInfo.id").value("1"))
                .andExpect(jsonPath("$.[1].carSaleInfo.car.registration").value("AA-0177"));
    }

    @Test
    public void shouldReturnEmptyIfNoDealsForCarSaleId() throws Exception {
        long carSaleId = 1L;

        when(service.getAllDealsTheSameCar(carSaleId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/proposition/carDeals/1")
                .contentType("application/json;charset=UTF-8")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldReturnNotFoundIfNoCarSaleIdParam() throws Exception {
        mockMvc.perform(get("/proposition/carDeals/")
                .contentType("application/json;charset=UTF-8")).andExpect(status().isNotFound());
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
}



