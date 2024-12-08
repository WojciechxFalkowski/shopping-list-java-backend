package com.shoppinglist.shoppinglist.shopping_list.dto;

import com.shoppinglist.shoppinglist.shopping_list_item.dto.ShoppingListItemDto;
import com.shoppinglist.shoppinglist.user.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListWithShoppingItemsDto {
    private UUID id;
    private String name;
    private String listCode;
    private UUID ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ShoppingListItemDto> shoppingItems;
    private Boolean isShared;
    private List<UserDto> sharedWithUsers;
}
