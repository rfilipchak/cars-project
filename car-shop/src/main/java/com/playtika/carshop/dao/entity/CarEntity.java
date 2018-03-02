package com.playtika.carshop.dao.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "cars")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String registration;
    private String brand;

    @Column(name = "car_year")
    private int year;

    private String color;

    public CarEntity(String brand, int year, String registration, String color) {
        this.registration = registration;
        this.brand = brand;
        this.year = year;
        this.color = color;
    }
}