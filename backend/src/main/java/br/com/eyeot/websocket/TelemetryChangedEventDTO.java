package br.com.eyeot.websocket;

import java.time.LocalDateTime;

public record TelemetryChangedEventDTO(
    Long idTelemetryData,
    String deviceId,
    LocalDateTime timestamp
) {}
