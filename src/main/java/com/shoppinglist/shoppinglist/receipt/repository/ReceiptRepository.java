package com.shoppinglist.shoppinglist.receipt.repository;

import com.shoppinglist.shoppinglist.receipt.entity.ReceiptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReceiptRepository extends JpaRepository<ReceiptEntity, UUID> {

    // Sprawdza, czy istnieje paragon z danym receiptNumber
    boolean existsByReceiptNumber(String receiptNumber);

    // Sprawdza, czy istnieje paragon z danym date i time
    boolean existsByDateAndTime(LocalDate date, LocalTime time);

    // Znajduje paragony z receiptNumber w podanej liście
    List<ReceiptEntity> findByReceiptNumberIn(List<String> receiptNumbers);
}
