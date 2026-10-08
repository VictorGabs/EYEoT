package br.com.eyeot.model.dtos.sensor;

public record SensorResumeDTO(
    Integer id_device,
    Short index,
    String deviceId,
    Integer id_sensortype,
    String sensorTypeDescription,
    String physicalQuantity,
    String measureUnit
) {}