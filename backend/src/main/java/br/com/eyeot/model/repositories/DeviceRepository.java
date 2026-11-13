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
        JOIN dom.owner u
        WHERE u.id_user = :idUser
    """)
    List<Device> findMyDevices(Integer idUser);


    @Query(value = "SELECT EXISTS(SELECT 1 FROM eyeot_devices WHERE deviceId = :deviceId)", nativeQuery = true)
    boolean existsByDeviceId(String deviceId);

    @Query("""
        SELECT d
        FROM Device d
        WHERE d.domain.id_domain = :idDomain
        """)
    List<Device> findAllByDomain_Id_domain(
        @Param("idDomain") Integer idDomain
    );

    Optional<Device> findByDeviceId(String deviceId);


    @Query("""
        SELECT d
        FROM Device d
        LEFT JOIN FETCH d.owner
        WHERE d.deviceId = :deviceId
    """)
    Optional<Device> findByDeviceIdWithOwner(
        @Param("deviceId") String deviceId
    );
    

}
