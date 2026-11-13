package br.com.eyeot.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.eyeot.model.entities.Sensor;
import br.com.eyeot.model.entities.ids.SensorId;

public interface SensorRepository extends JpaRepository<Sensor, SensorId>, JpaSpecificationExecutor<Sensor> {

    @Query("""
        SELECT s
        FROM Sensor s
        JOIN s.device d
        JOIN d.domain dom
        JOIN dom.owner u
        WHERE u.id_user = :idUser
    """)
    List<Sensor> findMySensors(Integer idUser);

    @Query("""
        SELECT s
        FROM Sensor s
        WHERE s.sensorId.id_device = :idDevice
        ORDER BY s.sensorId.index
    """)
    List<Sensor> findSensoresDoDispositivo(
        @Param("idDevice") Integer idDevice
    );

    @Query("""
        SELECT COUNT(s) > 0
        FROM Sensor s
        WHERE s.sensorId.id_device = :idDevice
        AND s.sensorId.index = :sensorIndex
        AND s.sensorType.dataType IN (1, 5)
    """)
    boolean existsNumericSensor(
        @Param("idDevice") Integer idDevice,
        @Param("sensorIndex") Short sensorIndex
    );

}
