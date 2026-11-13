package br.com.eyeot.model.dtos.device;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DeviceResumeDTO(
    Integer id_device,
    String deviceId,
    Integer id_domain,
    String domainName,
    Integer id_owner,
    String ownerName,
    String ownerEmail,
    LocalDateTime creationDate,
    Boolean enable,
    BigDecimal latitude,
    BigDecimal longitude,
    BigDecimal altitude,
    Integer numSensors
) {}