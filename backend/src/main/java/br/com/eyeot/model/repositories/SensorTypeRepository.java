package br.com.eyeot.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import br.com.eyeot.model.entities.SensorType;

public interface SensorTypeRepository extends  JpaRepository<SensorType, Integer>, JpaSpecificationExecutor<SensorType>{

}
