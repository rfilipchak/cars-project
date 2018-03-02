package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.DealEntity;
import com.playtika.carshopcommon.dealstatus.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface DealsDao extends JpaRepository<DealEntity, Long> {
    DealEntity findFirstByCarShopEntityId(long id);

    DealEntity getFirstByCarShopEntityIdOrderByBuyerPriceDesc(long id);

    Collection<DealEntity> getAllByCarShopEntityIdAndDealStatus(long id, DealStatus dealStatus);

    Collection<DealEntity> getAllByCarShopEntityId(long id);

    void deleteDealEntitiesByCarShopEntityId(long id);
}
