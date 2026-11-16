package br.com.eyeot.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserUpdateDTO(

    @NotBlank(message = "O nome é obrigatório.")
    String nameUser,

    String phone,

    @Email(message = "E-mail secundário inválido.")
    String email2

) {}