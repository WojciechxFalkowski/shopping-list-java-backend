package com.shoppinglist.shoppinglist.shopping_list;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoppinglist.shoppinglist.security.JwtUtil;
import com.shoppinglist.shoppinglist.shared_list.SharedListEntity;
import com.shoppinglist.shoppinglist.shared_list.SharedListRepository;
import com.shoppinglist.shoppinglist.shared_list.SharedListService;
import com.shoppinglist.shoppinglist.shopping_list.dto.ShoppingListWithShoppingItemsDto;
import com.shoppinglist.shoppinglist.shopping_list_item.ShoppingListItemEntity;
import com.shoppinglist.shoppinglist.shopping_list_item.ShoppingListItemService;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.CreateShoppingListItemDto;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.CreateShoppingListItemsDto;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.ShoppingListItemDto;
import com.shoppinglist.shoppinglist.shopping_list_item.dto.UpdateShoppingListItemDto;
import com.shoppinglist.shoppinglist.user.UserEntity;
import com.shoppinglist.shoppinglist.user.UserService;
import com.shoppinglist.shoppinglist.user.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShoppingListService {
    @Autowired
    private ShoppingListRepository shoppingListRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SharedListRepository sharedListRepository;

    @Autowired
    private ShoppingListItemService shoppingListItemService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SharedListService sharedListService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ShoppingListEntity> getAllShoppingLists() {
        return shoppingListRepository.findAll();
    }

    public ShoppingListWithShoppingItemsDto getShoppingList(String token, UUID listId) {
        // Pobierz szczegóły listy zakupów
        ShoppingListEntity shoppingList = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found."));

        // Pobierz elementy listy zakupów
        List<ShoppingListItemDto> shoppingItems = shoppingListItemService.getShoppingItemsByListId(token, listId);

        List<UserDto> sharedWithUsers = new ArrayList<>();
        List<SharedListEntity> sharedShoppingLists = sharedListService.getSharedListByShoppingListId(shoppingList.getId());

        for (SharedListEntity sharedShoppingList : sharedShoppingLists) {
            UserDto sharedUserDTO = userService.getUserById(sharedShoppingList.getUserId());
            sharedWithUsers.add(sharedUserDTO);
        }
//        sharedList.getSharedListByShoppingListId

        // Sortowanie elementów listy zakupów według createdAt (malejąco)
        shoppingItems.sort((item1, item2) -> item1.getCreatedAt().compareTo(item2.getCreatedAt()));

        // Utwórz i zwróć DTO
        return ShoppingListWithShoppingItemsDto.builder()
                .id(shoppingList.getId())
                .name(shoppingList.getName())
                .listCode(shoppingList.getListCode())
                .ownerId(shoppingList.getOwnerId())
                .createdAt(shoppingList.getCreatedAt())
                .updatedAt(shoppingList.getUpdatedAt())
                .shoppingItems(shoppingItems)
                .sharedWithUsers(sharedWithUsers)
                .build();
    }

    public List<ShoppingListWithShoppingItemsDto> getAllUserShoppingLists(String token) {
        // Znajdź użytkownika na podstawie nazwy użytkownika
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);

        // Pobierz listy zakupów, których użytkownik jest właścicielem
        List<ShoppingListEntity> ownedLists = shoppingListRepository.findAllByOwnerId(user.getId());
//        System.out.println("ownedLists");
//        System.out.println(ownedLists);
        // Pobierz listy zakupów, które są udostępnione użytkownikowi
        List<ShoppingListEntity> sharedLists = sharedListRepository.findAllByUserId(user.getId()).stream()
                .map(sharedList -> shoppingListRepository.findById(sharedList.getShoppingListId())
                        .orElseThrow(() -> new IllegalArgumentException("Shopping list not found.")))
                .toList();

        // Połącz ownedLists i sharedLists do jednej listy
        List<ShoppingListEntity> allLists = new ArrayList<>();
        allLists.addAll(ownedLists);
        allLists.addAll(sharedLists);

        // Mapowanie wszystkich list do DTO z oznaczeniem isShared
        List<ShoppingListWithShoppingItemsDto> result = allLists.stream()
                .map(list -> {
                    boolean isShared = sharedLists.contains(list);
                    List<UserDto> sharedWithUsers = new ArrayList<>();
                    List<SharedListEntity> sharedShoppingLists = sharedListService.getSharedListByShoppingListId(list.getId());

                    for (SharedListEntity sharedShoppingList : sharedShoppingLists) {
                        UserDto sharedUserDto = userService.getUserById(sharedShoppingList.getUserId());
                        sharedWithUsers.add(sharedUserDto);
                    }


//                    List<ShoppingListItemDto> shoppingItems = shoppingListItemService.getShoppingItemsByListId(token, list.getId());
                    List<ShoppingListItemDto> shoppingListItemDTOs = list.getShoppingItems().stream().map(shoppingListItemEntity -> shoppingListItemService.mapToDto(shoppingListItemEntity)).toList();
                    return ShoppingListWithShoppingItemsDto.builder()
                            .id(list.getId())
                            .name(list.getName())
                            .listCode(list.getListCode())
                            .ownerId(list.getOwnerId())
                            .createdAt(list.getCreatedAt())
                            .updatedAt(list.getUpdatedAt())
                            .shoppingItems(shoppingListItemDTOs)//shoppingItems list.getItems()
                            .isShared(isShared)
                            .sharedWithUsers(sharedWithUsers)
                            .build();
                })
                .sorted((dto1, dto2) -> dto2.getCreatedAt().compareTo(dto1.getCreatedAt())) // Sortowanie po createdAt (malejąco)
                .toList();

        return result;
    }


    public ShoppingListEntity createShoppingList(ShoppingListEntity shoppingListEntity, String username) {
        UserEntity user = userService.findByUsername(username);
        shoppingListEntity.setListCode(generateListCode());
        shoppingListEntity.setOwnerId(user.getId());
        return shoppingListRepository.save(shoppingListEntity);
    }

    public List<ShoppingListWithShoppingItemsDto> deleteShoppingList(UUID shoppingListId, String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);

        ShoppingListEntity shoppingList = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found."));

        if (!isOwnerOrShared(shoppingListId, user)) {
            throw new SecurityException("You are not authorized to delete this shopping list.");
        }

        boolean isOwnerOfShoppingList = shoppingList.getOwnerId().equals(user.getId());
        if (isOwnerOfShoppingList) {
            shoppingListRepository.deleteById(shoppingListId);
            return getAllUserShoppingLists(token);
        }

        System.out.println("v4");

        sharedListService.deleteSharedList(shoppingListId, user.getId());
        System.out.println("v5");
        return getAllUserShoppingLists(token);
    }

    private String generateListCode() {
        return UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }

    public boolean isOwnerOrShared(UUID listId, UserEntity user) {
        ShoppingListEntity list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found."));

        if (list.getOwnerId().equals(user.getId())) {
            return true;
        }

        return sharedListService.existsByShoppingListIdAndUserId(listId, user.getId());
    }

    public ShoppingListEntity getListById(UUID listId, UserEntity user) {
        ShoppingListEntity list = shoppingListRepository.findById(listId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found."));

        if (!isOwnerOrShared(listId, user)) {
            throw new AccessDeniedException("You do not have permission to access this list");
        }

        return list;
    }


    public List<UserDto> addUserToShareShoppingList(UUID shoppingListId, UUID sharedUserId, String username) {
        UserEntity owner = userService.findByUsername(username);
        ShoppingListEntity list = shoppingListRepository.findById(shoppingListId)
                .orElseThrow(() -> new IllegalArgumentException("Shopping list not found."));

        if (!list.getOwnerId().equals(owner.getId())) {
            throw new AccessDeniedException("You are not the owner of this shopping list.");
        }

        if (sharedListRepository.existsByShoppingListIdAndUserId(shoppingListId, sharedUserId)) {
            throw new IllegalArgumentException("User already has access to this shopping list.");
        }

        SharedListEntity sharedList = SharedListEntity.builder()
                .shoppingListId(shoppingListId)
                .userId(sharedUserId)
                .build();

        sharedListRepository.save(sharedList);
        List<SharedListEntity> sharedListEntities = sharedListService.getSharedListByShoppingListId(shoppingListId);
        return sharedListEntities.stream().map(sharedListEntity -> userService.getUserById(sharedListEntity.getUserId())).toList();
    }

    public List<ShoppingListEntity> getSharedShoppingListsForUser(UUID userId) {
        List<SharedListEntity> sharedLists = sharedListRepository.findAllByUserId(userId);
        return sharedLists.stream()
                .map(sl -> shoppingListRepository.findById(sl.getShoppingListId())
                        .orElseThrow(() -> new IllegalArgumentException("Shopping list not found.")))
                .toList();
    }

    public boolean isSharedWithUser(UUID listId, UUID userId) {
        return sharedListRepository.existsByShoppingListIdAndUserId(listId, userId);
    }

    public ShoppingListItemDto addShoppingItemToShoppingList(String token, UUID listId, CreateShoppingListItemDto dto) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);

        ShoppingListEntity list = getListById(listId, user);

        ShoppingListItemEntity shoppingListItem = ShoppingListItemEntity.builder()
                .list(list)
                .name(dto.getName())
                .quantity(dto.getQuantity())
                .purchased(dto.getPurchased())
                .build();


        return shoppingListItemService.addShoppingItem(shoppingListItem);
    }

    public ShoppingListItemDto updateShoppingItemInShoppingList(String token, UUID itemId, UpdateShoppingListItemDto dto) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);

        ShoppingListItemEntity shoppingListItem = shoppingListItemService.findById(itemId);

        if (!isOwnerOrShared(shoppingListItem.getList().getId(), user)) {
            throw new AccessDeniedException("You do not have permission to update this item");
        }

        shoppingListItem.setName(dto.getName());
        shoppingListItem.setQuantity(dto.getQuantity());
        shoppingListItem.setPurchased(dto.getPurchased());

        return shoppingListItemService.addShoppingItem(shoppingListItem);
    }

    public ShoppingListWithShoppingItemsDto deleteShoppingItemInShoppingList(String token, UUID itemId) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);

        ShoppingListItemEntity item = shoppingListItemService.findById(itemId);
        if (!isOwnerOrShared(item.getList().getId(), user)) {
            throw new AccessDeniedException("You do not have permission to delete this item");
        }

        ShoppingListEntity shoppingList = item.getList();
        shoppingList.getShoppingItems().remove(item);
        shoppingListRepository.save(shoppingList);

        return getShoppingList(token, item.getList().getId());
    }

    public List<UserDto> getSharedWithUsersByShoppingList(String token, UUID listId) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);

        ShoppingListEntity shoppingList = getListById(listId, user);
        List<SharedListEntity> sharedShoppingLists = sharedListService.getSharedListByShoppingListId(shoppingList.getId());

        List<UserDto> sharedWithUsers = new ArrayList<>();
        for (SharedListEntity sharedShoppingList : sharedShoppingLists) {
            UserDto sharedUserDto = userService.getUserById(sharedShoppingList.getUserId());
            sharedWithUsers.add(sharedUserDto);
        }

        return sharedWithUsers;
    }

    public List<UserDto> deleteSharedWithUsersByShoppingList(String token, UUID shoppingListId, UUID toBeDeletedUserId) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);

        ShoppingListEntity shoppingList = getListById(shoppingListId, user);
        List<SharedListEntity> sharedShoppingLists = sharedListService.getSharedListByShoppingListId(shoppingList.getId());
        SharedListEntity sharedListEntity = sharedShoppingLists.stream().filter(sharedShoppingList -> sharedShoppingList.getUserId().equals(toBeDeletedUserId)).findFirst().orElse(null);

        if (sharedListEntity != null) {
            sharedListService.deleteSharedListById(sharedListEntity.getId());
        }

        sharedShoppingLists = sharedListService.getSharedListByShoppingListId(shoppingList.getId());
        List<UserDto> sharedWithUsers = new ArrayList<>();
        for (SharedListEntity sharedShoppingList : sharedShoppingLists) {
            UserDto sharedUserDTO = userService.getUserById(sharedShoppingList.getUserId());
            sharedWithUsers.add(sharedUserDTO);
        }
        return sharedWithUsers;
    }

    public List<ShoppingListItemDto> addShoppingItemsToShoppingList(String token, UUID listId, CreateShoppingListItemsDto shoppingListItemsDto) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = userService.findByUsername(username);
        ShoppingListEntity shoppingList = getListById(listId, user);
        List<ShoppingListItemEntity> shoppingListItemEntities = shoppingListItemsDto.getShoppingListItems().stream().map(shoppingListItemDto ->
                ShoppingListItemEntity.builder()
                        .list(shoppingList)
                        .name(shoppingListItemDto.getName())
                        .quantity(shoppingListItemDto.getQuantity())
                        .purchased(shoppingListItemDto.getPurchased())
                        .build()
        ).toList();
        return shoppingListItemService.addShoppingItem(shoppingListItemEntities);
    }
}
