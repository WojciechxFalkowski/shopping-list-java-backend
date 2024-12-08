package com.shoppinglist.shoppinglist.shopping_list_item;

import com.shoppinglist.shoppinglist.security.JwtUtil;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.ShoppingListItemDto;
import com.shoppinglist.shoppinglist.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShoppingListItemService {

    @Autowired
    private ShoppingListItemRepository shoppingListItemRepository;

//    @Autowired
//    private ShoppingListService shoppingListService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    public List<ShoppingListItemDto> getShoppingItemsByListId(String token, UUID listId) {
        String username = jwtUtil.getUsernameFromToken(token);

        List<ShoppingListItemEntity> items = shoppingListItemRepository.findByList_Id(listId);

        return items.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ShoppingListItemDto addShoppingItem(ShoppingListItemEntity shoppingListItem) {
        shoppingListItem = shoppingListItemRepository.save(shoppingListItem);
        return mapToDto(shoppingListItem);
    }

    public List<ShoppingListItemDto> addShoppingItem(List<ShoppingListItemEntity> shoppingListItems) {
        System.out.println(shoppingListItems.size());
        List<ShoppingListItemEntity> updatedShoppingListItems = shoppingListItemRepository.saveAll(shoppingListItems);
        return updatedShoppingListItems.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public ShoppingListItemEntity findById(UUID itemId) {
        return shoppingListItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    public void deleteShoppingItem(ShoppingListItemEntity shoppingListItem) {
        shoppingListItemRepository.delete(shoppingListItem);
    }

    public ShoppingListItemDto mapToDto(ShoppingListItemEntity item) {
        return ShoppingListItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .purchased(item.getPurchased())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}