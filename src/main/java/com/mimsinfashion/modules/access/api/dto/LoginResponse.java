package com.mimsinfashion.modules.access.api.dto;

import java.util.Set;

public record LoginResponse(
    String accessToken,
    String tokenType,
    long expiresIn,
    UserInfo user
) {
    public record UserInfo(
        Long id,
        String email,
        String firstName,
        String lastName,
        Set<String> roles,
        Set<String> permissions
    ) {}
}
