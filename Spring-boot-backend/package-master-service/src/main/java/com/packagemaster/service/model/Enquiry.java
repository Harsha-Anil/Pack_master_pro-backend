package com.packagemaster.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "enquiries", indexes = {
    @Index(name = "ix_enquiries_email", columnList = "email"),
    @Index(name = "ix_enquiries_status", columnList = "status"),
    @Index(name = "ix_enquiries_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Enquiry {

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

    @Column(nullable = false, length = 300)
    private String subject;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Builder.Default
    @Column(nullable = false, length = 50)
    private String status = "NEW";

    @Column(columnDefinition = "text")
    private String notes;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
