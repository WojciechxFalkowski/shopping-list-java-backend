package com.shoppinglist.shoppinglist.ai.dto.receipt;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @JsonProperty("amount")
    private Double amount;
    @JsonProperty("category")
    private String category;
    @JsonProperty("description")
    private String description;
    @JsonProperty("flags")
    private String flags;
    @JsonProperty("qty")
    private Integer qty;
    @JsonProperty("remarks")
    private String remarks;
    @JsonProperty("tags")
    private String tags;
    @JsonProperty("unitPrice")
    private Double unitPrice;
}
