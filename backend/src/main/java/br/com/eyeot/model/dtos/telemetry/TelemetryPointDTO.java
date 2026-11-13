package br.com.eyeot.model.dtos.telemetry;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder 
public record TelemetryPointDTO(
    Long idTelemetryData,
    Integer idDevice,
    Short sensorIndex,
    String sensorType,
    Short dataType,
    String physicalQuantity,
    String measureUnit,
    LocalDateTime timestamp,
    Double value,
    Double value2,
    Double value3,
    Boolean booleanValue
) {}