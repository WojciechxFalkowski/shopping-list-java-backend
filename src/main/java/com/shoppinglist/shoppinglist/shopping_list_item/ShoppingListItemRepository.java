package com.shoppinglist.shoppinglist.shopping_list_item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShoppingListItemRepository extends JpaRepository<ShoppingListItemEntity, UUID> {
    List<ShoppingListItemEntity> findByList_Id(UUID listId);
}