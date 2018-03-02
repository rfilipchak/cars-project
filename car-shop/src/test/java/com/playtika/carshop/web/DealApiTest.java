package com.playtika.carshop.web;

import com.playtika.carshop.carshopservice.CarService;
import com.playtika.carshop.dealservice.DealService;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import com.playtika.supportvalues.SupportTestValues;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(DealController.class)
public class DealApiTest extends SupportTestValues {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CarService carService;
    @MockBean
    private DealService dealService;

    @Test
    public void shouldCreateANewDealSuccessfully() throws Exception {
        long carId = 1L;
        long dealId = 1L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");
        Deal deal = generateDeal(dealId, DealStatus.ACTIVE, carSaleInfo);

        when(dealService.checkDealForAccept(carId)).thenReturn(false);
        when(carService.getCar(carId)).thenReturn(Optional.of(carSaleInfo));
        when(dealService.addDeal(carSaleInfo, "contact1", 2000)).thenReturn(Optional.of(deal));

        String result = mockMvc.perform(post("/deals")
                .param("buyer", "contact1")
                .param("price", "2000")
                .param("carSaleId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long id = Long.parseLong(result);
        assertThat(id).isNotNull().isEqualTo(dealId);
    }

    @Test
    public void shouldReturnBadRequestIfDealJustAccepted() throws Exception {
        long carId = 1L;

        when(dealService.checkDealForAccept(carId)).thenReturn(true);

        mockMvc.perform(post("/deals")
                .param("buyer", "contact1")
                .param("price", "2000")
                .param("carSaleId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestIfSellerContactEqualBuyerContact() throws Exception {
        long carId = 1L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");

        when(dealService.checkDealForAccept(carId)).thenReturn(false);
        when(carService.getCar(carId)).thenReturn(Optional.of(carSaleInfo));
        when(dealService.addDeal(carSaleInfo, "contact", 2000)).thenReturn(Optional.empty());

        mockMvc.perform(post("/deals")
                .param("buyer", "contact")
                .param("price", "2000")
                .param("carSaleId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnEmptyIfDealsNotExist() throws Exception {
        when(dealService.getAllDeals()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldReturnDealsForCarProposition() throws Exception {
        long carId = 1L;
        long dealIdFirst = 1L;
        long dealIdSecond = 2L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");
        Deal dealFirst = generateDeal(dealIdFirst, DealStatus.ACTIVE, carSaleInfo);
        Deal dealSecond = generateDeal(dealIdSecond, DealStatus.ACTIVE, carSaleInfo);
        List deals = Arrays.asList(dealFirst, dealSecond);

        when(dealService.getAllDeals()).thenReturn(deals);

        mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldReturnErrorIfDoesNotCarPropositionId() throws Exception {
        when(carService.getCar(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/deals/carDeals/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(NOT_FOUND.getReasonPhrase()));
    }

    @Test
    public void shouldReturnEmptyIfNoDealForCarProposition() throws Exception {
        long carId = 1L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");

        when(carService.getCar(1L)).thenReturn(Optional.of(carSaleInfo));

        mockMvc.perform(get("/deals/carDeals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldReturnDealsForSameCarProposition() throws Exception {
        long carId = 1L;
        long dealIdFirst = 1L;
        long dealIdSecond = 2L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");
        Deal dealFirst = generateDeal(dealIdFirst, DealStatus.ACTIVE, carSaleInfo);
        Deal dealSecond = generateDeal(dealIdSecond, DealStatus.ACTIVE, carSaleInfo);
        List deals = Arrays.asList(dealFirst, dealSecond);

        when(carService.getCar(1L)).thenReturn(Optional.of(carSaleInfo));
        when(dealService.getAllDealsTheSameCar(carId)).thenReturn(deals);


        mockMvc.perform(get("/deals/carDeals/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(dealIdFirst))
                .andExpect(jsonPath("$[1].id").value(dealIdSecond));
    }

    @Test
    public void shouldReturnErrorIfNoBestDealForCarProposition() throws Exception {
        long carId = 1L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");

        when(carService.getCar(1L)).thenReturn(Optional.of(carSaleInfo));
        when(dealService.getBestDeal(carId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/deals/bestDeal/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(NOT_FOUND.getReasonPhrase()));
    }

    @Test
    public void shouldReturnBestDealForCarProposition() throws Exception {
        long carId = 1L;
        long dealId = 1L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");
        Deal deal = generateDeal(dealId, DealStatus.ACTIVE, carSaleInfo);

        when(carService.getCar(1L)).thenReturn(Optional.of(carSaleInfo));
        when(dealService.getBestDeal(carId)).thenReturn(Optional.of(deal));

        mockMvc.perform(get("/deals/bestDeal/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.carSaleInfo.id").value(1))
                .andExpect(jsonPath("$.carSaleInfo.car.registration").value("aa-1"))
                .andExpect(jsonPath("$.buyerContact").value("contact1"))
                .andExpect(jsonPath("$.dealStatus").value("ACTIVE"));
    }

    @Test
    public void shouldReturnStatusForAcceptedDeal() throws Exception {
        long carId = 1L;
        long dealId = 1L;
        CarSaleInfo carSaleInfo = generateCarSaleInfo(carId, "aa-1");
        Deal deal = generateDeal(dealId, DealStatus.ACCEPTED, carSaleInfo);

        when(carService.getCar(1L)).thenReturn(Optional.of(carSaleInfo));
        when(dealService.getBestDeal(carId)).thenReturn(Optional.of(deal));
        when(dealService.getDealStatusById(carId)).thenReturn(DealStatus.ACCEPTED);

        String status = mockMvc.perform(post("/deals/acceptCarDeal/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(status).isEqualTo("ACCEPTED");
    }
}




