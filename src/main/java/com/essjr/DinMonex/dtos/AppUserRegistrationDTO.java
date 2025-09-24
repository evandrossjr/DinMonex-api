package com.essjr.DinMonex.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record AppUserRegistrationDTO(@NotEmpty(message = "O nome não pode estar em branco.")
                                     String name,
                                     @NotEmpty(message = "O email não pode estar em branco.")
                                     @Email(message = "Formato de email inválido.")
                                     String email,
                                     @NotEmpty(message = "A senha não pode estar em branco.")
                                     @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres.")
                                     String password) {

}
