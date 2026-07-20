package com.packagemaster.service.controller;

import com.packagemaster.service.dto.CreateQuoteRequestDto;
import com.packagemaster.service.dto.QuoteRequestResponseDto;
import com.packagemaster.service.dto.UpdateNotesDto;
import com.packagemaster.service.dto.UpdateStatusDto;
import com.packagemaster.service.model.QuoteRequest;
import com.packagemaster.service.repository.QuoteRequestRepository;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/quotes")
public class QuotesController {

    private final QuoteRequestRepository quoteRequestRepository;

    public QuotesController(QuoteRequestRepository quoteRequestRepository) {
        this.quoteRequestRepository = quoteRequestRepository;
    }

    // POST /api/quotes
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @ModelAttribute CreateQuoteRequestDto dto,
            @RequestParam(required = false) MultipartFile referenceImage,
            @RequestParam(required = false) MultipartFile artwork) {

        try {
            QuoteRequest quote = QuoteRequest.builder()
                    .name(dto.getName())
                    .companyName(dto.getCompanyName())
                    .phoneNumber(dto.getPhoneNumber())
                    .email(dto.getEmail())
                    .productType(dto.getProductType())
                    .length(dto.getLength())
                    .width(dto.getWidth())
                    .height(dto.getHeight())
                    .quantity(dto.getQuantity())
                    .color(dto.getColor())
                    .printingRequired(dto.isPrintingRequired())
                    .deliveryLocation(dto.getDeliveryLocation())
                    .expectedTimeline(dto.getExpectedTimeline())
                    .additionalNotes(dto.getAdditionalNotes())
                    .status("NEW")
                    .createdAt(Instant.now())
                    .build();

            // Handle file uploads
            if (referenceImage != null && !referenceImage.isEmpty()) {
                String refPath = saveFile(referenceImage, "reference-images");
                quote.setReferenceImagePath(refPath);
            }

            if (artwork != null && !artwork.isEmpty()) {
                String artPath = saveFile(artwork, "artworks");
                quote.setArtworkPath(artPath);
            }

            QuoteRequest saved = quoteRequestRepository.save(quote);
            log.info("New quote request #{} from {} ({}) for {}", saved.getId(), saved.getName(), saved.getEmail(), saved.getProductType());

            QuoteRequestResponseDto response = mapToResponse(saved);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();

            return ResponseEntity.created(location).body(response);

        } catch (IOException e) {
            log.error("Failed to save uploaded file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process file uploads");
        }
    }

    // GET /api/quotes
    @GetMapping
    public ResponseEntity<List<QuoteRequestResponseDto>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize,
            HttpServletResponse response) {

        Specification<QuoteRequest> spec = (root, query, cb) -> cb.conjunction();

        if (status != null && !status.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (productType != null && !productType.trim().isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("productType"), productType));
        }

        if (search != null && !search.trim().isEmpty()) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), searchPattern),
                    cb.like(cb.lower(root.get("companyName")), searchPattern),
                    cb.like(cb.lower(root.get("email")), searchPattern)
            ));
        }

        int pageIndex = Math.max(0, page - 1);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<QuoteRequest> quotePage = quoteRequestRepository.findAll(spec, pageable);

        response.setHeader("X-Total-Count", String.valueOf(quotePage.getTotalElements()));

        List<QuoteRequestResponseDto> dtos = quotePage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // GET /api/quotes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<QuoteRequestResponseDto> getById(@PathVariable int id) {
        return quoteRequestRepository.findById(id)
                .map(this::mapToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/quotes/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<QuoteRequestResponseDto> updateStatus(
            @PathVariable int id,
            @Valid @RequestBody UpdateStatusDto dto) {

        return quoteRequestRepository.findById(id)
                .map(quote -> {
                    quote.setStatus(dto.getStatus());
                    QuoteRequest saved = quoteRequestRepository.save(quote);
                    return ResponseEntity.ok(mapToResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/quotes/{id}/notes
    @PutMapping("/{id}/notes")
    public ResponseEntity<QuoteRequestResponseDto> updateNotes(
            @PathVariable int id,
            @RequestBody UpdateNotesDto dto) {

        return quoteRequestRepository.findById(id)
                .map(quote -> {
                    quote.setNotes(dto.getNotes());
                    QuoteRequest saved = quoteRequestRepository.save(quote);
                    return ResponseEntity.ok(mapToResponse(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String saveFile(MultipartFile file, String folder) throws IOException {
        File uploadsDir = new File("Uploads/" + folder);
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String cleanFilename = originalFilename != null ? new File(originalFilename).getName() : "file";
        String uniqueName = UUID.randomUUID() + "_" + cleanFilename;
        File destFile = new File(uploadsDir, uniqueName);

        file.transferTo(destFile.getAbsoluteFile());

        return "/uploads/" + folder + "/" + uniqueName;
    }

    private QuoteRequestResponseDto mapToResponse(QuoteRequest q) {
        return QuoteRequestResponseDto.builder()
                .id(q.getId())
                .name(q.getName())
                .companyName(q.getCompanyName())
                .phoneNumber(q.getPhoneNumber())
                .email(q.getEmail())
                .productType(q.getProductType())
                .length(q.getLength())
                .width(q.getWidth())
                .height(q.getHeight())
                .quantity(q.getQuantity())
                .color(q.getColor())
                .printingRequired(q.isPrintingRequired())
                .deliveryLocation(q.getDeliveryLocation())
                .expectedTimeline(q.getExpectedTimeline())
                .additionalNotes(q.getAdditionalNotes())
                .referenceImagePath(q.getReferenceImagePath())
                .artworkPath(q.getArtworkPath())
                .status(q.getStatus())
                .notes(q.getNotes())
                .createdAt(q.getCreatedAt())
                .build();
    }
}
