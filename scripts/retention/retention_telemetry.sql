BEGIN;

-- Armazena as leituras que podem ser excluidas.
CREATE TEMP TABLE retention_candidates (
    id_telemetrydata BIGINT NOT NULL,
    sensor_index SMALLINT NOT NULL,
    source_table TEXT NOT NULL
) ON COMMIT DROP;

-- Identifica leituras expiradas e verifica os resumos.
INSERT INTO retention_candidates (
    id_telemetrydata,
    sensor_index,
    source_table
)
WITH all_telemetry AS (

    SELECT id_telemetrydata, index, 'real' AS source_table
    FROM omni_telemetrydata_real

    UNION ALL

    SELECT id_telemetrydata, index, 'integer'
    FROM omni_telemetrydata_integer

    UNION ALL

    SELECT id_telemetrydata, index, 'boolean'
    FROM omni_telemetrydata_boolean

    UNION ALL

    SELECT id_telemetrydata, index, 'gps'
    FROM omni_telemetrydata_gps

    UNION ALL

    SELECT id_telemetrydata, index, 'accelerometer'
    FROM omni_telemetrydata_accelerometer
),

expired AS (
    SELECT
        t.id_telemetrydata,
        t.index AS sensor_index,
        t.source_table,
        d.id_device,
        td.timestamp,
        st.datatype,
        st.summary_period

    FROM all_telemetry t

    JOIN omni_telemetrydata td
        ON td.id_telemetrydata = t.id_telemetrydata

    JOIN eyeot_devices d
        ON d.deviceid = td.deviceid

    JOIN eyeot_sensor s
        ON s.id_device = d.id_device
        AND s.index = t.index

    JOIN eyeot_sensor_type st
        ON st.id_sensortype = s.sensortype

    WHERE st.retention_period IS NOT NULL
      AND td.timestamp IS NOT NULL
      AND td.timestamp
          + make_interval(days => st.retention_period)
          <= LOCALTIMESTAMP
)

SELECT
    e.id_telemetrydata,
    e.sensor_index,
    e.source_table

FROM expired e

WHERE
    -- Tipos que nao exigem resumo periodico.
    e.datatype NOT IN (1, 5)

    OR

    -- REAL e INTEGER exigem resumo existente.
    (
        e.datatype IN (1, 5)
        AND e.summary_period IS NOT NULL
        AND EXISTS (
            SELECT 1
            FROM eyeot_sensor_statistics_period p
            WHERE p.id_device = e.id_device
              AND p.index = e.sensor_index
              AND p.summary_period = e.summary_period
              AND p.interval_start = date_bin(
                  make_interval(mins => e.summary_period),
                  e.timestamp,
                  TIMESTAMP '2000-01-01 00:00:00'
              )
        )
    );

-- Executa as exclusoes na ordem correta.
DO $$
DECLARE
    deleted_real BIGINT;
    deleted_integer BIGINT;
    deleted_boolean BIGINT;
    deleted_gps BIGINT;
    deleted_accelerometer BIGINT;
    deleted_parents BIGINT;
BEGIN

    DELETE FROM omni_telemetrydata_real r
    USING retention_candidates c
    WHERE c.source_table = 'real'
      AND r.id_telemetrydata = c.id_telemetrydata
      AND r.index = c.sensor_index;

    GET DIAGNOSTICS deleted_real = ROW_COUNT;

    DELETE FROM omni_telemetrydata_integer i
    USING retention_candidates c
    WHERE c.source_table = 'integer'
      AND i.id_telemetrydata = c.id_telemetrydata
      AND i.index = c.sensor_index;

    GET DIAGNOSTICS deleted_integer = ROW_COUNT;

    DELETE FROM omni_telemetrydata_boolean b
    USING retention_candidates c
    WHERE c.source_table = 'boolean'
      AND b.id_telemetrydata = c.id_telemetrydata
      AND b.index = c.sensor_index;

    GET DIAGNOSTICS deleted_boolean = ROW_COUNT;

    DELETE FROM omni_telemetrydata_gps g
    USING retention_candidates c
    WHERE c.source_table = 'gps'
      AND g.id_telemetrydata = c.id_telemetrydata
      AND g.index = c.sensor_index;

    GET DIAGNOSTICS deleted_gps = ROW_COUNT;

    DELETE FROM omni_telemetrydata_accelerometer a
    USING retention_candidates c
    WHERE c.source_table = 'accelerometer'
      AND a.id_telemetrydata = c.id_telemetrydata
      AND a.index = c.sensor_index;

    GET DIAGNOSTICS deleted_accelerometer = ROW_COUNT;

    -- Remove pais somente quando nao restar nenhuma leitura.
    DELETE FROM omni_telemetrydata td
    WHERE EXISTS (
        SELECT 1
        FROM retention_candidates c
        WHERE c.id_telemetrydata = td.id_telemetrydata
    )
    AND NOT EXISTS (
        SELECT 1 FROM omni_telemetrydata_real r
        WHERE r.id_telemetrydata = td.id_telemetrydata
    )
    AND NOT EXISTS (
        SELECT 1 FROM omni_telemetrydata_integer i
        WHERE i.id_telemetrydata = td.id_telemetrydata
    )
    AND NOT EXISTS (
        SELECT 1 FROM omni_telemetrydata_boolean b
        WHERE b.id_telemetrydata = td.id_telemetrydata
    )
    AND NOT EXISTS (
        SELECT 1 FROM omni_telemetrydata_gps g
        WHERE g.id_telemetrydata = td.id_telemetrydata
    )
    AND NOT EXISTS (
        SELECT 1 FROM omni_telemetrydata_accelerometer a
        WHERE a.id_telemetrydata = td.id_telemetrydata
    );

    GET DIAGNOSTICS deleted_parents = ROW_COUNT;

    RAISE NOTICE 'REAL: %', deleted_real;
    RAISE NOTICE 'INTEGER: %', deleted_integer;
    RAISE NOTICE 'BOOLEAN: %', deleted_boolean;
    RAISE NOTICE 'GPS: %', deleted_gps;
    RAISE NOTICE 'ACCELEROMETER: %', deleted_accelerometer;
    RAISE NOTICE 'Registros principais: %', deleted_parents;

END $$;

-- Confirma permanentemente as exclusoes.
COMMIT;
