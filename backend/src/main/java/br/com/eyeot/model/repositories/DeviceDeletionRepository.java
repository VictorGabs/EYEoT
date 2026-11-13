package br.com.eyeot.model.repositories;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DeviceDeletionRepository {

    private final JdbcTemplate jdbcTemplate;

    public void deletarDadosTipados(String deviceId) {

        String[] tabelas = {
            "omni_telemetrydata_accelerometer",
            "omni_telemetrydata_boolean",
            "omni_telemetrydata_gps",
            "omni_telemetrydata_integer",
            "omni_telemetrydata_real"
        };

        for (String tabela : tabelas) {

            String sql = """
                DELETE FROM %s
                WHERE id_telemetrydata IN (
                    SELECT id_telemetrydata
                    FROM omni_telemetrydata
                    WHERE deviceid = ?
                )
                """.formatted(tabela);

            jdbcTemplate.update(sql, deviceId);
        }
    }

    public void deletarTelemetrias(String deviceId) {

        String sql = """
            DELETE FROM omni_telemetrydata
            WHERE deviceid = ?
            """;

        jdbcTemplate.update(sql, deviceId);
    }

    public void deletarSensores(Integer idDevice) {

        String sql = """
            DELETE FROM eyeot_sensor
            WHERE id_device = ?
            """;

        jdbcTemplate.update(sql, idDevice);
    }

    public void deletarDadosDoSensor(String deviceId, Short sensorIndex) {

        String sqlBuscarTelemetrias = """
            SELECT DISTINCT dados.id_telemetrydata
            FROM (
                SELECT id_telemetrydata
                FROM omni_telemetrydata_accelerometer
                WHERE "index" = ?

                UNION ALL

                SELECT id_telemetrydata
                FROM omni_telemetrydata_boolean
                WHERE "index" = ?

                UNION ALL

                SELECT id_telemetrydata
                FROM omni_telemetrydata_gps
                WHERE "index" = ?

                UNION ALL

                SELECT id_telemetrydata
                FROM omni_telemetrydata_integer
                WHERE "index" = ?

                UNION ALL

                SELECT id_telemetrydata
                FROM omni_telemetrydata_real
                WHERE "index" = ?
            ) dados
            JOIN omni_telemetrydata td
                ON td.id_telemetrydata = dados.id_telemetrydata
            WHERE td.deviceid = ?
            """;

        List<Long> idsTelemetrias = jdbcTemplate.queryForList(
            sqlBuscarTelemetrias,
            Long.class,
            sensorIndex,
            sensorIndex,
            sensorIndex,
            sensorIndex,
            sensorIndex,
            deviceId
        );

        String[] tabelas = {
            "omni_telemetrydata_accelerometer",
            "omni_telemetrydata_boolean",
            "omni_telemetrydata_gps",
            "omni_telemetrydata_integer",
            "omni_telemetrydata_real"
        };

        for (String tabela : tabelas) {

            String sql = """
                DELETE FROM %s
                WHERE "index" = ?
                AND id_telemetrydata IN (
                    SELECT id_telemetrydata
                    FROM omni_telemetrydata
                    WHERE deviceid = ?
                )
                """.formatted(tabela);

            jdbcTemplate.update(sql, sensorIndex, deviceId);
        }

        if (!idsTelemetrias.isEmpty()) {

            String placeholders = String.join(
                ",",
                java.util.Collections.nCopies(idsTelemetrias.size(), "?")
            );

            String sqlDeletarTelemetrias = """
                DELETE FROM omni_telemetrydata td
                WHERE td.id_telemetrydata IN (%s)
                AND NOT EXISTS (
                    SELECT 1
                    FROM omni_telemetrydata_accelerometer v
                    WHERE v.id_telemetrydata = td.id_telemetrydata
                )
                AND NOT EXISTS (
                    SELECT 1
                    FROM omni_telemetrydata_boolean v
                    WHERE v.id_telemetrydata = td.id_telemetrydata
                )
                AND NOT EXISTS (
                    SELECT 1
                    FROM omni_telemetrydata_gps v
                    WHERE v.id_telemetrydata = td.id_telemetrydata
                )
                AND NOT EXISTS (
                    SELECT 1
                    FROM omni_telemetrydata_integer v
                    WHERE v.id_telemetrydata = td.id_telemetrydata
                )
                AND NOT EXISTS (
                    SELECT 1
                    FROM omni_telemetrydata_real v
                    WHERE v.id_telemetrydata = td.id_telemetrydata
                )
                """.formatted(placeholders);

            jdbcTemplate.update(sqlDeletarTelemetrias, idsTelemetrias.toArray());
        }
    }
}