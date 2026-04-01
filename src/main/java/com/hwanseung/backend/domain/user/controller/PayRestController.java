package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.dto.PayChargeVO;
import com.hwanseung.backend.domain.user.dto.PayHistory;
import com.hwanseung.backend.domain.user.svc.PayService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    // 1. 포트원 토큰 가져오기
    public String getToken() {
        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("imp_key", impKey);
            requestBody.put("imp_secret", impSecretKey);
            ObjectMapper mapper = new ObjectMapper();
            String jsonBody = mapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/users/getToken"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            Map<String, Object> responseData = (Map<String, Object>) resMap.get("response");

            return (String) responseData.get("access_token");
        } catch (Exception e) {
            return null;
        }
    }

    // 2. 결제 위변조 검증 API
    @PostMapping("/verify")
    public ResponseEntity<String> verifyCharge(@RequestHeader("Authorization") String accessToken,
                                               @RequestBody PayChargeVO chargeVO) {

        // 🌟 수정 1: 토큰에서 고유번호를 확실하게 숫자(Long)로 받습니다!
        Long userId = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/payments/" + chargeVO.getImp_uid()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getToken())
                    .method("GET", HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            Map<String, Object> iamportData = (Map<String, Object>) resMap.get("response");

            int actualPaidAmount = (Integer) iamportData.get("amount");
            String status = (String) iamportData.get("status");

            if ("paid".equals(status) && chargeVO.getAmount() == actualPaidAmount) {
                PayHistory history = new PayHistory();

                // 🌟 수정 2: 장부(PayHistory)에 적을 때는 String.valueOf()를 써서 글자로 변환해 줍니다!
                history.setUserId(String.valueOf(userId));

                history.setImpUid(chargeVO.getImp_uid());
                history.setMerchantUid(chargeVO.getMerchant_uid());
                history.setType("CHARGE");
                history.setAmount(actualPaidAmount);

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

        // 🌟 수정 3: 토큰에서 고유번호를 숫자(Long)로 받습니다!
        Long userId = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));

        // 🌟 수정 4: 서비스에 넘겨줄 때도 String.valueOf()로 글자 변환해서 전달합니다!
        int myBalance = payService.getBalance(String.valueOf(userId));

        return ResponseEntity.status(HttpStatus.OK).body(myBalance);
    }
}