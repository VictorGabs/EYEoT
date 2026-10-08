package br.com.eyeot.model.dtos.forms;

import java.math.BigDecimal;

public record DeviceFormDTO(
    String deviceId,
    Integer id_domain,
    Boolean enable,
    BigDecimal latitude,
    BigDecimal longitude,
    BigDecimal altitude
) {}