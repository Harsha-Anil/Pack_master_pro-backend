package com.packagemaster.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequestResponseDto {
    private int id;
    private String name;
    private String companyName;
    private String phoneNumber;
    private String email;
    private String productType;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private int quantity;
    private String color;
    private boolean printingRequired;
    private String deliveryLocation;
    private String expectedTimeline;
    private String additionalNotes;
    private String referenceImagePath;
    private String artworkPath;
    private String status;
    private String notes;
    private Instant createdAt;
}
