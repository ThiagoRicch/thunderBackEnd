package com.example.thunder.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ProfileRequest(@NotEmpty(message = "nome é obrigatório") String name,
                             @NotEmpty(message = "email é obrigatório") String email) {
}
