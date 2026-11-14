package br.com.eyeot.model.dtos.sensor;

import java.math.BigDecimal;

public record SensorTypeResumeDTO(
    Integer id,
    String description,
    Short dataType,
    BigDecimal minValue,
    BigDecimal maxValue,
    String physicalQuantity,
    String measureUnit,
    Integer averageInterval,
    Integer retentionPeriod,
    Integer summaryPeriod,
    Integer numSensors
) {}