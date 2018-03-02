package com.playtika.carshop.web;

import com.playtika.carshop.carshopservice.CarService;
import com.playtika.carshop.dealservice.DealService;
import com.playtika.carshop.exeptions.CarNotFoundException;
import com.playtika.carshop.exeptions.CreateCarSaleInfoException;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@Api(description = "Api for car sale operation")
@RequestMapping("/cars")
@Slf4j
@Data
public class CarShopController {

    private final CarService carService;
    private final DealService dealService;

    @PostMapping
    @ApiOperation(value = "Add new car with price and seller contact")
    public Long addCarSaleInfo(@RequestBody Car car,
                               @RequestParam int price,
                               @RequestParam String contact) {
        long id = carService.addCar(car, price, contact)
                .orElseThrow(() -> new CreateCarSaleInfoException("Can't create carSaleInfo for existing carSaleInfo"));
        log.info("Car sale info was created [carSaleInfo: id: {},{},price: {}, contact: {};]", id, car, price, contact);
        return id;
    }

    @GetMapping
    @ApiOperation(value = "Get all car sale proposition")
    public Collection<CarSaleInfo> getAllCars() {
        log.info("All cars was requested");
        return carService.getCars();
    }

    @GetMapping(value = "{id}")
    @ApiOperation(value = "Get car sale proposition with id")
    public CarSaleInfo getCarInfoById(@PathVariable("id") long id) throws CarNotFoundException {
        return carService.getCar(id)
                .orElseThrow(() -> new CarNotFoundException("Car not found", id));
    }

    @DeleteMapping(value = "{id}")
    @ResponseBody
    @ApiOperation(value = "Remove car sale proposition with id")
    public ResponseEntity removeCarById(@PathVariable("id") long id) {
        dealService.removeDealByCarShopId(id);
        if (carService.removeCar(id)) {
            log.info("Car with id was removed [Car id: {};]", id);
            return ResponseEntity.ok("Car with id was removed");
        } else {
            log.warn("Car with id does not exist or just removed[Car id: {};]", id);
            return ResponseEntity.noContent().varyBy("Car with id does not exist or just removed").build();
        }
    }
}

