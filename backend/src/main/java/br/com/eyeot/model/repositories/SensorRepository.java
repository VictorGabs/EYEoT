package br.com.eyeot.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.eyeot.model.entities.Sensor;
import br.com.eyeot.model.entities.ids.SensorId;

public interface SensorRepository extends JpaRepository<Sensor, SensorId>{

    
}
