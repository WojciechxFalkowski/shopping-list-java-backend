package com.shoppinglist.shoppinglist.receipt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDto {
    private UUID id;
    private String storeName;
    private String storeStreet;
    private String storeBuildingNumber;
    private String storePostalCode;
    private String storeCity;
    private String storeCountry;
    private String nip;
    private String merchantCompanyRegNo;
    private String receiptNumber;
    private LocalDate date;
    private LocalTime time;
    private String currency;
    private Double total;
    private String notes;
    private List<ItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
