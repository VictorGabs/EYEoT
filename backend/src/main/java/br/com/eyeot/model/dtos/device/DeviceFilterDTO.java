package br.com.eyeot.model.dtos.device;

import lombok.Builder;

@Builder 
public record DeviceFilterDTO(String busca, Integer idDomain) {}
    
