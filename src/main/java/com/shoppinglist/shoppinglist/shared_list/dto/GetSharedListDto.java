package com.shoppinglist.shoppinglist.shared_list.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetSharedListDto {

    private UUID id;

    private UUID shoppingListId;

    private UUID userId;

    private LocalDateTime joinedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
