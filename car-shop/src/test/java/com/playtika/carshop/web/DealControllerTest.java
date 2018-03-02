package com.playtika.carshop.web;

import com.playtika.carshop.carshopservice.CarService;
import com.playtika.carshop.dealservice.DealService;
import com.playtika.carshop.exeptions.CarNotFoundException;
import com.playtika.carshop.exeptions.CreateDealException;
import com.playtika.carshop.exeptions.DealNotFoundException;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import com.playtika.supportvalues.SupportTestValues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class DealControllerTest extends SupportTestValues {
    @Mock
    private CarService carService;
    @Mock
    private DealService dealService;
    private MockMvc mockMvc;
    private DealController controller;

    @Before
    public void init() {
        controller = new DealController(carService, dealService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldFailCreatingDealWithMissingBody() throws Exception {
        mockMvc.perform(post("/deals")
                .param("buyer", "contact").param("price", "2000")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldThrowExceptionWhenDealJustAccepted() throws Exception {
        when(dealService.checkDealForAccept(anyLong())).thenReturn(true);

        try {
            mockMvc.perform(post("/deals")
                    .param("buyer", "contact")
                    .param("price", "2000")
                    .param("carSaleId", "1")
                    .contentType("application/json;charset=UTF-8"));
        } catch (NestedServletException e) {
            assertThat(e.getCause().getClass()).isEqualTo(CreateDealException.class);
        }
    }

    @Test
    public void shouldThrowExceptionWhenCarForDealNotExist() throws Exception {
        CarSaleInfo car = generateCarSaleInfo(1L, "aa-1");
        when(dealService.checkDealForAccept(anyLong())).thenReturn(false);
        when(carService.getCar(car.getId())).thenReturn(Optional.empty());

        try {
            mockMvc.perform(post("/deals")
                    .param("buyer", "contact")
                    .param("price", "2000")
                    .param("carSaleId", "1")
                    .contentType("application/json;charset=UTF-8"));
        } catch (NestedServletException e) {
            assertThat(e.getCause().getClass()).isEqualTo(CarNotFoundException.class);
        }
    }

    @Test
    public void shouldThrowExceptionWhenSellerAddBuyerTheSame() throws Exception {
        CarSaleInfo car = generateCarSaleInfo(1L, "aa-1");

        when(dealService.checkDealForAccept(anyLong())).thenReturn(false);
        when(carService.getCar(1L)).thenReturn(Optional.of(car));
        when(dealService.addDeal(car, "contact", 2000)).thenReturn(Optional.empty());

        try {
            mockMvc.perform(post("/deals")
                    .param("buyer", "contact")
                    .param("price", "2000")
                    .param("carSaleId", "1")
                    .contentType("application/json;charset=UTF-8"));
        } catch (NestedServletException e) {
            assertThat(e.getCause().getClass()).isEqualTo(CreateDealException.class);
        }
    }

    @Test
    public void shouldGetOkResponseForEmptyDeals() throws Exception {
        when(dealService.getAllDeals()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/deals").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());

    }

    @Test
    public void shouldGetOkResponseIfNoDealsForCarId() throws Exception {
        CarSaleInfo car = generateCarSaleInfo(1L, "aa-1");
        when(dealService.getAllDealsTheSameCar(anyLong())).thenReturn(Collections.emptyList());
        when(carService.getCar(1L)).thenReturn(Optional.of(car));

        mockMvc.perform(get("/deals/carDeals/1").contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldThrowExceptionIfNoCarForSale() throws Exception {
        when(carService.getCar(1L)).thenReturn(Optional.empty());
        try {
            mockMvc.perform(get("/deals/carDeals/1").contentType("application/json;charset=UTF-8"));
        } catch (NestedServletException e) {
            assertThat(e.getCause().getClass()).isEqualTo(CarNotFoundException.class);
        }
    }

    @Test
    public void shouldThrowExceptionWhenNoCarForBestDeal() throws Exception {
        when(carService.getCar(1L)).thenReturn(Optional.empty());
        try {
            mockMvc.perform(get("/deals/bestDeal/1").contentType("application/json;charset=UTF-8"));
        } catch (NestedServletException e) {
            assertThat(e.getCause().getClass()).isEqualTo(CarNotFoundException.class);
        }
    }

    @Test
    public void shouldThrowExceptionIfNoDealForCar() throws Exception {
        CarSaleInfo car = generateCarSaleInfo(1L, "aa-1");
        when(carService.getCar(1L)).thenReturn(Optional.of(car));
        when(dealService.getBestDeal(1L)).thenReturn(Optional.empty());
        try {
            mockMvc.perform(get("/deals/bestDeal/1").contentType("application/json;charset=UTF-8"));
        } catch (NestedServletException e) {
            assertThat(e.getCause().getClass()).isEqualTo(DealNotFoundException.class);
        }
    }

    @Test
    public void shouldReturnDealStatusIfJustAccepted() throws Exception {
        long carId = 1L;
        CarSaleInfo car = generateCarSaleInfo(1L, "aa-1");
        Deal deal = generateDeal(1L, DealStatus.ACTIVE, car);
        when(carService.getCar(1L)).thenReturn(Optional.of(car));
        when(dealService.getBestDeal(1L)).thenReturn(Optional.of(deal));
        when(dealService.getDealStatusById(1L)).thenReturn(DealStatus.ACCEPTED);

        String status = controller.autoAcceptBestDealOfCar(carId);

        assertThat(status).isEqualTo(DealStatus.ACCEPTED.toString());
    }
    @Test
    public void shouldRunAcceptDealMethodOnce() throws Exception {
        long carId = 1L;
        CarSaleInfo car = generateCarSaleInfo(1L, "aa-1");
        Deal deal = generateDeal(1L, DealStatus.ACTIVE, car);
        when(carService.getCar(1L)).thenReturn(Optional.of(car));
        when(dealService.getBestDeal(1L)).thenReturn(Optional.of(deal));
        when(dealService.getDealStatusById(1L)).thenReturn(DealStatus.ACTIVE);

        controller.autoAcceptBestDealOfCar(carId);
        verify(dealService,times(1)).acceptDeal(1L);
        verify(dealService,times(2)).getDealStatusById(1L);
    }
}