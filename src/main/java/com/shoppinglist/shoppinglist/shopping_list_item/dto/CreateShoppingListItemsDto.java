package com.shoppinglist.shoppinglist.shopping_list_item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateShoppingListItemsDto {
    @NotNull
    private UUID listId;

    @NotNull
    private List<CreateShoppingListItemDto> shoppingListItems;
}
