package br.com.eyeot.model.dtos;

public record UserDTO (
    Integer id_user,
    String name,
    String phone,
    String email1,
    String email2
){}
