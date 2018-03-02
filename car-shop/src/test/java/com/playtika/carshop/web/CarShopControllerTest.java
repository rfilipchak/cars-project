package com.playtika.carshop.web;

import com.playtika.carshop.carshopservice.CarService;
import com.playtika.carshop.dealservice.DealService;
import com.playtika.carshop.exeptions.CreateCarSaleInfoException;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.supportvalues.SupportTestValues;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class CarShopControllerTest extends SupportTestValues {
    @Mock
    private CarService carService;
    @Mock
    private DealService dealService;
    private CarShopController controller;
    private MockMvc mockMvc;

    @Before
    public void init() {
        controller = new CarShopController(carService, dealService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void shouldFailCreatingCarWithMissingBody() throws Exception {
        mockMvc.perform(post("/cars")
                .param("price", "200000").param("contact", "contact")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldThrowExceptionWhenCarSaleInfoExist() throws Exception {
        Car car = new Car("Ford", 2017, "AA-0177-BH", "black");
        int price = 200000;
        String contact = "contact";

        when(carService.addCar(car, price, contact)).thenReturn(Optional.empty());

        try {
            mockMvc.perform(post("/cars")
                    .content("{\"brand\": \"Ford\",\"year\":2017,\"registration\":\"AA-0177-BH\",\"color\":\"black\"}")
                    .param("price", "200000").param("contact", "contact")
                    .contentType("application/json;charset=UTF-8"));
        } catch (NestedServletException e) {
            assertThat(e.getCause().getClass()).isEqualTo(CreateCarSaleInfoException.class);
        }
    }

    @Test
    public void shouldFailCreatingCarWithMissingParams() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"type\": \"Ford\",\"year\":2017}")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestIfCarIdIsNull() throws Exception {
        mockMvc.perform(get("/cars/" + null))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestIfCarIdDoesNotValid() throws Exception {
        mockMvc.perform(get("/cars/" + "nulfdsfl"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnEmptyListIfWhereNoAddedCars() throws Exception {
        when(carService.getCars()).thenReturn(Collections.emptyList());

        Collection<CarSaleInfo> allCars = controller.getAllCars();

        assertThat(allCars).isEmpty();
    }

    @Test
    public void shouldReturnOkAfterRemoving() throws Exception {
        when(carService.removeCar(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/cars/" + "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnNoContentAfterRemoving() throws Exception {
        when(carService.removeCar(anyLong())).thenReturn(false);

        mockMvc.perform(delete("/cars/" + "1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnBadRequestIfCarIdDoesNotValidForDelete() throws Exception {
        mockMvc.perform(delete("/cars/" + "njndjsad"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestForDeleteWithNullCarId() throws Exception {
        mockMvc.perform(delete("/cars/" + null))
                .andExpect(status().isBadRequest());
    }
}