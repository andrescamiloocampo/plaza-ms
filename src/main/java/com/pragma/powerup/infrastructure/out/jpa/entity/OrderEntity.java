package com.pragma.powerup.infrastructure.out.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "userId", nullable = false)
    private int userId;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "chefId")
    private int chefId;

    @ManyToOne
    @JoinColumn(name = "restaurantId",nullable = false)
    private RestaurantEntity restaurant;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDishEntity> orderDishes;
}
