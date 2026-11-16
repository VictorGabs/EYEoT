package br.com.eyeot.model.dtos;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDTO(

    @NotBlank(message = "Informe sua senha atual.")
    String currentPassword,

    @NotBlank(message = "Informe a nova senha.")
    @Size(min = 8, message = "A nova senha deve ter pelo menos 8 caracteres.")
    String newPassword

) {}