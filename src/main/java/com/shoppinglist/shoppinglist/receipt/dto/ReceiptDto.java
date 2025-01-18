package com.shoppinglist.shoppinglist.receipt.dto;

import jakarta.validation.constraints.*;
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

    @NotBlank(message = "Store name cannot be blank.")
    private String storeName;

    @NotBlank(message = "Store street cannot be blank.")
    private String storeStreet;

    @NotBlank(message = "Store building number cannot be blank.")
    private String storeBuildingNumber;

    @NotBlank(message = "Store postal code cannot be blank.")
    private String storePostalCode;

    @NotBlank(message = "Store city cannot be blank.")
    private String storeCity;

    @NotBlank(message = "Store country cannot be blank.")
    private String storeCountry;

    @NotBlank(message = "NIP cannot be blank.")
    private String nip;

    private String merchantCompanyRegNo;

    @NotBlank(message = "Receipt number cannot be blank.")
    private String receiptNumber;

    @NotNull(message = "Date is required.")
    private LocalDate date;

    @NotNull(message = "Time is required.")
    private LocalTime time;

    @NotBlank(message = "Currency is required.")
    private String currency;

    @NotNull(message = "Total is required.")
    @PositiveOrZero(message = "Total cannot be negative.")
    private Double total;

    private String notes;

    @NotEmpty(message = "There must be at least one item in the receipt.")
    private List<ItemDto> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
