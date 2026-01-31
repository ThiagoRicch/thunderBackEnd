package com.example.thunder.config;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {
}
