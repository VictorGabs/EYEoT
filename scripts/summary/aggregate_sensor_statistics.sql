
BEGIN;

WITH readings AS (

    -- Sensores REAL
    SELECT
        d.id_device,
        r.index AS sensor_index,
        st.summary_period,
        td.timestamp AS telemetry_timestamp,
        r.realvalue::double precision AS value
    FROM omni_telemetrydata_real r
    JOIN omni_telemetrydata td
        ON td.id_telemetrydata = r.id_telemetrydata
    JOIN eyeot_devices d
        ON d.deviceid = td.deviceid
    JOIN eyeot_sensor s
        ON s.id_device = d.id_device
        AND s.index = r.index
    JOIN eyeot_sensor_type st
        ON st.id_sensortype = s.sensortype
    WHERE st.datatype = 1
      AND st.summary_period = :'summary_period'::INTEGER

    UNION ALL

    -- Sensores INTEGER
    SELECT
        d.id_device,
        i.index AS sensor_index,
        st.summary_period,
        td.timestamp AS telemetry_timestamp,
        i.integervalue::double precision AS value
    FROM omni_telemetrydata_integer i
    JOIN omni_telemetrydata td
        ON td.id_telemetrydata = i.id_telemetrydata
    JOIN eyeot_devices d
        ON d.deviceid = td.deviceid
    JOIN eyeot_sensor s
        ON s.id_device = d.id_device
        AND s.index = i.index
    JOIN eyeot_sensor_type st
        ON st.id_sensortype = s.sensortype
    WHERE st.datatype = 5
      AND st.summary_period = :'summary_period'::INTEGER
),

bucketed AS (
    SELECT
        id_device,
        sensor_index,
        summary_period,
        date_bin(
            make_interval(mins => summary_period),
            telemetry_timestamp,
            TIMESTAMP '2000-01-01 00:00:00'
        ) AS interval_start,
        value
    FROM readings
),

pending_intervals AS (
    SELECT b.*
    FROM bucketed b
    WHERE NOT EXISTS (
        SELECT 1
        FROM eyeot_sensor_statistics_period existing
        WHERE existing.id_device = b.id_device
          AND existing.index = b.sensor_index
          AND existing.interval_start = b.interval_start
          AND existing.summary_period = b.summary_period
    )
),

aggregated AS (
    SELECT
        id_device,
        sensor_index,
        summary_period,
        interval_start,
        interval_start + make_interval(mins => summary_period)
            AS interval_end,

        MIN(value) AS min_value,
        MAX(value) AS max_value,
        MAX(value) - MIN(value) AS amplitude_value,
        SUM(value) AS sum_value,
        SUM(value * value) AS sum_value_squared,
        AVG(value) AS average_value,

        PERCENTILE_CONT(0.5)
            WITHIN GROUP (ORDER BY value) AS median_value,

        STDDEV_POP(value) AS standard_deviation,
        COUNT(value) AS total_count

    FROM pending_intervals
    GROUP BY
        id_device,
        sensor_index,
        summary_period,
        interval_start
)

INSERT INTO eyeot_sensor_statistics_period (
    id_device,
    index,
    summary_period,
    interval_start,
    interval_end,
    min_value,
    max_value,
    amplitude_value,
    sum_value,
    sum_value_squared,
    average_value,
    median_value,
    standard_deviation,
    total_count
)

SELECT
    id_device,
    sensor_index,
    summary_period,
    interval_start,
    interval_end,
    min_value,
    max_value,
    amplitude_value,
    sum_value,
    sum_value_squared,
    average_value,
    median_value,
    standard_deviation,
    total_count
FROM aggregated
WHERE interval_end <= LOCALTIMESTAMP

ON CONFLICT (
    id_device,
    index,
    interval_start,
    summary_period
) DO NOTHING;

COMMIT;
