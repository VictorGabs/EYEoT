package br.com.eyeot.model.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Domain;

public interface DeviceRepository extends  JpaRepository<Device, Integer>, JpaSpecificationExecutor<Device>{

    Optional<Device> findByDeviceIdAndDomain(
        String deviceId,
        Domain domain
    );

    @Query("""
        SELECT d
        FROM Device d
        JOIN d.owner o
        WHERE d.id_device = :idDevice
        AND o.id_user = :idUser
    """)
    Optional<Device> findByIdAndOwner(
            @Param("idDevice") Integer idDevice,
            @Param("idUser") Integer idUser
    );

    @Query("""
        SELECT d
        FROM Device d
        JOIN d.domain dom
        JOIN dom.user u
        WHERE u.id_user = :idUser
    """)
    List<Device> findMyDevices(Integer idUser);
}
