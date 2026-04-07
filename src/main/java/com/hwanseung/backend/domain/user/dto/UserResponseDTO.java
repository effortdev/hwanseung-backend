package com.hwanseung.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hwanseung.backend.domain.user.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String name;
    private String username;
    private String nickname;
    private String birthday;
    private String contact;
    private String email;
    private String gender;
    private String zipCode;
    private String address;
    private String detailAddress;
    private String role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String profileImagePath;
    private String profileOriginalName;

    public UserResponseDTO(User entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.username = entity.getUsername();
        this.nickname = entity.getNickname();
        this.birthday = entity.getBirthday();
        this.contact = entity.getContact();
        this.email = entity.getEmail();
        this.gender = entity.getGender();
        this.zipCode = entity.getZipCode();
        this.address = entity.getAddress();
        this.detailAddress = entity.getDetailAddress();
        this.role = entity.getRole() != null ? entity.getRole().name() : null;
        this.createdAt = entity.getCreatedAt();
        this.profileImagePath = entity.getProfileImagePath();
        this.profileOriginalName = entity.getProfileOriginalName();
    }
}