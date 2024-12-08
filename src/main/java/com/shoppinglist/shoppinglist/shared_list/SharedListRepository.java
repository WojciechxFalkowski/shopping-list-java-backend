package com.shoppinglist.shoppinglist.shared_list;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SharedListRepository extends JpaRepository<SharedListEntity, UUID> {
    boolean existsByShoppingListIdAndUserId(UUID shoppingListId, UUID userId);

    List<SharedListEntity> findAllByUserId(UUID userId);

    List<SharedListEntity> findAllByShoppingListId(UUID shoppingListId);
}