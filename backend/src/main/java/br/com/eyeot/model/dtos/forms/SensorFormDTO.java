package br.com.eyeot.model.dtos.forms;

public record SensorFormDTO(
    Integer id_device,
    Short index,
    Integer id_sensortype
) {}