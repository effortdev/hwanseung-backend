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

    @Column(name = "userid", nullable = false, length = 50, unique = true)
    private String userid; // 로그인 아이디

    @Column(name = "username", nullable = false, length = 50)
    private String username; // 사용자 실명

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickname", nullable = false, length = 30, unique = true)
    private String nickname; // 별명

    @Column(name = "birthday", length = 10)
    private String birthday; // 생년월일

    @Column(name = "contact", length = 20)
    private String contact; // 연락처

    @Column(name = "email", length = 50, unique = true)
    private String email; // 이메일

    @Column(name = "gender", length = 10)
    private String gender; // 성별

    @Column(name = "zip_code", length = 10)
    private String zipCode; // 우편번호

    @Column(name = "address", length = 100)
    private String address; // 기본 주소

    @Column(name = "detail_address", length = 100)
    private String detailAddress; // 상세 주소

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Auth auth;

    // 빌더 패턴 유지 (생성자)
    @Builder(builderMethodName = "userBuilder") // 중복 빌더 방지
    public User(String email, String contact, String username, String password, Role role,
                String userid, String nickname, String birthday, String gender,
                String zipCode, String address, String detailAddress) {
        this.email = email;
        this.contact = contact;
        this.username = username;
        this.password = password;
        this.role = role;
        this.userid = userid;
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