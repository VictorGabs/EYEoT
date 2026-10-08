package br.com.eyeot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.sensor.SensorTypeDTO;
import br.com.eyeot.model.dtos.sensor.SensorTypeFilterDTO;
import br.com.eyeot.model.dtos.sensor.SensorTypeResumeDTO;
import br.com.eyeot.model.services.SensorTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequestMapping ("v1/sensor-types")
@RequiredArgsConstructor 
public class SensorTypeController {

    private final SensorTypeService sensorTypeService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Void>> registrarSensorType(
            @Valid @RequestBody SensorTypeDTO sensorTypeFormDTO
    ) {
        return sensorTypeService.cadastrarTipoSensor(
            sensorTypeFormDTO
        );
    }

    @GetMapping("/all")
    public ResponseEntity<ResponseDTO<List<SensorTypeResumeDTO>>> listarSensorTypes() {
        return sensorTypeService.listarSensorTypes();
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<List<SensorTypeResumeDTO>>> listarSensorTypesPaginado(
            @ModelAttribute SensorTypeFilterDTO filtro,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return sensorTypeService.listarSensorTypesPaginado(
            filtro,
            pageable
        );
    }
    
}
