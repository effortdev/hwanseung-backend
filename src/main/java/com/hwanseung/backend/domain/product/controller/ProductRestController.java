package com.hwanseung.backend.domain.product.controller;

import com.hwanseung.backend.domain.product.dto.ProductListResponseDTO;
import com.hwanseung.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class ProductRestController {

     private final ProductService productService;


    @GetMapping("/api/products/nearby")
    public ResponseEntity<?> getNearbyProducts(
            @RequestParam(value = "lat") double lat,
            @RequestParam(value = "lng") double lng,
            @RequestParam(value = "radius", defaultValue = "5.0") double radius) {


        List<ProductListResponseDTO> nearbyProducts = productService.getNearbyProducts(lat, lng, radius);
        return ResponseEntity.status(HttpStatus.OK).body(nearbyProducts);
    }
}