package br.com.eyeot.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.eyeot.model.entities.Device;

public interface DeviceRepository extends  JpaRepository<Device, Integer>{

}
