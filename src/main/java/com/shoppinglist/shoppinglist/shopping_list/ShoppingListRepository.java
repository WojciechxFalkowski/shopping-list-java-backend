package com.shoppinglist.shoppinglist.shopping_list;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShoppingListRepository extends JpaRepository<ShoppingListEntity, UUID> {
    List<ShoppingListEntity> findAllByOwnerId(UUID ownerId);
}
