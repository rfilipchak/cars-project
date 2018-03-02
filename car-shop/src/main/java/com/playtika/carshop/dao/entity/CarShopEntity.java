package com.playtika.carshop.dao.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cars_shop")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class CarShopEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = {CascadeType.PERSIST})
    private CarEntity car;

    private int price;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private PersonEntity person;

    public CarShopEntity(CarEntity car, int price, PersonEntity person) {
        this.price = price;
        this.car = car;
        this.person = person;
    }
}