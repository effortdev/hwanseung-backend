package com.hwanseung.backend.domain.chat.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 메시지 고유 번호 (자동 증가)

    // 💡 꿀팁: ChatRoom과 @ManyToOne으로 강하게 묶지 않고 String으로 느슨하게 연결합니다.
    // 나중에 메시지만 따로 NoSQL(MongoDB 등)로 분리하거나 Spring Batch로 밀어넣을 때 훨씬 유리합니다.
    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private String senderId; // 보낸 사람 ID

    @Column(nullable = false, length = 1000)
    private String content; // 메시지 내용

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt; // 보낸 시간

    @Column(nullable = false)
    private boolean isRead = false;
}