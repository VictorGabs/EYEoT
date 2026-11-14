package br.com.eyeot.model.dtos.forms;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;

public record DeviceFormDTO(
    Integer id_domain,
    
    Boolean enable,

    @DecimalMin("-90")
    @DecimalMax("90")
    @Digits(integer = 3, fraction = 6)
    BigDecimal latitude,

    @DecimalMin("-180")
    @DecimalMax("180")
    @Digits(integer = 4, fraction = 6)
    BigDecimal longitude,

    @DecimalMin("-9999.99")
    @DecimalMax("9999.99")
    @Digits(integer = 4, fraction = 2)
    BigDecimal altitude,

    Integer id_owner
) {}