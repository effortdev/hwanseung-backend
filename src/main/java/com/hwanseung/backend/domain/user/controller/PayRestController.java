package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.dto.PayChargeVO;
import com.hwanseung.backend.domain.user.dto.PayHistory;
import com.hwanseung.backend.domain.user.svc.PayService;


import com.fasterxml.jackson.databind.ObjectMapper; // 🌟 바로 이 녀석이 JSON 통역사입니다!
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

    //properties에 숨겨둔 키 가져오기
    @Value("${iamport.api.key}")
    private String impKey;

    @Value("${iamport.api.secret}")
    private String impSecretKey;

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

                // 1. userId는 String이므로 변환해서 삽입
                history.setUserId(String.valueOf(userId));

                // 2. 나머지 String 값들 삽입
                history.setImpUid(chargeVO.getImp_uid());
                history.setMerchantUid(chargeVO.getMerchant_uid());
                history.setType("CHARGE");

                // 3. amount는 int이므로 숫자로 삽입
                // (iamportData에서 가져온 값은 Integer이므로 바로 호환됩니다)
                history.setAmount(actualPaidAmount);

                // 🌟 historyNo와 createdAt은 DB가 알아서 해주니 코드로 넣지 마세요!

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