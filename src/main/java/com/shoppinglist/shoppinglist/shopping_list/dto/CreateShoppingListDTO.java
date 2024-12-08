package com.shoppinglist.shoppinglist.shopping_list.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateShoppingListDTO {
    @NotBlank(message = "Name is required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
