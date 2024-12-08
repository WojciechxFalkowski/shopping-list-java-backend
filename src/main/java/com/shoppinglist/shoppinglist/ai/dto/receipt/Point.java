package com.shoppinglist.shoppinglist.ai.dto.receipt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Point {
    @JsonProperty("y")
    private Integer y;
    @JsonProperty("x")
    private Integer x;
}