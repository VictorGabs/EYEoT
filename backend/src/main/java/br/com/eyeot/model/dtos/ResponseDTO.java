package br.com.eyeot.model.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseDTO<T>(
        String status,
        String descricao,
        T data
    ) {}