package br.com.eyeot.model.dtos.sensor;

public record SensorResumeDTO(
    Integer id_device,
    Short index,
    String deviceId,
    String deviceOwnerName,
    String deviceOwnerEmail,
    Integer id_sensortype,
    Short dataType,
    String sensorTypeDescription,
    String physicalQuantity,
    String measureUnit,
    Integer summaryPeriod
) {}