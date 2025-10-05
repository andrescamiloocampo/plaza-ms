package com.pragma.powerup.infrastructure.out.jpa.entity;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_dish")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderDishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int userId;

    @ManyToOne
    @JoinColumn(name = "id_order", nullable = false)
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "id_dish", nullable = false)
    private DishEntity dish;

    @Column(nullable = false)
    private int quantity;
}
