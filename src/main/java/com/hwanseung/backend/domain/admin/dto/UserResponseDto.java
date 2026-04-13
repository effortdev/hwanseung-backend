package com.hwanseung.backend.domain.admin.dto;

public record UserResponseDto(
        Long id,
        String username,
        String nickname,
        Integer trustScore,
        Integer reportCount,
        Status status
) {}