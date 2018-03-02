package com.playtika.carshop.client;

import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.Deal;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@FeignClient(
        name = "carsService"
)
public interface CarClient {

    @PostMapping(value = "/cars")
    Long addCarSaleInfo(@RequestBody Car car,
                        @RequestParam("price") int price,
                        @RequestParam("contact") String contact);

    @PostMapping(value = "/deals")
    Long addDeal(@RequestParam("buyer") String buyer,
                 @RequestParam("price") int price,
                 @RequestParam("carSaleId") long carSaleId);

    @PostMapping(value = "/deals/acceptCarDeal/{carId}")
    String autoAcceptBestDealOfCar(@PathVariable("carId") long carId);

    @GetMapping(value = "/deals/carDeals/{carId}")
    Collection<Deal> getAllDealsTheSameCar(@PathVariable("carId") long carId);
}
