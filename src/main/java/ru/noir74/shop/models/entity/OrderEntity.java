package ru.noir74.shop.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "orders", schema = "store")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "orderEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<OrderItemEntity> orderItemEntities;
}