package br.com.eyeot.model.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.com.eyeot.model.entities.Sensor;
import br.com.eyeot.model.entities.ids.SensorId;

public interface SensorRepository extends JpaRepository<Sensor, SensorId>, JpaSpecificationExecutor<Sensor> {

    @Query("""
        SELECT s
        FROM Sensor s
        JOIN s.device d
        JOIN d.domain dom
        JOIN dom.user u
        WHERE u.id_user = :idUser
    """)
    List<Sensor> findMySensors(Integer idUser);

}
