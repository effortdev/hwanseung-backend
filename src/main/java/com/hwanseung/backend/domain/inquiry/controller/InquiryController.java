package com.hwanseung.backend.domain.inquiry.controller;

import com.hwanseung.backend.domain.inquiry.entity.Inquiry;
import com.hwanseung.backend.domain.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryService inquiryService;


    @GetMapping("/all")
    public ResponseEntity<List<Inquiry>> getInquiriesAll(
            @RequestParam(defaultValue = "all") String category) {
        System.out.println("all category: %s".formatted(category));
        return ResponseEntity.ok(inquiryService.getInquiriesAll(category));
    }

    @GetMapping
    public ResponseEntity<Page<Inquiry>> getInquiries(
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "0") int page) {
        System.out.println("category: %s, page: %d".formatted(category, page));
        return ResponseEntity.ok(inquiryService.getInquiries(category, page));
    }

    @PostMapping
    public ResponseEntity<Inquiry> create(@RequestBody Inquiry inquiry) {
        return ResponseEntity.ok(inquiryService.save(inquiry));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Inquiry> update(@PathVariable Long id, @RequestBody Inquiry inquiry) {
        return ResponseEntity.ok(inquiryService.update(id, inquiry));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        inquiryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}