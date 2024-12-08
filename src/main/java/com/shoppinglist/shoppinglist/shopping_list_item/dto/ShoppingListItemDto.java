package com.shoppinglist.shoppinglist.shopping_list_item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListItemDto {

    private UUID id;

    private String name;

    private Integer quantity;

    private Boolean purchased;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}