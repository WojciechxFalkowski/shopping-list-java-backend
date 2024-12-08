package com.shoppinglist.shoppinglist.receipt.controller;

import com.shoppinglist.shoppinglist.receipt.dto.ReceiptDto;
import com.shoppinglist.shoppinglist.receipt.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    // Tworzenie pojedynczego paragonu
    @PostMapping
    public ResponseEntity<ReceiptDto> createReceipt(@RequestBody ReceiptDto receiptDto) {
        try {
            ReceiptDto createdReceipt = receiptService.createReceipt(receiptDto);
            return new ResponseEntity<>(createdReceipt, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Tworzenie wielu paragonów
    @PostMapping("/batch")
    public ResponseEntity<List<ReceiptDto>> createReceipts(@RequestBody List<ReceiptDto> receiptDtos) {
        try {
            List<ReceiptDto> createdReceipts = receiptService.createReceipts(receiptDtos);
            return new ResponseEntity<>(createdReceipts, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Pobieranie paragonu po ID
    @GetMapping("/{id}")
    public ResponseEntity<ReceiptDto> getReceiptById(@PathVariable UUID id) {
        try {
            ReceiptDto receiptDto = receiptService.getReceiptById(id);
            return new ResponseEntity<>(receiptDto, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Pobieranie wszystkich paragonów
    @GetMapping
    public ResponseEntity<List<ReceiptDto>> getAllReceipts() {
        List<ReceiptDto> receipts = receiptService.getAllReceipts();
        return new ResponseEntity<>(receipts, HttpStatus.OK);
    }

    // Aktualizacja paragonu
    @PutMapping("/{id}")
    public ResponseEntity<ReceiptDto> updateReceipt(@PathVariable UUID id, @RequestBody ReceiptDto receiptDto) {
        try {
            ReceiptDto updatedReceipt = receiptService.updateReceipt(id, receiptDto);
            return new ResponseEntity<>(updatedReceipt, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // Usuwanie paragonu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable UUID id) {
        try {
            receiptService.deleteReceipt(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
