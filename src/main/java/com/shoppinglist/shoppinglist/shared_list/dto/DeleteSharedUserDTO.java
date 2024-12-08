package com.shoppinglist.shoppinglist.shared_list.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeleteSharedUserDTO {
    UUID shoppingListId;
    UUID toBeDeletedUserId;
}
