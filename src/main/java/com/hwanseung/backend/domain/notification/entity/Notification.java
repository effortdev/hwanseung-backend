package com.hwanseung.backend.domain.notification.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String receiverId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String type;

    private Long relatedItemId;

    @Column(length = 100) // 넉넉하게
    private String relatedStringId;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Notification(String receiverId, String content, String type, Long relatedItemId,String relatedStringId) {
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
        this.relatedItemId = relatedItemId;
        this.relatedStringId = relatedStringId;
        this.isRead = false;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}