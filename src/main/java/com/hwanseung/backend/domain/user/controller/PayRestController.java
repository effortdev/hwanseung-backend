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
    // 1. 포트원 토큰 가져오기 (디버깅 강화 버전)
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
            System.out.println("🚨 [1단계] 토큰 발급 응답: " + response.body()); // 🌟 이유를 눈으로 직접 확인!

            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            Map<String, Object> responseData = (Map<String, Object>) resMap.get("response");

            if (responseData == null) {
                System.out.println("❌ 토큰 발급 실패! API Key와 Secret을 확인하세요.");
                return null;
            }

            return (String) responseData.get("access_token");
        } catch (Exception e) {
            System.out.println("❌ 토큰 발급 중 통신 에러: " + e.getMessage());
            return null;
        }
    }

    // 2. 결제 위변조 검증 API (초정밀 디버깅 버전)
    @PostMapping("/verify")
    public ResponseEntity<String> verifyCharge(@RequestHeader("Authorization") String accessToken,
                                               @RequestBody PayChargeVO chargeVO) {

        System.out.println("=========================================");
        System.out.println("🚨 [1단계] 프론트에서 데이터 잘 도착했나?");
        System.out.println("전달받은 impUid: " + chargeVO.getImpUid());
        System.out.println("전달받은 금액: " + chargeVO.getAmount());

        if (chargeVO.getImpUid() == null) {
            System.out.println("❌ 실패원인: impUid가 null입니다. (데이터 배달 사고)");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }

        Long userId = this.jwtTokenProvider.getUserIdFromToken(accessToken.substring(7));
        String iamportToken = getToken();

        if (iamportToken == null) {
            System.out.println("❌ 실패원인: 포트원 토큰 발급 실패");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
        }

        try {
            System.out.println("🚨 [2단계] 포트원에 영수증 검증 요청 시작");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.iamport.kr/payments/" + chargeVO.getImpUid()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + iamportToken)
                    .method("GET", HttpRequest.BodyPublishers.ofString("{}"))
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("포트원 응답 결과: " + response.body());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> resMap = mapper.readValue(response.body(), Map.class);
            Map<String, Object> iamportData = (Map<String, Object>) resMap.get("response");

            if (iamportData == null) {
                System.out.println("❌ 실패원인: 포트원이 결제 내역을 안 줌 (이유: " + resMap.get("message") + ")");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("fail");
            }

            // 금액 검증 로직 안전하게 수정
            int actualPaidAmount = Integer.parseInt(String.valueOf(iamportData.get("amount")));
            String status = (String) iamportData.get("status");

            System.out.println("🚨 [3단계] 해킹(금액 조작) 검증");
            System.out.println("프론트가 보낸 금액: " + chargeVO.getAmount() + " vs 실제 결제된 금액: " + actualPaidAmount);
            System.out.println("결제 상태: " + status);

            if ("paid".equals(status) && chargeVO.getAmount() == actualPaidAmount) {
                System.out.println("🚨 [4단계] 금액 검증 통과! DB 저장 시작");
                PayHistory history = new PayHistory();
                history.setUserId(String.valueOf(userId));
                history.setImpUid(chargeVO.getImpUid());
                history.setMerchantUid(chargeVO.getMerchantUid());
                history.setType("CHARGE");
                history.setAmount(actualPaidAmount);

                boolean isSuccess = payService.chargeHwanseungPay(history);

                if(isSuccess) {
                    System.out.println("✅ [최종 성공] DB 저장까지 완벽하게 완료되었습니다!");
                    return ResponseEntity.status(HttpStatus.OK).body("success");
                } else {
                    System.out.println("❌ 실패원인: DB 저장 로직(payService)에서 false를 반환함");
                }
            } else {
                System.out.println("❌ 실패원인: 결제 상태가 paid가 아니거나, 금액이 불일치합니다!");
            }
        } catch (Exception e) {
            System.out.println("❌ 실패원인: 백엔드 내부 에러 발생 (아래 로그 확인)");
            e.printStackTrace();
        }

        System.out.println("=========================================");
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