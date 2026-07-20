package com.packagemaster.service.controller;

import com.packagemaster.service.dto.CreateEnquiryDto;
import com.packagemaster.service.dto.EnquiryResponseDto;
import com.packagemaster.service.dto.UpdateNotesDto;
import com.packagemaster.service.dto.UpdateStatusDto;
import com.packagemaster.service.model.Enquiry;
import com.packagemaster.service.repository.EnquiryRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/enquiries")
public class EnquiriesController {

    private final EnquiryRepository enquiryRepository;

    public EnquiriesController(EnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }

    // POST /api/enquiries
    @PostMapping
    public ResponseEntity<EnquiryResponseDto> create(@Valid @RequestBody CreateEnquiryDto dto) {
        Enquiry enquiry = Enquiry.builder()
                .name(dto.getName())
                .companyName(dto.getCompanyName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .status("NEW")
                .createdAt(Instant.now())
                .build();

        Enquiry saved = enquiryRepository.save(enquiry);
        log.info("New enquiry #{} from {} ({})", saved.getId(), saved.getName(), saved.getEmail());

        EnquiryResponseDto response = mapToResponse(saved);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // GET /api/enquiries
    @GetMapping
    public ResponseEntity<List<EnquiryResponseDto>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletResponse response) {

        Specification<Enquiry> spec = (root, query, cb) -> cb.conjunction();

        if (status != null && !status.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (search != null && !search.trim().isEmpty()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("companyName")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern)
            ));
        }

        // Map 1-based page to 0-based page for Spring Data
        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Enquiry> enquiryPage = enquiryRepository.findAll(spec, pageable);

        response.setHeader("X-Total-Count", String.valueOf(enquiryPage.getTotalElements()));

        List<EnquiryResponseDto> dtos = enquiryPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // GET /api/enquiries/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EnquiryResponseDto> getById(@PathVariable int id) {
        return enquiryRepository.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/enquiries/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<EnquiryResponseDto> updateStatus(
            @PathVariable int id,
            @Valid @RequestBody UpdateStatusDto dto) {

        return enquiryRepository.findById(id)
                .map(enquiry -> {
                    enquiry.setStatus(dto.getStatus());
                    Enquiry saved = enquiryRepository.save(enquiry);
                    return ResponseEntity.ok(mapToResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/enquiries/{id}/notes
    @PutMapping("/{id}/notes")
    public ResponseEntity<EnquiryResponseDto> updateNotes(
            @PathVariable int id,
            @RequestBody UpdateNotesDto dto) {

        return enquiryRepository.findById(id)
                .map(enquiry -> {
                    enquiry.setNotes(dto.getNotes());
                    Enquiry saved = enquiryRepository.save(enquiry);
                    return ResponseEntity.ok(mapToResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private EnquiryResponseDto mapToResponse(Enquiry e) {
        return EnquiryResponseDto.builder()
                .id(e.getId())
                .name(e.getName())
                .companyName(e.getCompanyName())
                .phoneNumber(e.getPhoneNumber())
                .email(e.getEmail())
                .subject(e.getSubject())
                .message(e.getMessage())
                .status(e.getStatus())
                .notes(e.getNotes())
                .createdAt(e.getCreatedAt())
                .build();
    }
}
