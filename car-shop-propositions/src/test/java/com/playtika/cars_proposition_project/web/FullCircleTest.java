package com.playtika.cars_proposition_project.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class FullCircleTest {

    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @Before
    public void init() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test //!!!!!!!!!!!CarShopApplication should be run before!!!!!!!!!!!!!!
    public void shouldBePassedForFullCircleFlow() throws Exception {
        //Add carSaleIdFromFile
        String filePath = "D:\\cars-project\\car-shop-propositions\\src\\main\\resources\\data.csv";

        mockMvc.perform(post("/proposition/fromFile")
                .content(filePath)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$[0]").value(1))
                .andExpect(jsonPath("$[1]").value(2));
        log.info("Car from file added, returned [1,2]");

        //Add Deals for carSaleId = 1
        //The First Deal for carSopId = 1
        long expectedFirstDealId = 1L;
        mockMvc.perform(post("/proposition/addDeal")
                .param("buyer", "Buyer")
                .param("price", "200")
                .param("carSaleId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value(expectedFirstDealId));
        log.info("The first Deal for carSaleId = 1 added, returned dealId = {}", expectedFirstDealId);

        //The Second Deal for carSopId = 1
        long expectedSecondDealId = 2L;
        mockMvc.perform(post("/proposition/addDeal")
                .param("buyer", "Buyer2")
                .param("price", "20000")
                .param("carSaleId", "1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value(expectedSecondDealId));
        log.info("The second Deal for carSaleId = 1 added, returned dealId = {}", expectedSecondDealId);

        //The Third Deal for carSopId = 2
        long expectedThirdDealId = 3L;
        mockMvc.perform(post("/proposition/addDeal")
                .param("buyer", "Buyer3")
                .param("price", "20000")
                .param("carSaleId", "2")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value(expectedThirdDealId));
        log.info("The first Deal for carSaleId = 2 added, returned dealId = {}", expectedThirdDealId);

        //Get all Deals for  carSaleId = 1
        mockMvc.perform(get("/proposition/carDeals/1")
                .contentType("application/json;charset=UTF-8")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$.[0].carSaleInfo.car.registration").value("AA-192-JJ"))
                .andExpect(jsonPath("$.[0].carSaleInfo.id").value("1"))
                .andExpect(jsonPath("$.[0].dealStatus").value("ACTIVE"))
                .andExpect(jsonPath("$.[1].id").value("2"))
                .andExpect(jsonPath("$.[1].carSaleInfo.id").value("1"))
                .andExpect(jsonPath("$.[1].carSaleInfo.car.registration").value("AA-192-JJ"))
                .andExpect(jsonPath("$.[1].dealStatus").value("ACTIVE"));
        log.info("Return Deals {}, {} for carSaleId = 1", expectedFirstDealId, expectedSecondDealId);

        //Accept the best deal for carSaleId = 1
        mockMvc.perform(post("/proposition/acceptCarDeal/1")
                .contentType("application/json;charset=UTF-8"))
                .andExpect(jsonPath("$").value("ACCEPTED"));
        //Check Deal for Accept
        mockMvc.perform(get("/proposition/carDeals/1")
                .contentType("application/json;charset=UTF-8")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].dealStatus").value("REJECTED"))
                .andExpect(jsonPath("$.[0].buyerPrice").value("200"))
                .andExpect(jsonPath("$.[1].dealStatus").value("ACCEPTED"))
                .andExpect(jsonPath("$.[1].buyerPrice").value("20000"));
        log.info("Deal with id = {}, for carSaleId = 1 was ACCEPTED", expectedSecondDealId);
        log.info("Deal with id = {}, for carSaleId = 1 was REJECTED", expectedFirstDealId);

        //Check that Exception from server side redirected
        mockMvc.perform(post("/proposition/fromFile")
                .content(filePath)
                .contentType("application/json;charset=UTF-8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.description").value("Can't create carSaleInfo for existing carSaleInfo"));
        log.warn("Server side throw an Exception for adding the same carSaleInfo from file");
    }
}
