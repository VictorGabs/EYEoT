package br.com.eyeot.model.dtos.domain;

import java.util.List;

import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.device.DeviceDTO;

public record DomainDTO(
    Integer id_domain,
    UserDTO userDTO,
    String domainName,
    String description,
    List<DeviceDTO> devices
) {}
