package br.com.eyeot.model.dtos;

import java.security.Principal;

import br.com.eyeot.enuns.RolesEnum;

public record UserDTO(
    Integer id_user,
    RolesEnum role,
    String name,
    String phone,
    String email1,
    String email2
) implements Principal {

    @Override
    public String getName() {
        return email1;
    }
}