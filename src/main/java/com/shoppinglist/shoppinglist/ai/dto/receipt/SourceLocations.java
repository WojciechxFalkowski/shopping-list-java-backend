package com.shoppinglist.shoppinglist.ai.dto.receipt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SourceLocations {
    // Zgodnie ze strukturą "source_locations" jest obiektem zawierającym mapy lokalizacji:
    // "date": [[ {y,x}, {y,x}, ... ]],
    // "receipt_no": [[ {y,x}, ... ]], "merchant_name", "merchant_address", "doc"
    // Każde pole to lista list punktów.

    @JsonProperty("date")
    private List<List<Point>> date;

    @JsonProperty("receipt_no")
    private List<List<Point>> receiptNo;

    @JsonProperty("merchant_name")
    private List<List<Point>> merchantName;

    @JsonProperty("merchant_address")
    private List<List<Point>> merchantAddress;

    @JsonProperty("doc")
    private List<List<Point>> doc;
}
