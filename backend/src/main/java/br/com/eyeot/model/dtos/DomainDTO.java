package br.com.eyeot.model.dtos;

import java.util.List;

public record DomainDTO(
    Integer id_domain,
    UserDTO userDTO,
    String domainName,
    String description,
    List<DeviceDTO> devices
) {}
