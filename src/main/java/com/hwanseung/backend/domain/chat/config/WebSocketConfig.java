package com.hwanseung.backend.domain.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 구독 요청 prefix (받는 쪽)
        config.enableSimpleBroker("/sub");
        // 메시지 발행 요청 prefix (보내는 쪽)
        config.setApplicationDestinationPrefixes("/pub");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 웹소켓에 접속할 엔드포인트: ws://localhost/ws-chat
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // 테스트를 위해 모든 도메인 허용
                .withSockJS(); // 낮은 버전 브라우저 호환성 지원
    }
}
