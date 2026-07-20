package com.packagemaster.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryResponseDto {
    private int id;
    private String name;
    private String companyName;
    private String phoneNumber;
    private String email;
    private String subject;
    private String message;
    private String status;
    private String notes;
    private Instant createdAt;
}
