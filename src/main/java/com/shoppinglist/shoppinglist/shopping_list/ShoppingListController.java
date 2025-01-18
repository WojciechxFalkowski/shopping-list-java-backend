package com.shoppinglist.shoppinglist.shopping_list;

import com.shoppinglist.shoppinglist.security.JwtUtil;
import com.shoppinglist.shoppinglist.shared_list.dto.DeleteSharedUserDTO;
import com.shoppinglist.shoppinglist.shopping_list.dto.CreateShoppingListDTO;
import com.shoppinglist.shoppinglist.shopping_list.dto.DeleteShoppingListDTO;
import com.shoppinglist.shoppinglist.shopping_list.dto.ShareShoppingListDTO;
import com.shoppinglist.shoppinglist.shopping_list.dto.ShoppingListWithShoppingItemsDto;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.CreateShoppingListItemDto;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.CreateShoppingListItemsDto;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.ShoppingListItemDto;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.UpdateShoppingListItemDto;
import com.shoppinglist.shoppinglist.user.UserService;
import com.shoppinglist.shoppinglist.user.dto.UserDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shopping-lists")
public class ShoppingListController {
    @Autowired
    private ShoppingListService shoppingListService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<ShoppingListEntity>> getAllShoppingLists() {
        return ResponseEntity.ok(shoppingListService.getAllShoppingLists());
    }

    @GetMapping("/{listId}")
    public ResponseEntity<ShoppingListWithShoppingItemsDto> getShoppingList(@RequestHeader("Authorization") String token, @PathVariable UUID listId) {
        return ResponseEntity.ok(shoppingListService.getShoppingList(token, listId));
    }

    @GetMapping("/{listId}/sharedWithUsers")
    public ResponseEntity<List<UserDto>> getSharedWithUsersByShoppingList(@RequestHeader("Authorization") String token, @PathVariable UUID listId) {
        return ResponseEntity.ok(shoppingListService.getSharedWithUsersByShoppingList(token, listId));
    }

    @DeleteMapping("/{listId}/sharedWithUsers")
    public ResponseEntity<List<UserDto>> deleteSharedWithUsersByShoppingList(@RequestHeader("Authorization") String token, @RequestBody @Valid DeleteSharedUserDTO deleteSharedUserDTO) {
        return ResponseEntity.ok(shoppingListService.deleteSharedWithUsersByShoppingList(token, deleteSharedUserDTO.getShoppingListId(), deleteSharedUserDTO.getToBeDeletedUserId()));
    }

    @GetMapping("my-shopping-lists")
    public ResponseEntity<List<ShoppingListWithShoppingItemsDto>> getAllUserShoppingLists(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(shoppingListService.getAllUserShoppingLists(token));
    }

    @PostMapping
    public ResponseEntity<ShoppingListWithShoppingItemsDto> createShoppingList(@RequestBody @Valid CreateShoppingListDTO shoppingListDTO, @RequestHeader("Authorization") String token) {
//        String token = token.replace("Bearer ", "");
//        String username = jwtUtil.extractUsername(token);
        ShoppingListEntity shoppingList = new ShoppingListEntity();
        shoppingList.setName(shoppingListDTO.getName());

        return ResponseEntity.ok(shoppingListService.createShoppingList(shoppingList, token));
    }

    @DeleteMapping
    public ResponseEntity<List<ShoppingListWithShoppingItemsDto>> deleteShoppingList(@RequestBody DeleteShoppingListDTO deleteShoppingListDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(shoppingListService.deleteShoppingList(UUID.fromString(deleteShoppingListDTO.getShoppingListId()), token));
    }

    @PostMapping("/sharedWithUsers")
    public ResponseEntity<List<UserDto>> addUserToShareShoppingList(@RequestBody ShareShoppingListDTO shareDTO, @RequestHeader("Authorization") String authHeader) {
        String username = jwtUtil.getUsernameFromToken(authHeader);

        UUID listId = UUID.fromString(shareDTO.getShoppingListId());
        UUID sharedUserId = UUID.fromString(shareDTO.getUserId());

        return ResponseEntity.ok(shoppingListService.addUserToShareShoppingList(listId, sharedUserId, username));
    }

    @PostMapping("/add-shopping-item")
    public ResponseEntity<ShoppingListItemDto> addShoppingItemToShoppingList(@RequestHeader("Authorization") String token, @RequestBody CreateShoppingListItemDto dto) {
        return ResponseEntity.ok(shoppingListService.addShoppingItemToShoppingList(token, dto.getListId(), dto));
    }

    @PostMapping("/add-shopping-items")
    public ResponseEntity<List<ShoppingListItemDto>> addShoppingItemsToShoppingList(@RequestHeader("Authorization") String token, @RequestBody CreateShoppingListItemsDto shoppingListItemsDto) {
        return ResponseEntity.ok(shoppingListService.addShoppingItemsToShoppingList(token, shoppingListItemsDto.getListId(), shoppingListItemsDto));
    }

    @PutMapping("/update-shopping-item")
    public ResponseEntity<ShoppingListWithShoppingItemsDto> updateItem(@RequestHeader("Authorization") String token, @RequestBody UpdateShoppingListItemDto dto) {

        return ResponseEntity.ok(shoppingListService.updateShoppingItemInShoppingList(token, dto));
    }

    @DeleteMapping("/delete-shopping-item/{itemId}")
    public ResponseEntity<ShoppingListWithShoppingItemsDto> deleteItem(@PathVariable UUID itemId, @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(shoppingListService.deleteShoppingItemInShoppingList(token, itemId));
    }
}
