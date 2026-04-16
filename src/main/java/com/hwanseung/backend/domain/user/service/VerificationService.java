package com.hwanseung.backend.domain.user.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {
    private final Map<String, String> verificationStorage = new ConcurrentHashMap<>();

    public String createCode(String key) {
        String code = String.valueOf((int)(Math.random() * 899999) + 100000);
        verificationStorage.put(key, code);
        return code;
    }

    public boolean verify(String key, String code) {
        String savedCode = verificationStorage.get(key);
        if (savedCode != null && savedCode.equals(code)) {
            verificationStorage.remove(key);
            return true;
        }
        return false;
    }
}