package br.com.eyeot.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.device.DeviceFilterDTO;
import br.com.eyeot.model.dtos.device.DeviceResumeDTO;
import br.com.eyeot.model.dtos.forms.DeviceFormDTO;
import br.com.eyeot.model.dtos.statistics.SensorStatisticsDTO;
import br.com.eyeot.model.dtos.telemetry.TelemetryPointDTO;
import br.com.eyeot.model.services.DeviceService;
import br.com.eyeot.model.services.SensorStatisticsService;
import br.com.eyeot.model.services.TelemetryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;
    private final TelemetryService telemetryService;
    private final SensorStatisticsService sensorStatisticsService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<String>> registrarDispositivo(
            @Valid @RequestBody DeviceFormDTO deviceFormDTO,
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return deviceService.registrarDispositivo(
            deviceFormDTO,
            userDTO
        );
    }

    @GetMapping("/my/all")
    public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarMeusDispositivos(
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return deviceService.listarMeusDispositivos(userDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarMeusDispositivosPaginado(
            @ModelAttribute DeviceFilterDTO filtro,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return deviceService.listarMeusDispositivosPaginado(
            filtro,
            userDTO,
            pageable
        );
    }

    @PutMapping("/update/{idDevice}")
    public ResponseEntity<ResponseDTO<Void>> editar(
            @PathVariable Integer idDevice,
            @Valid @RequestBody DeviceFormDTO dto,
            @AuthenticationPrincipal UserDTO userDTO) {

        return deviceService.atualizarDispositivo(idDevice, dto, userDTO);
    }

    @DeleteMapping("/delete/{idDevice}")
    public ResponseEntity<ResponseDTO<Void>> excluir(@PathVariable Integer idDevice, @AuthenticationPrincipal UserDTO userDTO) {
        return deviceService.deletarDispositivo(idDevice, userDTO);
    }

    @GetMapping("/{idDevice}/telemetry")
    public ResponseEntity<List<TelemetryPointDTO>> historico(
            @PathVariable Integer idDevice,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fim) {

        if (inicio == null && fim == null) {
            return ResponseEntity.ok(
                telemetryService.buscarHistorico(idDevice)
            );
        }

        if (inicio == null || fim == null || !inicio.isBefore(fim)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
            telemetryService.buscarHistoricoPorPeriodo(
                idDevice,
                inicio,
                fim
            )
        );
    }
    @GetMapping("/{idDevice}/telemetry/latest")
    public ResponseEntity<List<TelemetryPointDTO>> buscarUltimasLeituras(
            @PathVariable Integer idDevice) {

        return ResponseEntity.ok(
            telemetryService.buscarUltimasLeituras(idDevice)
        );
    }

    @GetMapping("/{idDevice}/sensors/{sensorIndex}/statistics")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<List<SensorStatisticsDTO>> buscarEstatisticas(
            @PathVariable Integer idDevice,
            @PathVariable Short sensorIndex,
            @RequestParam Integer summaryPeriod,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end,
            @AuthenticationPrincipal UserDTO usuarioLogado) {

        List<SensorStatisticsDTO> statistics = sensorStatisticsService.buscarEstatisticas(idDevice, sensorIndex, summaryPeriod, start, end, usuarioLogado);

        if (statistics.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(statistics);
    }


    @GetMapping("/{idDevice}/sensors/{sensorIndex}/statistics/page")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<List<SensorStatisticsDTO>>> buscarEstatisticasPaginadas(
            @PathVariable Integer idDevice,
            @PathVariable Short sensorIndex,
            @RequestParam Integer summaryPeriod,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDTO usuarioLogado) {

        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Parâmetros de paginação inválidos."
            );
        }

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
            sensorStatisticsService.buscarEstatisticasPaginadas(idDevice, sensorIndex, summaryPeriod, start, end, pageable, usuarioLogado)
        );
    }


}
