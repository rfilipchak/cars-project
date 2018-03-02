package com.playtika.carshop.dao;//package com.playtika.carshop.dao.repository;

import com.playtika.carshop.dao.entity.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarDao extends JpaRepository<CarEntity, Long> {
    CarEntity getCarByRegistration(String registration);
}
