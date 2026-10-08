package br.com.eyeot.model.dtos.forms;

import java.math.BigDecimal;

public record SensorTypeFormDTO(
    String description,
    Short dataType,
    BigDecimal minValue,
    BigDecimal maxValue,
    String physicalQuantity,
    String measureUnit,
    Integer averageInterval
) {}
