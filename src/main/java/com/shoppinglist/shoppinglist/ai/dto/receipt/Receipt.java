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
public class Receipt {
    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("merchant_address")
    private String merchantAddress;
    @JsonProperty("merchant_phone")
    private String merchantPhone;
    @JsonProperty("merchant_website")
    private String merchantWebsite;
    @JsonProperty("merchant_tax_reg_no")
    private String merchantTaxRegNo;
    @JsonProperty("merchant_company_reg_no")
    private String merchantCompanyRegNo;
    @JsonProperty("region")
    private String region;
    @JsonProperty("mall")
    private String mall;
    @JsonProperty("country")
    private String country;
    @JsonProperty("receipt_no")
    private String receiptNo;
    @JsonProperty("date")
    private String date; // Można zamienić na LocalDate, jeśli potrzebne
    @JsonProperty("time")
    private String time; // Można zamienić na LocalTime, jeśli potrzebne
    @JsonProperty("items")
    private List<Item> items;
    @JsonProperty("currency")
    private String currency;
    @JsonProperty("total")
    private Double total;
    @JsonProperty("subtotal")
    private Double subtotal;
    @JsonProperty("tax")
    private Double tax;
    @JsonProperty("service_charge")
    private Double serviceCharge;
    @JsonProperty("tip")
    private Double tip;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("payment_details")
    private String paymentDetails;
    @JsonProperty("credit_card_type")
    private String creditCardType;
    @JsonProperty("credit_card_number")
    private String creditCardNumber;
    @JsonProperty("ocr_text")
    private String ocrText;
    @JsonProperty("ocr_confidence")
    private Double ocrConfidence;
    @JsonProperty("width")
    private Integer width;
    @JsonProperty("height")
    private Integer height;
    @JsonProperty("avg_char_width")
    private Double avgCharWidth;
    @JsonProperty("avg_line_height")
    private Double avgLineHeight;
    @JsonProperty("conf_amount")
    private Integer confAmount;
    @JsonProperty("source_locations")
    private SourceLocations sourceLocations;
}