package com.hwanseung.backend.domain.user.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {
    // <연락처(이메일 혹은 전화번호), 인증번호> 저장소
    private final Map<String, String> verificationStorage = new ConcurrentHashMap<>();

    /** 6자리 랜덤 인증번호 생성 및 저장 */
    public String createCode(String key) {
        String code = String.valueOf((int)(Math.random() * 899999) + 100000);
        verificationStorage.put(key, code);
        // 실제 서비스에서는 여기에 유효시간(예: 3분) 타이머 로직을 추가하거나 Redis를 사용합니다.
        return code;
    }

    /** 인증번호 일치 여부 확인 */
    public boolean verify(String key, String code) {
        String savedCode = verificationStorage.get(key);
        if (savedCode != null && savedCode.equals(code)) {
            verificationStorage.remove(key); // 인증 성공 시 1회성으로 삭제
            return true;
        }
        return false;
    }
}