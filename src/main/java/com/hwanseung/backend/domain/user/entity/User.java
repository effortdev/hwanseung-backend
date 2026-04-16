package com.hwanseung.backend.domain.user.entity;

import com.hwanseung.backend.domain.admin.dto.Status;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.hwanseung.backend.domain.admin.dto.Status;
import com.hwanseung.backend.domain.user.entity.Role;

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
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String username;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String password;

    @Column(nullable = false, length = 30, unique = true)
    private String nickname;

    @Column(length = 10)
    private String birthday;

    @Column(length = 20)
    private String contact;

    @Column(length = 50, unique = true)
    private String email;

    @Column(length = 10)
    private String gender;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(length = 100)
    private String address;

    @Column(name = "detail_address", length = 100)
    private String detailAddress;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Auth auth;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('ACTIVE', 'SUSPENDED', 'SECESSION', 'PENDING') DEFAULT 'ACTIVE'")
    private Status status = Status.ACTIVE;

    @Column
    @Builder.Default
    private Integer trustScore = 0;

    @Column
    private Integer reportCount;

    @Column(name = "neighborhood", length = 50)
    private String neighborhood;

    @Builder.Default
    @Column(name = "is_neighborhood_authenticated", nullable = false, columnDefinition = "TINYINT(1) DEFAULT 0")
    private boolean isNeighborhoodAuthenticated = false;

    @Column(name = "profile_image_path")
    private String profileImagePath;

    @Column(name = "profile_original_name")
    private String profileOriginalName;

    @Column
    private LocalDateTime suspendedAt;

    @Column
    private LocalDateTime suspendUntil;

    @Builder.Default
    @Column(length = 20)
    private String provider = "LOCAL";

    @Column(name = "provider_id", unique = true)
    private String providerId;


    public void withdraw() { //회원탈퇴
        this.status = Status.SECESSION;
    }

}