package com.playtika.cars_proposition_project.service;

import au.com.bytecode.opencsv.CSVReader;
import com.playtika.carshop.client.CarClient;
import com.playtika.carshopcommon.domain.Car;
import com.playtika.carshopcommon.domain.Deal;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@AllArgsConstructor
public class CarPropositionServiceImpl implements CarPropositionService {

    @Autowired
    private CarClient carClient;

    @Override
    public List<Long> carsFileProcessing(String filePath) throws IOException {
        CSVReader reader = new CSVReader(new FileReader(filePath), ',', '"', 0);
        String[] nextLine;
        List<Long> addedId = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
            if (nextLine != null) {
                addedId.add(carClient.addCarSaleInfo(new Car(nextLine[0], Integer.parseInt(nextLine[1]), nextLine[2], nextLine[3])
                        , Integer.parseInt(nextLine[4])
                        , nextLine[5]));
            }
        }
        return addedId;
    }

    @Override
    public Long addDeal(String buyer, int price, long carSaleId) {
        return carClient.addDeal(buyer, price, carSaleId);
    }

    @Override
    public String acceptBestDealOfCar(long carSaleId) {
        return carClient.autoAcceptBestDealOfCar(carSaleId);
    }

    @Override
    public Collection<Deal> getAllDealsTheSameCar(long carSaleId) {
        return carClient.getAllDealsTheSameCar(carSaleId);
    }
}
