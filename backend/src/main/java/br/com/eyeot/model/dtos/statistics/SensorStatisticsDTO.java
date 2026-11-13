package br.com.eyeot.model.dtos.statistics;

import java.time.LocalDateTime;

public record SensorStatisticsDTO(
    Integer idDevice,
    Short sensorIndex,
    LocalDateTime intervalStart,
    LocalDateTime intervalEnd,
    Integer summaryPeriod,
    Double minValue,
    Double maxValue,
    Double amplitudeValue,
    Double sumValue,
    Double sumValueSquared,
    Double averageValue,
    Double medianValue,
    Double standardDeviation,
    Long totalCount,
    LocalDateTime aggregatedAt
) {}