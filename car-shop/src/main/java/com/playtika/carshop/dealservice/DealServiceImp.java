package com.playtika.carshop.dealservice;

import com.playtika.carshop.carshopservice.CarServiceImpl;
import com.playtika.carshop.converter.Converter;
import com.playtika.carshop.dao.DealsDao;
import com.playtika.carshop.dao.entity.DealEntity;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import com.playtika.carshopcommon.domain.CarSaleInfo;
import com.playtika.carshopcommon.domain.Deal;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class DealServiceImp implements DealService {

    private CarServiceImpl carService;
    private final Converter converter;
    private final DealsDao dealsDao;

    @Override
    public Optional<Deal> addDeal(CarSaleInfo carSaleInfo, String buyerContact, int price) {
        if (carSaleInfo.getContact().equals(buyerContact)) {
            return Optional.empty();
        }
        DealEntity dealEntity = dealsDao.save(new DealEntity(converter.domainToCarShopEntity(carSaleInfo),
                carService.checkPersonForExist(buyerContact), price));

        return Optional.of(converter.dealEntityToDeal(dealEntity));
    }

    @Override
    public Collection<Deal> getAllDeals() {
        return converter.DealEntitiesToDealsList(dealsDao.findAll());
    }

    @Override
    public Collection<Deal> getAllDealsTheSameCar(long carSaleId) {
        Collection<DealEntity> dealEntities = dealsDao.getAllByCarShopEntityId(carSaleId);
        return converter.DealEntitiesToDealsList(dealEntities);
    }

    @Override
    public void acceptDeal(long dealId) {
        dealsDao.findOne(dealId).setDealStatus(DealStatus.ACCEPTED);
        rejectAllWithCarShoId(dealsDao.findOne(dealId).getCarShopEntity().getId());
    }

    @Override
    public void removeDealByCarShopId(long carSaleId) {
        if (dealsDao.findFirstByCarShopEntityId(carSaleId) != null) {
            dealsDao.deleteDealEntitiesByCarShopEntityId(carSaleId);
        }
    }

    @Override
    public DealStatus getDealStatusById(long dealId) {
        return dealsDao.findOne(dealId).getDealStatus();
    }

    @Override
    public Optional<Deal> getBestDeal(long carSaleId) {
        DealEntity dealEntity = dealsDao.getFirstByCarShopEntityIdOrderByBuyerPriceDesc(carSaleId);
        if (dealEntity != null) {
            return Optional.of(converter.dealEntityToDeal(dealEntity));
        }
        return Optional.empty();
    }

    @Override
    public boolean checkDealForAccept(long carSaleId) {
        Collection<Deal> allDealsTheSameCar = getAllDealsTheSameCar(carSaleId);
        return allDealsTheSameCar.stream().anyMatch(deal -> deal.getDealStatus().equals(DealStatus.ACCEPTED));
    }

    private void rejectAllWithCarShoId(long carSaleId) {
        Collection<DealEntity> dealEntities = dealsDao.getAllByCarShopEntityIdAndDealStatus(carSaleId, DealStatus.ACTIVE);
        for (DealEntity deal : dealEntities) {
            deal.setDealStatus(DealStatus.REJECTED);
        }
    }
}
