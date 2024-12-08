package com.shoppinglist.shoppinglist.ai.dto.receipt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcrResult {
    @JsonProperty("ocr_type")
    private String ocrType;
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("ref_no")
    private String refNo;
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty("request_received_on")
    private Long requestReceivedOn;
    @JsonProperty("success")
    private boolean success;
    @JsonProperty("image_width")
    private int imageWidth;
    @JsonProperty("image_height")
    private int imageHeight;
    @JsonProperty("image_rotation")
    private double imageRotation;
    @JsonProperty("recognition_completed_on")
    private Long recognitionCompletedOn;
    @JsonProperty("receipts")
    private List<Receipt> receipts;
}