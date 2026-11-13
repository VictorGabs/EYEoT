package br.com.eyeot.model.dtos.forms;

import java.math.BigDecimal;

public record DeviceFormDTO(
    Integer id_domain,
    Boolean enable,
    BigDecimal latitude,
    BigDecimal longitude,
    BigDecimal altitude,
    Integer id_owner
) {}