package br.com.eyeot.model.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.eyeot.model.dtos.telemetry.TelemetryPointDTO;
import br.com.eyeot.model.repositories.TelemetryRepository;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class TelemetryService {

    private final TelemetryRepository repository;

    @Transactional(readOnly = true)
    public List<TelemetryPointDTO> buscarHistorico(Integer idDevice) {
        return repository.findPointsByDevice(idDevice);
    }

    @Transactional(readOnly = true)
    public List<TelemetryPointDTO> buscarTelemetria(Long idTelemetryData) {
        return repository.findPointsByTelemetry(idTelemetryData);
    }

    @Transactional(readOnly = true)
    public List<TelemetryPointDTO> buscarHistoricoPorPeriodo(
            Integer idDevice,
            LocalDateTime inicio,
            LocalDateTime fim) {

        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new IllegalArgumentException("O período informado é inválido.");
        }

        return repository.findPointsByDeviceAndPeriod(
            idDevice,
            inicio,
            fim
        );
    }

    @Transactional(readOnly = true)
    public List<TelemetryPointDTO> buscarUltimasLeituras(Integer idDevice) {
        return repository.findLatestPointsByDevice(idDevice);
    }
}
