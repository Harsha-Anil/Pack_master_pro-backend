package com.packagemaster.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "quote_requests", indexes = {
    @Index(name = "ix_quote_requests_email", columnList = "email"),
    @Index(name = "ix_quote_requests_status", columnList = "status"),
    @Index(name = "ix_quote_requests_product_type", columnList = "product_type"),
    @Index(name = "ix_quote_requests_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 200)
    private String email;

    @Column(name = "product_type", nullable = false, length = 100)
    private String productType;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal length;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal width;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal height;

    @Column(nullable = false)
    private Integer quantity;

    @Column(length = 100)
    private String color;

    @Column(name = "printing_required", nullable = false)
    private boolean printingRequired;

    @Column(name = "delivery_location", nullable = false, length = 300)
    private String deliveryLocation;

    @Column(name = "expected_timeline", nullable = false, length = 200)
    private String expectedTimeline;

    @Column(name = "additional_notes", columnDefinition = "text")
    private String additionalNotes;

    @Column(name = "reference_image_path", length = 500)
    private String referenceImagePath;

    @Column(name = "artwork_path", length = 500)
    private String artworkPath;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String status = "NEW";

    @Column(columnDefinition = "text")
    private String notes;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
