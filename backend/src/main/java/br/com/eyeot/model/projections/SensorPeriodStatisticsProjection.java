package br.com.eyeot.model.projections;

import java.time.LocalDateTime;

public interface SensorPeriodStatisticsProjection {

    Integer getIdDevice();

    Short getSensorIndex();

    LocalDateTime getIntervalStart();

    LocalDateTime getIntervalEnd();

    Integer getSummaryPeriod();

    Double getMinValue();

    Double getMaxValue();

    Double getAmplitudeValue();

    Double getSumValue();

    Double getSumValueSquared();

    Double getAverageValue();

    Double getMedianValue();

    Double getStandardDeviation();

    Long getTotalCount();

    LocalDateTime getAggregatedAt();
}