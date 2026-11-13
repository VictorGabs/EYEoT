package br.com.eyeot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.eyeot.model.dtos.telemetry.TelemetryPointDTO;
import br.com.eyeot.model.services.TelemetryService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/telemetry")  
@RequiredArgsConstructor 
public class TelemetryController {

    private final TelemetryService telemetryService;

    @GetMapping("/{idTelemetryData}")
    public ResponseEntity<List<TelemetryPointDTO>> telemetria(@PathVariable Long idTelemetryData) {
        return ResponseEntity.ok(telemetryService.buscarTelemetria(idTelemetryData));
    }
}
