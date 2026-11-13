package br.com.eyeot.model.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.eyeot.enuns.SensorTypeDataTypeEnum;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.sensor.SensorTypeDTO;
import br.com.eyeot.model.dtos.sensor.SensorTypeFilterDTO;
import br.com.eyeot.model.dtos.sensor.SensorTypeResumeDTO;
import br.com.eyeot.model.entities.SensorType;
import br.com.eyeot.model.repositories.SensorTypeRepository;
import br.com.eyeot.specifications.SensorTypeSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SensorTypeService {
    
    private final SensorTypeRepository sensorTypeRepository;


    @Transactional 
    public ResponseEntity<ResponseDTO<Void>> cadastrarTipoSensor(
            SensorTypeDTO dto
    ) {
        try {

            if (!SensorTypeDataTypeEnum.isValid(dto.getDataType())) {
                throw new IllegalArgumentException(
                    "Tipo de dado inválido: " + dto.getDataType()
                );
            }

            SensorType sensorType = SensorType.builder()
                .description(dto.getDescription())
                .dataType(dto.getDataType())
                .minValue(dto.getMinValue())
                .maxValue(dto.getMaxValue())
                .physicalQuantity(dto.getPhysicalQuantity())
                .measureUnit(dto.getMeasureUnit())
                .averageInterval(dto.getAverageInterval())
                .build();

            sensorTypeRepository.save(sensorType);

            return ResponseEntity.ok(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Tipo de sensor cadastrado com sucesso.")
                    .build()
            );

        } catch (IllegalArgumentException e) {

            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao(e.getMessage())
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<Void>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Erro ao cadastrar tipo de sensor."
                    )
                    .build()
            );
        }
    }

      public ResponseEntity<ResponseDTO<List<SensorTypeResumeDTO>>> listarSensorTypes() {

        try {

            List<SensorType> entities =
                    sensorTypeRepository.findAll(
                        Sort.by(
                            Sort.Order.asc("description")
                        )
                    );

            List<SensorTypeResumeDTO> sensorTypes =
                    entities.stream()
                        .map(sensorType ->
                            new SensorTypeResumeDTO(
                                sensorType.getId(),
                                sensorType.getDescription(),
                                sensorType.getDataType(),
                                sensorType.getMinValue(),
                                sensorType.getMaxValue(),
                                sensorType.getPhysicalQuantity(),
                                sensorType.getMeasureUnit(),
                                sensorType.getAverageInterval(),
                                sensorType.getSensors().size()
                            )
                        )
                        .toList();

            return ResponseEntity.ok(
                ResponseDTO.<List<SensorTypeResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Listagem dos tipos de sensores")
                    .data(sensorTypes)
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<List<SensorTypeResumeDTO>>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao listar os tipos de sensores, causa: "
                        + e.getMessage()
                    )
                    .data(null)
                    .build()
            );
        }
    }


        public ResponseEntity<ResponseDTO<List<SensorTypeResumeDTO>>> listarSensorTypesPaginado(
            SensorTypeFilterDTO sensorTypeFiltroDTO,
            Pageable pageable
    ) {

        try {

            Specification<SensorType> specification =
                    SensorTypeSpecification.comFiltro(
                        sensorTypeFiltroDTO.busca()
                    );

            Sort sort = Sort.by(
                Sort.Order.asc("description")
            );

            Pageable pageableOrdenado =
                    PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        sort
                    );

            Page<SensorType> entities =
                    sensorTypeRepository.findAll(
                        specification,
                        pageableOrdenado
                    );

            List<SensorTypeResumeDTO> sensorTypes =
                    entities.getContent()
                        .stream()
                        .map(sensorType ->
                            new SensorTypeResumeDTO(
                                sensorType.getId(),
                                sensorType.getDescription(),
                                sensorType.getDataType(),
                                sensorType.getMinValue(),
                                sensorType.getMaxValue(),
                                sensorType.getPhysicalQuantity(),
                                sensorType.getMeasureUnit(),
                                sensorType.getAverageInterval(),
                                sensorType.getSensors().size()
                            )
                        )
                        .toList();

            return ResponseEntity.ok(
                ResponseDTO.<List<SensorTypeResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao(
                        "Listagem dos tipos de sensores"
                    )
                    .data(sensorTypes)
                    .pageNumber(entities.getNumber())
                    .totalElements(entities.getTotalElements())
                    .totalPages(entities.getTotalPages())
                    .pageSize(entities.getSize())
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<List<SensorTypeResumeDTO>>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao listar os tipos de sensores, causa: "
                        + e.getMessage()
                    )
                    .data(null)
                    .build()
            );
        }
    }

    public ResponseEntity<ResponseDTO<Void>> deletarTipoSensor(Integer sensorTypeId) {

        try {

            if (!sensorTypeRepository.existsById(sensorTypeId)) {
                return ResponseEntity.badRequest().body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .descricao(
                            "Tipo de sensor não encontrado para o ID: "
                            + sensorTypeId
                        )
                        .build()
                );
            }

            sensorTypeRepository.deleteById(sensorTypeId);

            return ResponseEntity.ok(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Tipo de sensor deletado com sucesso.")
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<Void>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao deletar o tipo de sensor, causa: "
                        + e.getMessage()
                    )
                    .build()
            );
        }
    }

    public ResponseEntity<ResponseDTO<Void>> atualizarTipoSensor(
            Integer sensorTypeId,
            SensorTypeDTO dto
    ) {

        try {

            if (!sensorTypeRepository.existsById(sensorTypeId)) {
                return ResponseEntity.badRequest().body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .descricao(
                            "Tipo de sensor não encontrado para o ID: "
                            + sensorTypeId
                        )
                        .build()
                );
            }

            SensorType sensorType = sensorTypeRepository.findById(sensorTypeId).get();

            sensorType.setDescription(dto.getDescription());
            sensorType.setDataType(dto.getDataType());
            sensorType.setMinValue(dto.getMinValue());
            sensorType.setMaxValue(dto.getMaxValue());
            sensorType.setPhysicalQuantity(dto.getPhysicalQuantity());
            sensorType.setMeasureUnit(dto.getMeasureUnit());
            sensorType.setAverageInterval(dto.getAverageInterval());

            sensorTypeRepository.save(sensorType);

            return ResponseEntity.ok(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Tipo de sensor atualizado com sucesso.")
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<Void>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao atualizar o tipo de sensor, causa: "
                        + e.getMessage()
                    )
                    .build()
            );
        }
    }
}

