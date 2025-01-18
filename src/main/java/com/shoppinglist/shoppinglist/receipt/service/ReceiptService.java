package com.shoppinglist.shoppinglist.receipt.service;

import com.shoppinglist.shoppinglist.receipt.dto.ItemDto;
import com.shoppinglist.shoppinglist.receipt.dto.ReceiptDto;
import com.shoppinglist.shoppinglist.receipt.entity.ItemEntity;
import com.shoppinglist.shoppinglist.receipt.entity.ReceiptEntity;
import com.shoppinglist.shoppinglist.receipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReceiptService {

    @Autowired
    private ReceiptRepository receiptRepository;

    // Metoda do mapowania ReceiptEntity do ReceiptDto
    private ReceiptDto mapToDto(ReceiptEntity receipt) {
        List<ItemDto> itemDtos = receipt.getItems().stream()
                .map(this::mapItemToDto)
                .collect(Collectors.toList());

        return ReceiptDto.builder()
                .id(receipt.getId())
                .storeName(receipt.getStoreName())
                .storeStreet(receipt.getStoreStreet())
                .storeBuildingNumber(receipt.getStoreBuildingNumber())
                .storePostalCode(receipt.getStorePostalCode())
                .storeCity(receipt.getStoreCity())
                .storeCountry(receipt.getStoreCountry())
                .nip(receipt.getNip())
                .merchantCompanyRegNo(receipt.getMerchantCompanyRegNo())
                .receiptNumber(receipt.getReceiptNumber())
                .date(receipt.getDate())
                .time(receipt.getTime())
                .currency(receipt.getCurrency())
                .total(receipt.getTotal())
                .notes(receipt.getNotes())
                .items(itemDtos)
                .createdAt(receipt.getCreatedAt())
                .updatedAt(receipt.getUpdatedAt())
                .build();
    }

    // Metoda do mapowania ItemEntity do ItemDto
    private ItemDto mapItemToDto(ItemEntity item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .category(item.getCategory())
                .remarks(item.getRemarks())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    // Metoda do mapowania ReceiptDto do ReceiptEntity
    private ReceiptEntity mapToEntity(ReceiptDto dto) {
        ReceiptEntity receipt = ReceiptEntity.builder()
                .storeName(dto.getStoreName())
                .storeStreet(dto.getStoreStreet())
                .storeBuildingNumber(dto.getStoreBuildingNumber())
                .storePostalCode(dto.getStorePostalCode())
                .storeCity(dto.getStoreCity())
                .storeCountry(dto.getStoreCountry())
                .nip(dto.getNip())
                .merchantCompanyRegNo(dto.getMerchantCompanyRegNo())
                .receiptNumber(dto.getReceiptNumber())
                .date(dto.getDate())
                .time(dto.getTime())
                .currency(dto.getCurrency())
                .total(dto.getTotal())
                .notes(dto.getNotes())
                .build();

        List<ItemEntity> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (ItemDto itemDto : dto.getItems()) {
                ItemEntity item = ItemEntity.builder()
                        .name(itemDto.getName())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .category(itemDto.getCategory())
                        .remarks(itemDto.getRemarks())
                        .receipt(receipt)
                        .build();
                items.add(item);
            }
        }
        receipt.setItems(items);
        return receipt;
    }

    // Tworzenie pojedynczego paragonu
    @Transactional
    public ReceiptDto createReceipt(ReceiptDto receiptDto) {
        if (isReceiptDuplicate(receiptDto.getDate(), receiptDto.getTime(), receiptDto.getReceiptNumber())) {
            throw new IllegalArgumentException("Receipt with the same date and time already exists");
        }

        ReceiptEntity receiptEntity = mapToEntity(receiptDto);
        ReceiptEntity savedReceipt = receiptRepository.save(receiptEntity);
        return mapToDto(savedReceipt);
    }

    // Tworzenie wielu paragonów
    @Transactional
    public List<ReceiptDto> createReceipts(List<ReceiptDto> receiptDtos) {
        for (ReceiptDto dto : receiptDtos) {
            if (isReceiptDuplicate(dto.getDate(), dto.getTime(), dto.getReceiptNumber())) {
                throw new IllegalArgumentException(
                        String.format("Receipt with the same date and time already exists: %s %s", dto.getDate(), dto.getTime())
                );
            }
        }

        List<ReceiptEntity> receiptEntities = receiptDtos.stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
        List<ReceiptEntity> savedReceipts = receiptRepository.saveAll(receiptEntities);
        return savedReceipts.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // Metoda sprawdzająca, czy paragon o określonej dacie i godzinie już istnieje
    private boolean isReceiptDuplicate(LocalDate date, LocalTime time, String receiptNumber) {
        return receiptRepository.existsByDateAndTime(date, time)
                || receiptRepository.existsByReceiptNumber(receiptNumber);
    }

    // Pobieranie paragonu po ID
    public ReceiptDto getReceiptById(UUID id) {
        ReceiptEntity receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found with id: " + id));
        return mapToDto(receipt);
    }

    // Pobieranie wszystkich paragonów
    public List<ReceiptDto> getAllReceipts() {
        List<ReceiptEntity> receipts = receiptRepository.findAll();
        return receipts.stream()
                .map(this::mapToDto)
                .sorted(Comparator.comparing(ReceiptDto::getDate, Comparator.reverseOrder())
                        .thenComparing(ReceiptDto::getTime, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    // Aktualizacja paragonu
    @Transactional
    public ReceiptDto updateReceipt(UUID id, ReceiptDto receiptDto) {
        if (isReceiptDuplicate(receiptDto.getDate(), receiptDto.getTime(), receiptDto.getReceiptNumber())) {
            throw new IllegalArgumentException("Receipt with the same date and time already exists");
        }

        ReceiptEntity existingReceipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found with id: " + id));

        // Aktualizacja pól
        existingReceipt.setStoreName(receiptDto.getStoreName());
        existingReceipt.setStoreStreet(receiptDto.getStoreStreet());
        existingReceipt.setStoreBuildingNumber(receiptDto.getStoreBuildingNumber());
        existingReceipt.setStorePostalCode(receiptDto.getStorePostalCode());
        existingReceipt.setStoreCity(receiptDto.getStoreCity());
        existingReceipt.setStoreCountry(receiptDto.getStoreCountry());
        existingReceipt.setNip(receiptDto.getNip());
        existingReceipt.setMerchantCompanyRegNo(receiptDto.getMerchantCompanyRegNo());
        existingReceipt.setReceiptNumber(receiptDto.getReceiptNumber());
        existingReceipt.setDate(receiptDto.getDate());
        existingReceipt.setTime(receiptDto.getTime());
        existingReceipt.setCurrency(receiptDto.getCurrency());
        existingReceipt.setTotal(receiptDto.getTotal());
        existingReceipt.setNotes(receiptDto.getNotes());

        // Aktualizacja pozycji
        existingReceipt.getItems().clear();
        if (receiptDto.getItems() != null) {
            for (ItemDto itemDto : receiptDto.getItems()) {
                ItemEntity item = ItemEntity.builder()
                        .name(itemDto.getName())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .category(itemDto.getCategory())
                        .remarks(itemDto.getRemarks())
                        .receipt(existingReceipt)
                        .build();
                existingReceipt.getItems().add(item);
            }
        }

        ReceiptEntity updatedReceipt = receiptRepository.save(existingReceipt);
        return mapToDto(updatedReceipt);
    }

    // Usuwanie paragonu
    @Transactional
    public void deleteReceipt(UUID id) {
        ReceiptEntity receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Receipt not found with id: " + id));
        receiptRepository.delete(receipt);
    }
}
