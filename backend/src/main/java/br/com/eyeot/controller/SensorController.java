package br.com.eyeot.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.forms.SensorFormDTO;
import br.com.eyeot.model.dtos.sensor.SensorFilterDTO;
import br.com.eyeot.model.dtos.sensor.SensorResumeDTO;
import br.com.eyeot.model.services.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/v1/sensors")
@RequiredArgsConstructor 
public class SensorController {

    private final SensorService sensorService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Void>> registrarSensor(
            @Valid @RequestBody SensorFormDTO sensorFormDTO,
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return sensorService.registrarSensor(
            sensorFormDTO,
            userDTO
        );
    }

    @GetMapping("/my/all")
    public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarMeusSensores(
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return sensorService.listarMeusSensores(userDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarMeusSensoresPaginado(
            @ModelAttribute SensorFilterDTO filtro,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return sensorService.listarMeusSensoresPaginado(
            filtro,
            userDTO,
            pageable
        );
    }

    @PutMapping("/update/{idDevice}/{index}")
    public ResponseEntity<ResponseDTO<Void>> editar(
            @PathVariable Integer idDevice,
            @PathVariable Short index,
            @Valid @RequestBody SensorFormDTO dto,
            @AuthenticationPrincipal UserDTO userDTO) {


        return sensorService.atualizarSensor(idDevice, index, dto, userDTO);
    }

    @DeleteMapping("/delete/{idDevice}/{index}")
    public ResponseEntity<ResponseDTO<Void>> excluir(
            @PathVariable Integer idDevice,
            @PathVariable Short index,
            @AuthenticationPrincipal UserDTO userDTO) {

        return sensorService.deletarSensor(idDevice, index, userDTO);
    }

    @GetMapping("/device/{idDevice}")
    public ResponseEntity<ResponseDTO<List<SensorResumeDTO>>> listarSensoresDoDispositivo(
            @PathVariable Integer idDevice,
            @AuthenticationPrincipal UserDTO userDTO) {

        return sensorService.listarSensoresDoDispositivo(idDevice, userDTO);
    }

}
