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

    // 알림을 받을 사람의 ID (예: 판매자 아이디)
    @Column(nullable = false)
    private String receiverId;

    // 알림 내용 (예: "es님이 [아이패드]를 찜했습니다!")
    @Column(nullable = false)
    private String content;

    // 알림 종류 (예: "FAVORITE" - 찜, "NOTICE" - 공지사항 등)
    @Column(nullable = false)
    private String type;

    // 관련된 데이터 번호 (클릭 시 해당 상품 페이지로 이동하기 위해 필요!)
    private Long relatedItemId;

    // 채팅방 번호(UUID)처럼 문자로 된 ID를 저장하기 위한 필드 추가!
    @Column(length = 100) // 넉넉하게
    private String relatedStringId;

    // 읽음 여부 (처음 알림이 생성되면 당연히 안 읽은 상태이므로 false)
    @Column(nullable = false)
    private boolean isRead = false;

    // 알림이 발생한 시간
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

    // 나중에 "알림 읽음 처리" API에서 사용할 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}