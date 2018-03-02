package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.CarShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarShopDao extends JpaRepository<CarShopEntity,Long> {
    CarShopEntity findCarShopEntityByCar_Registration(String registration);
}
