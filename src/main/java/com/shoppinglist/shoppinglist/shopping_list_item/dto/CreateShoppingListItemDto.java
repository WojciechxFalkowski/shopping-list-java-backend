package com.shoppinglist.shoppinglist.shopping_list_item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateShoppingListItemDto {
    @NotNull
    private UUID listId;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @Min(1)
    private Integer quantity;

    private Boolean purchased;
}