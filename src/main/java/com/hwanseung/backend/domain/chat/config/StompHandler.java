package com.hwanseung.backend.domain.chat.config;

import com.hwanseung.backend.domain.user.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 1. 웹소켓 연결(CONNECT) 요청일 때만 토큰을 검사합니다.
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {

            // 2. STOMP 헤더에서 'Authorization' 값을 가져옵니다.
            String authorizationHeader = accessor.getFirstNativeHeader("Authorization");

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                String token = authorizationHeader.substring(7);

                // 3. JwtTokenProvider를 사용해 토큰 유효성 검증
                if (jwtTokenProvider.validateToken(token)) {
                    String username = jwtTokenProvider.getUsernameFromToken(token);
                    log.info("🟢 웹소켓 연결 성공! 접속한 유저: {}", username);
                } else {
                    log.warn("🟡 유효하지 않은 토큰으로 연결 시도 - 비인증 상태로 진행");
                    // 예외를 던지지 않고 그냥 둡니다.
                }
            } else {
                log.info("⚪ 토큰 없이 웹소켓 연결 시도 (비회원)");
                // 예외를 던지지 않고 pass 시킵니다.
            }
        }

        // CONNECT 요청이 아니거나, 검증을 무사히 통과했다면 메시지를 그대로 흘려보냅니다.
        return message;
    }
}