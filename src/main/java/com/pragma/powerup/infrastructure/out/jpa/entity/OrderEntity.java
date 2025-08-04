package com.pragma.powerup.infrastructure.out.jpa.entity;

import com.pragma.powerup.domain.model.RestaurantModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "customerId", nullable = false)
    private int customerId;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "chefId", nullable = false)
    private int chefId;

    @ManyToOne
    @JoinColumn(name = "restaurantId",nullable = false)
    private RestaurantEntity restaurant;
}
