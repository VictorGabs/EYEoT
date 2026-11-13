package br.com.eyeot.model.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import br.com.eyeot.enuns.RolesEnum;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.forms.SensorFormDTO;
import br.com.eyeot.model.dtos.sensor.SensorFilterDTO;
import br.com.eyeot.model.dtos.sensor.SensorResumeDTO;
import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Sensor;
import br.com.eyeot.model.entities.SensorType;
import br.com.eyeot.model.entities.ids.SensorId;
import br.com.eyeot.model.repositories.DeviceDeletionRepository;
import br.com.eyeot.model.repositories.DeviceRepository;
import br.com.eyeot.model.repositories.SensorRepository;
import br.com.eyeot.model.repositories.SensorTypeRepository;
import br.com.eyeot.specifications.SensorSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SensorService {

    private final SensorRepository sensorRepository;
    private final DeviceRepository deviceRepository;
    private final DeviceDeletionRepository deviceDeletionRepository;
    private final SensorTypeRepository sensorTypeRepository;

    @Transactional 
    public ResponseEntity<ResponseDTO<Void>> registrarSensor(
            SensorFormDTO sensorFormDTO,
            UserDTO userDTO
    ) {

        Optional<Device> device =
                deviceRepository.findById(
                    sensorFormDTO.id_device()
                );

        if (device.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Dispositivo não encontrado!")
                    .data(null)
                    .build()
            );
        }

        Device deviceEntity = device.get();

        if(!deviceEntity.getEnable()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Este dispositivo está desativado!")
                    .data(null)
                    .build()
            );
        }

        if (!deviceEntity.getDomain()
                .getOwner()
                .getId_user()
                .equals(userDTO.id_user()) && (userDTO.role() == null || !userDTO.role().name().equals("ROLE_ADMIN"))) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .descricao(
                        "Você não possui acesso a este dispositivo!"
                    )
                    .data(null)
                    .build()
            );
        }

        Optional<SensorType> sensorType =
                sensorTypeRepository.findById(
                    sensorFormDTO.id_sensortype()
                );

        if (sensorType.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Tipo de sensor não encontrado!")
                    .data(null)
                    .build()
            );
        }

        SensorId sensorId = new SensorId(
            sensorFormDTO.id_device(),
            sensorFormDTO.index()
        );

        if (sensorRepository.existsById(sensorId)) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao(
                        "Já existe um sensor com este índice no dispositivo!"
                    )
                    .data(null)
                    .build()
            );
        }

        Sensor sensor = Sensor.builder()
            .sensorId(sensorId)
            .device(deviceEntity)
            .sensorType(sensorType.get())
            .build();

        sensorRepository.save(sensor);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Sensor registrado com sucesso!")
                .build()
        );
    }

   public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarMeusSensores( UserDTO userDTO) {

        try {
            if(userDTO.role() != null && userDTO.role().name().equals("ROLE_ADMIN")) {
                return listarTodosSensores(userDTO);
            }

            List<Sensor> entities =
                    sensorRepository.findMySensors(
                        userDTO.id_user()
                    );

            List<SensorResumeDTO> sensors =
                    entities.stream()
                        .map(sensor ->
                            new SensorResumeDTO(                                
                                sensor.getSensorId().getId_device(),
                                sensor.getSensorId().getIndex(),
                                sensor.getDevice().getDeviceId().trim(),
                                sensor.getDevice().getOwner().getName(),
                                sensor.getDevice().getOwner().getEmail1(),
                                sensor.getSensorType().getId(),
                                sensor.getSensorType().getDataType(),
                                sensor.getSensorType().getDescription(),
                                sensor.getSensorType().getPhysicalQuantity(),
                                sensor.getSensorType().getMeasureUnit(),
                                sensor.getSensorType().getSummaryPeriod()
                            )
                        )
                        .toList();

            return ResponseEntity.ok(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Listagem dos meus sensores")
                    .data(sensors)
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao listar seus sensores, causa: "
                        + e.getMessage()
                    )
                    .data(null)
                    .build()
            );
        }
    }

    public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarMeusSensoresPaginado(
            SensorFilterDTO sensorFiltroDTO,
            UserDTO userDTO,
            Pageable pageable
    ) {

        try {

            Specification<Sensor> specification =
                    SensorSpecification.comFiltro(
                        sensorFiltroDTO.busca(),
                        userDTO.id_user(),
                        userDTO.role()
                    );

            Sort sort = Sort.by(
                Sort.Order.asc("sensorId.index")
            );

            Pageable pageableOrdenado =
                    PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        sort
                    );

            Page<Sensor> entities =
                    sensorRepository.findAll(
                        specification,
                        pageableOrdenado
                    );

            List<SensorResumeDTO> sensors =
                    entities.getContent()
                        .stream()
                        .map(sensor ->
                            new SensorResumeDTO(
                                sensor.getSensorId().getId_device(),
                                sensor.getSensorId().getIndex(),
                                sensor.getDevice().getDeviceId().trim(),
                                sensor.getDevice().getOwner().getName(),
                                sensor.getDevice().getOwner().getEmail1(),
                                sensor.getSensorType().getId(),
                                sensor.getSensorType().getDataType(),
                                sensor.getSensorType().getDescription(),
                                sensor.getSensorType().getPhysicalQuantity(),
                                sensor.getSensorType().getMeasureUnit(),
                                sensor.getSensorType().getSummaryPeriod()
                            )
                        )
                        .toList();

            return ResponseEntity.ok(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Listagem dos meus sensores")
                    .data(sensors)
                    .pageNumber(entities.getNumber())
                    .totalElements(entities.getTotalElements())
                    .totalPages(entities.getTotalPages())
                    .pageSize(entities.getSize())
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao listar seus sensores, causa: "
                        + e.getMessage()
                    )
                    .data(null)
                    .build()
            );
        }
    }

     public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarTodosSensores(UserDTO userDTO) {

        if(userDTO.role() != null && !userDTO.role().name().equals("ROLE_ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .descricao(
                        "Você não possui permissão para listar todos os sensores!"
                    )
                    .data(null)
                    .build()
            );
        }

        try {

            List<Sensor> entities =
                    sensorRepository.findAll();

            List<SensorResumeDTO> sensors =
                    entities.stream()
                        .map(sensor ->
                            new SensorResumeDTO(
                                sensor.getSensorId().getId_device(),
                                sensor.getSensorId().getIndex(),
                                sensor.getDevice().getDeviceId().trim(),
                                sensor.getDevice().getOwner().getName(),
                                sensor.getDevice().getOwner().getEmail1(),
                                sensor.getSensorType().getId(),
                                sensor.getSensorType().getDataType(),
                                sensor.getSensorType().getDescription(),
                                sensor.getSensorType().getPhysicalQuantity(),
                                sensor.getSensorType().getMeasureUnit(),
                                sensor.getSensorType().getSummaryPeriod()
                            )
                        )
                        .toList();

            return ResponseEntity.ok(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Listagem dos meus sensores")
                    .data(sensors)
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao listar seus sensores, causa: "
                        + e.getMessage()
                    )
                    .data(null)
                    .build()
            );
        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deletarSensor(
            Integer id_device,
            Short index,
            UserDTO userDTO
    ) {

        SensorId sensorId = new SensorId(id_device, index);

        Optional<Sensor> sensor = sensorRepository.findById(sensorId);

        if (sensor.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Sensor não encontrado!")
                    .build()
            );
        }

        Sensor sensorEntity = sensor.get();
        Device device = sensorEntity.getDevice();

        if (!device.getOwner().getId_user().equals(userDTO.id_user())
                && (userDTO.role() == null
                    || !userDTO.role().name().equals("ROLE_ADMIN"))) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .descricao("Você não possui acesso a este sensor!")
                    .build()
            );
        }

        deviceDeletionRepository.deletarDadosDoSensor(
            device.getDeviceId().trim(),
            index
        );

        sensorRepository.delete(sensorEntity);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Sensor deletado com sucesso!")
                .build()
        );
    }

    public ResponseEntity<ResponseDTO<Void>> atualizarSensor(
            Integer id_device,
            Short index,
            SensorFormDTO sensorFormDTO,
            UserDTO userDTO
    ) {

        SensorId sensorId = new SensorId(id_device, index);

        Optional<Sensor> sensor =
                sensorRepository.findById(sensorId);

        if (sensor.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Sensor não encontrado!")
                    .data(null)
                    .build()
            );
        }

        Sensor sensorEntity = sensor.get();

        if (!sensorEntity.getDevice()
                .getDomain()
                .getOwner()
                .getId_user()
                .equals(userDTO.id_user()) && (userDTO.role() == null || !userDTO.role().name().equals("ROLE_ADMIN"))) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .descricao(
                        "Você não possui acesso a este sensor!"
                    )
                    .data(null)
                    .build()
            );
        }

        Optional<SensorType> sensorType =
                sensorTypeRepository.findById(
                    sensorFormDTO.id_sensortype()
                );

        if (sensorType.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Tipo de sensor não encontrado!")
                    .data(null)
                    .build()
            );
        }


        
        sensorEntity.setSensorType(sensorType.get());

        sensorRepository.save(sensorEntity);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Sensor atualizado com sucesso!")
                .build()
        );
    }

    public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarSensoresDoDispositivo(
            Integer idDevice,
            UserDTO userDTO) {

        boolean isAdmin = userDTO.role() == RolesEnum.ROLE_ADMIN;

        Device device = deviceRepository.findById(idDevice)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Dispositivo não encontrado!"
                ));

        if (!isAdmin &&
                (device.getOwner() == null ||
                !device.getOwner().getId_user().equals(userDTO.id_user()))) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem acesso a este dispositivo!"
            );
        }

        List<SensorResumeDTO> sensores =
                sensorRepository.findSensoresDoDispositivo(idDevice)
                        .stream()
                        .map(sensor -> new SensorResumeDTO(
                                sensor.getSensorId().getId_device(),
                                sensor.getSensorId().getIndex(),
                                device.getDeviceId().trim(),
                                device.getOwner() != null
                                        ? device.getOwner().getName()
                                        : null,
                                device.getOwner() != null
                                        ? device.getOwner().getEmail1()
                                        : null,
                                sensor.getSensorType().getId(),
                                sensor.getSensorType().getDataType(),
                                sensor.getSensorType().getDescription(),
                                sensor.getSensorType().getPhysicalQuantity(),
                                sensor.getSensorType().getMeasureUnit(),
                                sensor.getSensorType().getSummaryPeriod()
                        ))
                        .toList();

        return ResponseEntity.ok(
                ResponseDTO.<List<SensorResumeDTO>>builder()
                        .status(HttpStatus.OK.toString())
                        .descricao("Sensores consultados com sucesso!")
                        .data(sensores)
                        .build()
        );
    }
    
}
