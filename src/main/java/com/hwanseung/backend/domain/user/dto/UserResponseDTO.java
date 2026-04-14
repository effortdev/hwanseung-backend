package com.hwanseung.backend.domain.user.dto;

import com.hwanseung.backend.domain.user.entity.User;
import lombok.Data;

@Data
public class UserResponseDTO {
    private Long id;
    private String userid;
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

    public UserResponseDTO(User entity) {
        this.id = entity.getId();
        this.userid = entity.getUserid();
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
    }
}