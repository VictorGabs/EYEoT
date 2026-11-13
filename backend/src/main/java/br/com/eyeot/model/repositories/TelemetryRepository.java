package br.com.eyeot.model.repositories;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import br.com.eyeot.model.dtos.telemetry.TelemetryPointDTO;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor 
public class TelemetryRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final String SELECT_BASE = """
        SELECT
            id_telemetrydata,
            id_device,
            sensor_index,
            sensor_type,
            data_type,
            physical_quantity,
            measure_unit,
            measured_at,
            value,
            value2,
            value3,
            boolean_value
        FROM eyeot_telemetry_points
        """;

    private static final RowMapper<TelemetryPointDTO> ROW_MAPPER =
        (rs, rowNum) -> {
            Timestamp timestamp = rs.getTimestamp("measured_at");

            return new TelemetryPointDTO(
                rs.getLong("id_telemetrydata"),
                rs.getInt("id_device"),
                rs.getShort("sensor_index"),
                rs.getString("sensor_type"),
                rs.getObject("data_type", Short.class),
                rs.getString("physical_quantity"),
                rs.getString("measure_unit"),
                timestamp != null
                    ? timestamp.toLocalDateTime()
                    : null,
                rs.getObject("value", Double.class),
                rs.getObject("value2", Double.class),
                rs.getObject("value3", Double.class),
                rs.getObject("boolean_value", Boolean.class)
            );
        };

    public List<TelemetryPointDTO> findPointsByDevice(
            Integer idDevice) {

        String sql = SELECT_BASE + """
            WHERE id_device = ?
            ORDER BY measured_at, id_telemetrydata, sensor_index
            """;

        return jdbcTemplate.query(sql, ROW_MAPPER, idDevice);
    }

    public List<TelemetryPointDTO> findPointsByTelemetry(
            Long idTelemetryData) {

        String sql = SELECT_BASE + """
            WHERE id_telemetrydata = ?
            ORDER BY sensor_index
            """;

        return jdbcTemplate.query(
            sql,
            ROW_MAPPER,
            idTelemetryData
        );
    }

    public List<TelemetryPointDTO> findPointsByDeviceAndPeriod(
            Integer idDevice,
            LocalDateTime inicio,
            LocalDateTime fim) {

        String sql = SELECT_BASE + """
            WHERE id_device = ?
            AND measured_at >= ?
            AND measured_at < ?
            ORDER BY measured_at, id_telemetrydata, sensor_index
            """;

        return jdbcTemplate.query(
            sql,
            ROW_MAPPER,
            idDevice,
            Timestamp.valueOf(inicio),
            Timestamp.valueOf(fim)
        );
    }
    
    public List<TelemetryPointDTO> findLatestPointsByDevice(Integer idDevice) {

        String sql = SELECT_BASE + """
            WHERE id_device = ?
            AND id_telemetrydata IN (
                SELECT DISTINCT ON (sensor_index)
                    id_telemetrydata
                FROM eyeot_telemetry_points
                WHERE id_device = ?
                ORDER BY sensor_index, measured_at DESC, id_telemetrydata DESC
            )
            ORDER BY sensor_index
            """;

        return jdbcTemplate.query(
            sql,
            ROW_MAPPER,
            idDevice,
            idDevice
        );
    }
}