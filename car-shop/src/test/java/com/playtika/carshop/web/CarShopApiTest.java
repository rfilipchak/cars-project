package com.playtika.carshop.web;

import com.playtika.carshop.carshopservice.CarService;
import com.playtika.carshop.dealservice.DealService;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.supportvalues.SupportTestValues;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(CarShopController.class)
public class CarShopApiTest extends SupportTestValues {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CarService carService;
    @MockBean
    private DealService dealService;

    @Test
    public void shouldCreateANewCarSuccessfully() throws Exception {
        long createdCarId = 1L;
        Car car = new Car("Ford", 2017, "AA-0177-BH", "black");
        int price = 200000;
        String contact = "contact";

        when(carService.addCar(car, price, contact)).thenReturn(Optional.of(createdCarId));

        String result = mockMvc.perform(post("/cars")
                .content("{\"brand\": \"Ford\",\"year\":2017,\"registration\":\"AA-0177-BH\",\"color\":\"black\"}")
                .param("price", "200000").param("contact", "contact")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Long id = Long.parseLong(result);

        assertThat(id).isNotNull().isEqualTo(1L);
    }

    @Test
    public void shouldFailCreatingANewCarWithMissingParams() throws Exception {
        mockMvc.perform(post("/cars")
                .content("{\"brand\": \"Ford\",\"year\":2017,\"registration\":\"AA-0177-BH\",\"color\":\"black\"}")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value(BAD_REQUEST.getReasonPhrase()));
    }

    @Test
    public void shouldReturnAllCarsIfExist() throws Exception {
        Collection<CarSaleInfo> cars = asList(carSaleInfo(1L, "Ford"), carSaleInfo(2L, "Toyota"));

        when(carService.getCars()).thenReturn(cars);

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].car.brand").value("Ford"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].car.brand").value("Toyota"));
    }

    @Test
    public void shouldReturnEmptyListIfThereAreNoCars() throws Exception {
        when(carService.getCars()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldReturnCarById() throws Exception {
        Long carId = 1L;

        when(carService.getCar(carId)).thenReturn(java.util.Optional.of(carSaleInfo(carId, "Test Car")));

        mockMvc.perform(get("/cars/" + carId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId))
                .andExpect(jsonPath("$.car.brand").value("Test Car"));
    }

    @Test
    public void shouldReturnErrorIfCarDoesNotExist() throws Exception {
        when(carService.getCar(12L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/cars/" + 12L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.message").value(NOT_FOUND.getReasonPhrase()));
    }

    @Test
    public void shouldReturnOkAfterRemovingCarById() throws Exception {
        when(carService.removeCar(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/cars/" + 1L))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldReturnErrorOnRemoveNonExistingCar() throws Exception {
        when(carService.removeCar(12L)).thenReturn(false);

        mockMvc.perform(delete("/cars/" + 12L))
                .andExpect(status().isNoContent());
    }

    private CarSaleInfo carSaleInfo(Long id, String model) {
        Car testCar = new Car(model, 2017, "J1234", "black");
        return new CarSaleInfo(id, testCar, 200000, "contact");
    }
}
