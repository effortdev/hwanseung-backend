package com.hwanseung.backend.domain.admin.dto;

import com.hwanseung.backend.domain.user.entity.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminRoleUpdateDto {

    private Long userId;
    private Role role;
}
