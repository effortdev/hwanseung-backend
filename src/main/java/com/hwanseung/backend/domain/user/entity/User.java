package com.hwanseung.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // 🌟 핵심 1: 이제 DB의 'username' 컬럼과 정직하게 연결합니다.
    @Column(name = "username", nullable = false, length = 50, unique = true)
    private String username; // 👈 이게 이제부터 진짜 로그인 아이디입니다!

    // 🌟 핵심 2: 실명은 DB의 'name' 컬럼과 연결합니다.
    @Column(name = "name", nullable = false, length = 50)
    private String name; // 👈 사용자 실명

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30, unique = true)
    private String nickname; // 별명 (인덱스 UK)

    @Column(length = 10)
    private String birthday; // 생년월일 (YYYY-MM-DD)

    @Column(length = 20)
    private String contact; // 연락처

    @Column(length = 50, unique = true)
    private String email; // 이메일 (인덱스 4)

    @Column(length = 10)
    private String gender; // 성별

    @Column(name = "zip_code", length = 10)
    private String zipCode; // 우편번호 (DB의 zip_code와 매핑)

    @Column(length = 100)
    private String address; // 기본 주소

    @Column(name = "detail_address", length = 100)
    private String detailAddress; // 상세 주소 (DB의 detail_address와 매핑)

    @Enumerated(EnumType.STRING)
    private Role role; // 권한 (ROLE_USER, ROLE_ADMIN)

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Auth auth;

    @Column(name = "neighborhood", length = 50)
    private String neighborhood;

    // MySQL의 TINYINT(1)은 Java의 boolean과 완벽하게 1:1로 매칭됩니다.
    // columnDefinition = "tinyint(1) default 0" 옵션을 주면 DB와 동기화하기 좋습니다.
    @Column(name = "is_neighborhood_authenticated", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isNeighborhoodAuthenticated = false;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Column(name = "profile_original_name")
    private String profileOriginalName;

    @Builder
    public User(String email, String contact, String username, String password, Role role,
                String name, String nickname, String birthday, String gender,
                String zipCode, String address, String detailAddress) {
        this.email = email;
        this.contact = contact;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.nickname = nickname;
        this.birthday = birthday;
        this.gender = gender;
        this.zipCode = zipCode;
        this.address = address;
        this.detailAddress = detailAddress;

    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

}