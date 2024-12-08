package com.shoppinglist.shoppinglist.shopping_list.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShareShoppingListDTO {
    @NotBlank(message = "Shopping list ID is required")
    private String shoppingListId;

    @NotBlank(message = "User ID is required")
    private String userId;
}