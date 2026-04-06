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

// import com.hwanseung.backend.domain.product.service.ProductService;
// import com.hwanseung.backend.domain.product.dto.ProductResponseDTO;
// import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductRestController {

    // 🌟 핵심: 창고에서 물건을 찾아줄 관리자(Service)를 나중에 여기에 연결합니다.
     private final ProductService productService;

    /**
     * 내 근처 매물 조회 API
     * 프론트엔드에서 넘어온 위도, 경도 좌표를 받아 주변 상품을 검색합니다.
     * * @param lat 사용자의 현재 위도 (예: 37.1234)
     * @param lng 사용자의 현재 경도 (예: 127.1234)
     * @param radius 검색 반경 (기본값: 5.0km)
     */
    @GetMapping("/api/products/nearby")
    public ResponseEntity<?> getNearbyProducts(
            @RequestParam(value = "lat") double lat,
            @RequestParam(value = "lng") double lng,
            @RequestParam(value = "radius", defaultValue = "5.0") double radius) {

        // 서버 콘솔에 프론트엔드에서 보낸 좌표가 잘 도착했는지 찍어봅니다.
        System.out.println("📍 [위치 수신] 위도: " + lat + ", 경도: " + lng + ", 반경: " + radius + "km");

        /*
        🌟 실제 개발 로직의 흐름 (주석을 참고하세요)
        1. productService.findNearby(lat, lng, radius)를 호출하여 DB에서 가까운 상품을 찾습니다.
        2. 찾은 목록(List<ProductResponseDTO>)을 ResponseEntity.ok()에 담아 프론트엔드로 보냅니다.
        예시 코드: */

        List<ProductListResponseDTO> nearbyProducts = productService.getNearbyProducts(lat, lng, radius);
        return ResponseEntity.status(HttpStatus.OK).body(nearbyProducts);
    }
}