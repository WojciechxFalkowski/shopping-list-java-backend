package com.shoppinglist.shoppinglist.shopping_list_item;

import com.shoppinglist.shoppinglist.shopping_list_item.dto.ShoppingListItemDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopping-list-items")
public class ShoppingListItemController {

    @Autowired
    private ShoppingListItemService shoppingListItemService;

    @GetMapping
    public List<ShoppingListItemDto> getItemsByListId(@RequestHeader("Authorization") String token,
                                                      @RequestParam UUID listId) {
        return shoppingListItemService.getShoppingItemsByListId(token, listId);
    }
}