package ru.noir74.shop.models.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private List<OrderItem> items;
    private Integer totalCost;
}

record OrderItem(ItemDtoResp itemDtoResp, Integer quantity) {
}
