package br.com.eyeot.model.dtos.forms;

public record DomainFormDTO(
    String domainName,
    String description,
    Integer id_owner
) {}
