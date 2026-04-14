package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.svc.PayService;

// 🌟 (주의) 아래 VO 클래스 경로는 회원님 프로젝트의 실제 경로에 맞게 꼭 수정해주세요!
import com.hwanseung.backend.domain.user.vo.PayChargeVO;

import com.fasterxml.jackson.databind.ObjectMapper; // 🌟 바로 이 녀석이 JSON 통역사입니다!
import com.hwanseung.backend.domain.user.vo.PayHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pay")
public class PayRestController {

    private final PayService payService;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${iamport.api.key}")
    private String impKey;

    @Value("${iamport.api.secret}")
    private String impSecretKey;

    // 1. 포트원 토큰 발급 메서드
    public String getToken() {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("imp_key", impKey);
            requestBody.put("imp_secret", impSecretKey);

            // JSON 통역사 생성! (DB랑 무관함)
            ObjectMapper mapper = new ObjectMapper();

            // 자바 Map -> JSON 글자로 번역해서 포트원으로 전송
            String jsonBody = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/users/getToken"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // 포트원이 보낸 JSON 글자 -> 자바 Map으로 번역해서 꺼내기
            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            Map<String, Object> responseData = (Map<String, Object>) resMap.get("response");

            return (String) responseData.get("access_token");

        } catch (Exception e) {
            System.out.println("포트원 토큰 발급 실패: " + e.getMessage());
            return null;
        }
    }

    // 2. 결제 위변조 검증 API
    @PostMapping("/verify")
    public ResponseEntity<String> verifyCharge(@RequestHeader("Authorization") String accessToken,
                                               @RequestBody PayChargeVO chargeVO) {

        Long userId = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/payments/" + chargeVO.getImp_uid()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getToken())
                    .method("GET", HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // 여기서도 통역사 활약!
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            Map<String, Object> iamportData = (Map<String, Object>) resMap.get("response");

            int actualPaidAmount = (Integer) iamportData.get("amount");
            String status = (String) iamportData.get("status");

            if ("paid".equals(status) && chargeVO.getAmount() == actualPaidAmount) {
                PayHistory history = new PayHistory();

                history.setUserId(String.valueOf(userId));
                history.setImpUid(chargeVO.getImp_uid());
                history.setMerchantUid(chargeVO.getMerchant_uid());
                history.setAmount(actualPaidAmount);
                history.setType("CHARGE");

                boolean isSuccess = payService.chargeHwanseungPay(history);

                if(isSuccess) {
                    return ResponseEntity.status(HttpStatus.OK).body("success");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
    }

    // 3. 내 잔액 조회 API
    @GetMapping("/balance")
    public ResponseEntity<Integer> getMyBalance(@RequestHeader("Authorization") String accessToken) {
        Long userId = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        int myBalance = 50000;
        return ResponseEntity.status(HttpStatus.OK).body(myBalance);
    }
}