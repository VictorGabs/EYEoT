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

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.forms.SensorFormDTO;
import br.com.eyeot.model.dtos.sensor.SensorFilterDTO;
import br.com.eyeot.model.dtos.sensor.SensorResumeDTO;
import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Sensor;
import br.com.eyeot.model.entities.SensorType;
import br.com.eyeot.model.entities.ids.SensorId;
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

        if (!deviceEntity.getDomain()
                .getUser()
                .getId_user()
                .equals(userDTO.id_user())) {

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

   public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarMeusSensores(
            UserDTO userDTO
    ) {

        try {

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
                                sensor.getSensorType().getId(),
                                sensor.getSensorType().getDescription(),
                                sensor.getSensorType().getPhysicalQuantity(),
                                sensor.getSensorType().getMeasureUnit()
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
                        userDTO.id_user()
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
                                sensor.getSensorType().getId(),
                                sensor.getSensorType().getDescription(),
                                sensor.getSensorType().getPhysicalQuantity(),
                                sensor.getSensorType().getMeasureUnit()
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
    
}
