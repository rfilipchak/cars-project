package com.playtika.carshop.dao.entity;

import com.playtika.carshopcommon.dealstatus.DealStatus;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "deals")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class DealEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "car_for_sale_id")
    private CarShopEntity carShopEntity;

    @ManyToOne
    private PersonEntity person;

    private int buyerPrice;

    @Enumerated(EnumType.STRING)
    private DealStatus dealStatus;

    public DealEntity(CarShopEntity carShopEntity, PersonEntity person, int buyerPrice) {
        this.carShopEntity = carShopEntity;
        this.person = person;
        this.buyerPrice = buyerPrice;
        dealStatus = DealStatus.ACTIVE;
    }
}