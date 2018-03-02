package com.playtika.carshop.web;

import com.playtika.carshop.carshopservice.CarService;
import com.playtika.carshop.dealservice.DealService;
import com.playtika.carshop.exeptions.CarNotFoundException;
import com.playtika.carshop.exeptions.CreateDealException;
import com.playtika.carshop.exeptions.DealNotFoundException;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/deals")
@Api(description = "Operation with deal for car proposition")
@Slf4j
@Data
public class DealController {

    private final CarService carService;
    private final DealService dealService;

    @PostMapping
    @ApiOperation(value = "Add deal for car sale proposition")
    public Long addDeal(@RequestParam String buyer,
                        @RequestParam int price,
                        @RequestParam long carSaleId) {
        if (dealService.checkDealForAccept(carSaleId)) {
            throw new CreateDealException("Can't create deal. Deal just accepted");
        }
        Deal deal = dealService.addDeal(getCarProposition(carSaleId), buyer, price)
                .orElseThrow(() -> new CreateDealException("Can't create deal with the same seller an buyer name"));
        long id = deal.getId();
        log.info("Deal for carSaleInfo = {} was created [Deal id: {}, price: {}, contact: {};]", carSaleId, id, price, buyer);
        return id;
    }

    @GetMapping
    @ApiOperation(value = "Get all deals for all car sale proposition")
    public Collection<Deal> getAllDeals() {
        log.info("All deals was requested");
        return dealService.getAllDeals();
    }

    @GetMapping("/carDeals/{carId}")
    @ApiOperation(value = "Get all deals for same car sale proposition")
    public Collection<Deal> getAllDealsTheSameCar(@PathVariable("carId") long carId) throws DealNotFoundException {
        log.info("Deal by carId = {} was requested", carId);
        return dealService.getAllDealsTheSameCar(getCarProposition(carId).getId());
    }

    @GetMapping("/bestDeal/{carId}")
    @ApiOperation(value = "Get best price deal for car sale proposition")
    public Deal getBestDeal(@PathVariable("carId") long carId) throws DealNotFoundException {
        return dealService.getBestDeal(getCarProposition(carId).getId())
                .orElseThrow(() -> new DealNotFoundException("Deal not found for CarSaleId = {}", carId));
    }

    @PostMapping("/acceptCarDeal/{carId}")
    @ApiOperation(value = "Accept deal for car sale proposition")
    public String autoAcceptBestDealOfCar(@PathVariable("carId") long carId) {
        long id = getBestDeal(carId).getId();
        if (dealService.getDealStatusById(id).equals(DealStatus.ACCEPTED)) {
            log.warn("Deal for CarSaleId just ACCEPTED [Deal id: {};]", id);
            return dealService.getDealStatusById(id).toString();
        } else {
            dealService.acceptDeal(id);
            log.info("Deal for CarSaleId was ACCEPTED [Deal id: {};]", id);
            return dealService.getDealStatusById(id).toString();
        }
    }

    public CarSaleInfo getCarProposition(long carId) throws CarNotFoundException {
        return carService.getCar(carId)
                .orElseThrow(() -> new CarNotFoundException("Car not found", carId));
    }
}
