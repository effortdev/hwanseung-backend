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
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="userid", nullable = false, length = 50, unique = true)
    private String username; // 로그인 아이디 (인덱스 3)

    @Column(nullable = false, length = 50)
    private String name; // 사용자 이름 (실명 등)

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