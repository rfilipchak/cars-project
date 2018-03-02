package com.playtika.carshopproposition.web;

import com.playtika.carshopproposition.service.CarPropositionService;
import com.playtika.carshopcommon.domain.Deal;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/proposition")
@Slf4j
@Data
public class PropositionController {

    private final CarPropositionService carPropositionService;

    @PostMapping("/fromFile")
    public List<Long> addCarPropositionFromFile(@RequestBody String filePath) throws Exception {
               return carPropositionService.carsFileProcessing(filePath);
    }

    @PostMapping("/addDeal")
    public Long addDeal(@RequestParam("buyer") String buyer,
                 @RequestParam("price") int price,
                 @RequestParam("carSaleId") long carSaleId){
        return carPropositionService.addDeal(buyer,price,carSaleId);
    }
    @PostMapping("/acceptCarDeal/{carId}")
    public String acceptBestDealOfCar(@PathVariable("carId") long carId){
        return carPropositionService.acceptBestDealOfCar(carId);
    }

    @GetMapping("/carDeals/{carId}")
    public Collection<Deal> getAllDealsTheSameCar(@PathVariable("carId") long carId){
        return carPropositionService.getAllDealsTheSameCar(carId);
    }
}
