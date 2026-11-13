package br.com.eyeot.model.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.eyeot.model.entities.Sensor;
import br.com.eyeot.model.entities.ids.SensorId;
import br.com.eyeot.model.projections.SensorPeriodStatisticsProjection;

public interface SensorStatisticsPeriodRepository extends Repository<Sensor, SensorId> {


    @Query(value = """
        SELECT
            id_device AS "idDevice",
            index AS "sensorIndex",
            interval_start AS "intervalStart",
            interval_end AS "intervalEnd",
            summary_period AS "summaryPeriod",
            min_value AS "minValue",
            max_value AS "maxValue",
            amplitude_value AS "amplitudeValue",
            sum_value AS "sumValue",
            sum_value_squared AS "sumValueSquared",
            average_value AS "averageValue",
            median_value AS "medianValue",
            standard_deviation AS "standardDeviation",
            total_count AS "totalCount",
            aggregated_at AS "aggregatedAt"
        FROM eyeot_sensor_statistics_period
        WHERE id_device = :idDevice
          AND index = :sensorIndex
          AND summary_period = :summaryPeriod
          AND interval_start >= :start
          AND interval_end <= :end
        ORDER BY interval_start ASC
        """, nativeQuery = true)
    List<SensorPeriodStatisticsProjection> buscarEstatisticasPorPeriodo(
            @Param("idDevice") Integer idDevice,
            @Param("sensorIndex") Short sensorIndex,
            @Param("summaryPeriod") Integer summaryPeriod,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );


    @Query(value = """
        SELECT
            id_device AS "idDevice",
            index AS "sensorIndex",
            interval_start AS "intervalStart",
            interval_end AS "intervalEnd",
            summary_period AS "summaryPeriod",
            min_value AS "minValue",
            max_value AS "maxValue",
            amplitude_value AS "amplitudeValue",
            sum_value AS "sumValue",
            sum_value_squared AS "sumValueSquared",
            average_value AS "averageValue",
            median_value AS "medianValue",
            standard_deviation AS "standardDeviation",
            total_count AS "totalCount",
            aggregated_at AS "aggregatedAt"
        FROM eyeot_sensor_statistics_period
        WHERE id_device = :idDevice
        AND index = :sensorIndex
        AND summary_period = :summaryPeriod
        AND interval_start >= :start
        AND interval_end <= :end
        ORDER BY interval_start DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM eyeot_sensor_statistics_period
            WHERE id_device = :idDevice
            AND index = :sensorIndex
            AND summary_period = :summaryPeriod
            AND interval_start >= :start
            AND interval_end <= :end
        """, nativeQuery = true
    )
    Page<SensorPeriodStatisticsProjection> buscarEstatisticasPaginadas(
        @Param("idDevice") Integer idDevice,
        @Param("sensorIndex") Short sensorIndex,
        @Param("summaryPeriod") Integer summaryPeriod,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );
}