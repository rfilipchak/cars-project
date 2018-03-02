package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonDao extends JpaRepository<PersonEntity, Long> {
    PersonEntity getPersonByContact(String contact);
}
