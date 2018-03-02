package com.playtika.carshop.dao;

import com.playtika.carshop.dao.entity.PersonEntity;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.beans.SamePropertyValuesAs.samePropertyValuesAs;
import static org.junit.Assert.assertThat;

public class PersonDaoTest extends AbstractDaoTest<PersonDao> {

    @Test
    public void shouldReturnNullWhenPersonDoesNotExist() {
        PersonEntity notExistingPerson = dao.getPersonByContact("unknown");

        assertThat(notExistingPerson, nullValue());
    }

    @Test
    public void shouldReturnPersonEntityByContact() {
        String contact = "contact";
        long id = addPersonToPersonDb(contact);
        PersonEntity expectedPerson = new PersonEntity(contact);
        expectedPerson.setId(id);

        PersonEntity person = dao.getPersonByContact(contact);

        assertThat(person, samePropertyValuesAs(expectedPerson));
    }

    private long addPersonToPersonDb(String contact) {
        PersonEntity person = new PersonEntity(contact);
        return dao.save(person).getId();
    }
}