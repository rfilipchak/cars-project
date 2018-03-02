package com.playtika.carshop.web;

import com.playtika.carshopcommon.dealstatus.DealStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CarProjectSystemTest {
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void shouldCreateANewCarSuccessfullyForSystemContext() throws Exception {
        assertThat(Long.parseLong(addNewCar("Roman", "A123"))).isBetween(0L, 10L);

    }

    @Test
    public void shouldReturnAllCarsSuccessfullyForSystemContext() throws Exception {
        long carId = Long.parseLong(addNewCar("Yura", "R122"));

        this.mockMvc.perform(get("/cars"))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/cars/" + carId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.car.registration").value("R122"))
                .andExpect(jsonPath("$.contact").value("Yura"));
    }

    @Test
    public void shouldReturnCarByIdSuccessfullyForSystemContext() throws Exception {
        this.mockMvc.perform(get("/cars/" + addNewCar("Sveta", "R121")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.car.brand").value("Ford"))
                .andExpect(jsonPath("$.car.year").value(2017))
                .andExpect(jsonPath("$.price").value(200000))
                .andExpect(jsonPath("$.contact").value("Sveta"));
    }

    @Test
    public void shouldRemoveCarByIdSuccessfullyForSystemContext() throws Exception {
        this.mockMvc.perform(delete("/cars/" + addNewCar("Oleg", "R120")))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldCreateDealSuccessfullyForSystemContext() throws Exception {
        long carId = Long.parseLong(addNewCar("Roman", "J123"));
        long dealId = Long.parseLong(addDealForCarProposition("Igor", 2000, carId));

        assertThat(dealId).isBetween(0L, 10L);
    }

    @Test
    public void shouldGetAllDealsSuccessfullyForSystemContext() throws Exception {
        long carId = Long.parseLong(addNewCar("Roman", "A1234"));
        long dealIdFirst = Long.parseLong(addDealForCarProposition("Igor", 2000, carId));
        long dealIdSecond = Long.parseLong(addDealForCarProposition("Sveta", 2000, carId));

        this.mockMvc.perform(get("/deals"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$[0].id").value(dealIdFirst))
                .andExpect(jsonPath("$[1].id").value(dealIdSecond));
    }

    @Test
    public void shouldGetAllDealsTheSameCarSuccessfullyForSystemContext() throws Exception {
        long carIdFirst = Long.parseLong(addNewCar("Roman", "A12345"));
        long carIdSecond = Long.parseLong(addNewCar("Yura", "R1234"));
        long dealIdFirst = Long.parseLong(addDealForCarProposition("Igor", 2000, carIdFirst));
        long dealIdSecond = Long.parseLong(addDealForCarProposition("Sveta", 2000, carIdFirst));
        addDealForCarProposition("Sveta", 2000, carIdSecond);

        this.mockMvc.perform(get("/deals/carDeals/" + carIdFirst))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(dealIdFirst))
                .andExpect(jsonPath("$[1].id").value(dealIdSecond));
    }

    @Test
    public void shouldGetBestDealSuccessfullyForSystemContext() throws Exception {
        long carIdFirst = Long.parseLong(addNewCar("Ivan", "A1"));
        long dealIdFirst = Long.parseLong(addDealForCarProposition("Igor", 20000, carIdFirst));
        long dealIdSecond = Long.parseLong(addDealForCarProposition("Sveta", 2000, carIdFirst));

        this.mockMvc.perform(get("/deals/carDeals/" + carIdFirst))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(dealIdFirst))
                .andExpect(jsonPath("$[1].id").value(dealIdSecond));

        this.mockMvc.perform(get("/deals/bestDeal/" + carIdFirst))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$.id").value(dealIdFirst));
    }

    @Test
    public void shouldAcceptBestDealOfCarSuccessfullyForSystemContext() throws Exception {
        long carIdFirst = Long.parseLong(addNewCar("Nikolay", "A12"));
        long dealIdFirst = Long.parseLong(addDealForCarProposition("Igor", 20000, carIdFirst));
        long dealIdSecond = Long.parseLong(addDealForCarProposition("Sveta", 2000, carIdFirst));

        String status = this.mockMvc.perform(post("/deals/acceptCarDeal/" + carIdFirst))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertThat(status).isEqualTo(DealStatus.ACCEPTED.toString());

        this.mockMvc.perform(get("/deals/carDeals/" + carIdFirst))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(dealIdFirst))
                .andExpect(jsonPath("$[1].id").value(dealIdSecond))
                .andExpect(jsonPath("$[0].dealStatus").value(DealStatus.ACCEPTED.toString()))
                .andExpect(jsonPath("$[1].dealStatus").value(DealStatus.REJECTED.toString()));
    }

    private String addNewCar(String contact, String registration) throws Exception {
        String content = String.format("{\"brand\": \"Ford\",\"year\":2017,\"registration\":\"%s\",\"color\":\"black\"}", registration);
        return this.mockMvc.perform(post("/cars")
                .content(content)
                .param("price", "200000").param("contact", contact)
                .contentType("application/json;charset=UTF-8")).andReturn().getResponse().getContentAsString();
    }

    private String addDealForCarProposition(String buyer, int price, long carSaleId) throws Exception {
        return this.mockMvc.perform(post("/deals")
                .param("buyer", buyer)
                .param("price", String.valueOf(price))
                .param("carSaleId", String.valueOf(carSaleId))
                .contentType("application/json;charset=UTF-8")).andReturn().getResponse().getContentAsString();
    }
}

