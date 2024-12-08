package com.shoppinglist.shoppinglist.ai;

import com.shoppinglist.shoppinglist.ai.dto.GenerateShoppingListByUrlDto;
import com.shoppinglist.shoppinglist.ai.dto.GenerateShoppingListFromTextDTO;
import com.shoppinglist.shoppinglist.ai.dto.receipt.OcrResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    @Autowired
    private AIService aiService;

    @PutMapping("/generate-from-url")
    public ResponseEntity<?> generateShoppingList(@RequestBody GenerateShoppingListByUrlDto dto) {
        return ResponseEntity.ok(aiService.generateShoppingListFromUrl(dto.getUrl()));
    }

    @PutMapping("/generate-shopping-list-from-text")
    public ResponseEntity<?> generateShoppingListFromText(@RequestBody GenerateShoppingListFromTextDTO generateShoppingListFromTextDTO) {
        return ResponseEntity.ok(aiService.generateShoppingListFromText(generateShoppingListFromTextDTO.getText()));
    }

    //https://asprise.com/receipt-ocr-data-capture-api/extract-text-reader-scanner-index.html
    // Nowy endpoint do analizy obrazu z paragonem
    @PostMapping("/analyze-receipt")
    public ResponseEntity<OcrResult> analyzeReceipt(@RequestParam("file") MultipartFile file) {
        // Najpierw musimy zapisać otrzymany plik do tymczasowej lokalizacji,
        // ponieważ Asprise używa File, a nie InputStream.
        File tempFile;
        try {
            tempFile = File.createTempFile("receipt-", ".jpg");
            file.transferTo(tempFile);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Nie udało się zapisać pliku tymczasowego: " + e.getMessage());
        }

        // Wywołanie serwisu z podaniem ścieżki do pliku tymczasowego
//        String jsonResult = aiService.analyzeReceiptImage(tempFile.getAbsolutePath());
        OcrResult jsonResult = aiService.analyzeReceiptImage(tempFile.getAbsolutePath());
        jsonResult.getReceipts().stream().forEach(abc -> {
            abc.getItems().stream().forEach(item -> {
                System.out.println(item.getDescription() + " " + item.getQty());
            });
        });
        // Opcjonalnie możesz usunąć plik tymczasowy po przetworzeniu
        tempFile.delete();

        return ResponseEntity.ok(jsonResult);
    }

}

