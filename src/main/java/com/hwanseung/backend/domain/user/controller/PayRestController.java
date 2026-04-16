package com.hwanseung.backend.domain.user.controller;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import com.hwanseung.backend.domain.user.dto.PayChargeVO;
import com.hwanseung.backend.domain.user.dto.PayHistory;
import com.hwanseung.backend.domain.user.dto.PayUseVO;
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

            if (responseData == null) {
                return null;
            }

            return (String) responseData.get("access_token");
        } catch (Exception e) {
            return null;
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCharge(@RequestHeader("Authorization") String accessToken,
                                               @RequestBody PayChargeVO chargeVO) {
        try {
            String impUid = chargeVO.getImpUid() == null ? null : chargeVO.getImpUid().trim();
            String merchantUid = chargeVO.getMerchantUid() == null ? null : chargeVO.getMerchantUid().trim();

            if (impUid == null || impUid.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }

            Long userId = jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
            String iamportToken = getToken();

            if (iamportToken == null || iamportToken.isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> paymentData = fetchPaymentByImpUid(iamportToken, impUid, mapper);

            if (paymentData == null && merchantUid != null && !merchantUid.isBlank()) {
                paymentData = fetchPaymentByMerchantUid(iamportToken, merchantUid, mapper);
            }

            if (paymentData == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }

            Number amountValue = (Number) paymentData.get("amount");
            int actualPaidAmount = amountValue.intValue();
            String status = String.valueOf(paymentData.get("status"));
            String paidImpUid = String.valueOf(paymentData.get("imp_uid"));
            String paidMerchantUid = String.valueOf(paymentData.get("merchant_uid"));

            if (!"paid".equals(status)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }

            if (chargeVO.getAmount() != actualPaidAmount) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }

            PayHistory history = new PayHistory();
            history.setUserId(String.valueOf(userId));
            history.setImpUid(paidImpUid);
            history.setMerchantUid(paidMerchantUid);
            history.setType("CHARGE");
            history.setAmount(actualPaidAmount);

            boolean isSuccess = payService.chargeHwanseungPay(history);

            if (!isSuccess) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }

            return ResponseEntity.ok("success");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
        }
    }

    private Map<String, Object> fetchPaymentByImpUid(String iamportToken, String impUid, ObjectMapper mapper) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/payments/" + impUid))
                    .header("Authorization", iamportToken)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            return (Map<String, Object>) resMap.get("response");
        } catch (Exception e) {
            return null;
        }
    }

    private Map<String, Object> fetchPaymentByMerchantUid(String iamportToken, String merchantUid, ObjectMapper mapper) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/payments/find/" + merchantUid + "/paid"))
                    .header("Authorization", iamportToken)
                    .GET()
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            return (Map<String, Object>) resMap.get("response");
        } catch (Exception e) {
            return null;
        }
    }
    @GetMapping("/balance")
    public ResponseEntity<Integer> getMyBalance(@RequestHeader("Authorization") String accessToken) {

        Long userId = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));

        int myBalance = payService.getBalance(String.valueOf(userId));

        return ResponseEntity.status(HttpStatus.OK).body(myBalance);
    }
    @PostMapping("/use")
    public ResponseEntity<?> usePoint(@RequestHeader("Authorization") String accessToken,
                                      @RequestBody PayUseVO useVO) {
        try {
            Long userId = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));

            boolean isSuccess = payService.useHwanseungPay(String.valueOf(userId), useVO);

            if (isSuccess) {
                return ResponseEntity.status(HttpStatus.OK).body("success");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러가 발생했습니다.");
        }
    }
}
