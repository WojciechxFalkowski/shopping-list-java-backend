package com.shoppinglist.shoppinglist.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private UUID id;
    private String name;
    private Double quantity;
    private Double unitPrice;
    private String category;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
