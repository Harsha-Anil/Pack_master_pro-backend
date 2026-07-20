package com.packagemaster.service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateQuoteRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Size(max = 200, message = "Company name must not exceed 200 characters")
    private String companyName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Size(max = 200, message = "Email must not exceed 200 characters")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Product type is required")
    @Size(max = 100, message = "Product type must not exceed 100 characters")
    private String productType;

    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.1", message = "Length must be greater than 0")
    private BigDecimal length;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.1", message = "Width must be greater than 0")
    private BigDecimal width;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.1", message = "Height must be greater than 0")
    private BigDecimal height;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Size(max = 100, message = "Color must not exceed 100 characters")
    private String color;

    private boolean printingRequired;

    @NotBlank(message = "Delivery location is required")
    @Size(max = 300, message = "Delivery location must not exceed 300 characters")
    private String deliveryLocation;

    @NotBlank(message = "Expected timeline is required")
    @Size(max = 200, message = "Expected timeline must not exceed 200 characters")
    private String expectedTimeline;

    private String additionalNotes;
}
