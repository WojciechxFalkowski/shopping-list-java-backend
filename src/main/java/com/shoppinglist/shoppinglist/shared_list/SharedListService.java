package com.shoppinglist.shoppinglist.shared_list;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SharedListService {

    @Autowired
    private SharedListRepository sharedListRepository;

    public SharedListEntity findSharedListById(UUID id) {
        return sharedListRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Item not found"));
    }

    public List<SharedListEntity> getSharedListByShoppingListId(UUID shoppingListId) {
        return sharedListRepository.findAllByShoppingListId(shoppingListId);
    }

    public void deleteSharedListById(UUID id) {
        sharedListRepository.deleteById(id);
    }

    public void deleteSharedList(UUID shoppingListId, UUID userId) {
        List<SharedListEntity> sharedListEntities = getSharedListByShoppingListId(shoppingListId);
        SharedListEntity sharedListEntity = sharedListEntities.stream().filter(sharedListEntityElement -> sharedListEntityElement.getUserId().equals(userId)).findFirst().orElse(null);
        sharedListRepository.deleteById(sharedListEntity.getId());
    }

    public boolean existsByShoppingListIdAndUserId(UUID shoppingListId, UUID userId) {
        return sharedListRepository.existsByShoppingListIdAndUserId(shoppingListId, userId);
    }
}
