package com.essjr.DinMonex.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppUserDTO(@NotNull Long id,
                         @NotBlank String name,
                         @NotBlank @Email String email) {
}
