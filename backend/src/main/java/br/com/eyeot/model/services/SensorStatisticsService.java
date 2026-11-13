package br.com.eyeot.model.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.statistics.SensorStatisticsDTO;
import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.projections.SensorPeriodStatisticsProjection;
import br.com.eyeot.model.repositories.DeviceRepository;
import br.com.eyeot.model.repositories.SensorRepository;
import br.com.eyeot.model.repositories.SensorStatisticsPeriodRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SensorStatisticsService {

    private final SensorStatisticsPeriodRepository statisticsPeriodRepository;
    private final DeviceRepository deviceRepository;
    private final SensorRepository sensorRepository;

    @Transactional(readOnly = true)
    public List<SensorStatisticsDTO> buscarEstatisticas(
            Integer idDevice,
            Short sensorIndex,
            Integer summaryPeriod,
            LocalDateTime start,
            LocalDateTime end,
            UserDTO usuarioLogado) {

        validarConsulta(
            idDevice, sensorIndex, summaryPeriod,
            start, end, usuarioLogado
        );

        return statisticsPeriodRepository.buscarEstatisticasPorPeriodo(
                idDevice,
                sensorIndex,
                summaryPeriod,
                start,
                end
            )
            .stream()
            .map(this::converterParaDTO)
            .toList();
    }

    @Transactional(readOnly = true)
    public ResponseDTO<List<SensorStatisticsDTO>> buscarEstatisticasPaginadas(
            Integer idDevice,
            Short sensorIndex,
            Integer summaryPeriod,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable,
            UserDTO usuarioLogado) {

        validarConsulta(
            idDevice, sensorIndex, summaryPeriod,
            start, end, usuarioLogado
        );

        Page<SensorStatisticsDTO> statistics =
            statisticsPeriodRepository.buscarEstatisticasPaginadas(
                idDevice,
                sensorIndex,
                summaryPeriod,
                start,
                end,
                pageable
            ).map(this::converterParaDTO);

        return ResponseDTO.<List<SensorStatisticsDTO>>builder()
            .status(HttpStatus.OK.toString())
            .descricao("Estatísticas consultadas com sucesso.")
            .data(statistics.getContent())
            .totalPages(statistics.getTotalPages())
            .pageNumber(statistics.getNumber())
            .totalElements(statistics.getTotalElements())
            .pageSize(statistics.getSize())
            .build();
    }

    private void validarConsulta(
            Integer idDevice,
            Short sensorIndex,
            Integer summaryPeriod,
            LocalDateTime start,
            LocalDateTime end,
            UserDTO usuarioLogado) {

        Device device = deviceRepository.findById(idDevice)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Dispositivo não encontrado."
            ));

        boolean isAdmin = usuarioLogado.role()
            .toString()
            .equals("ROLE_ADMIN");

        boolean isOwner = device.getOwner().getId_user()
            .equals(usuarioLogado.id_user());

        if (!isAdmin && !isOwner) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Você não possui permissão para consultar este dispositivo."
            );
        }

        if (summaryPeriod == null || summaryPeriod <= 0
                || start == null || end == null
                || !start.isBefore(end)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Parâmetros de período inválidos."
            );
        }

        if (!sensorRepository.existsNumericSensor(idDevice, sensorIndex)) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Sensor REAL ou INTEGER não encontrado."
            );
        }
    }

    private SensorStatisticsDTO converterParaDTO(
            SensorPeriodStatisticsProjection projection) {

        return new SensorStatisticsDTO(
            projection.getIdDevice(),
            projection.getSensorIndex(),
            projection.getIntervalStart(),
            projection.getIntervalEnd(),
            projection.getSummaryPeriod(),
            projection.getMinValue(),
            projection.getMaxValue(),
            projection.getAmplitudeValue(),
            projection.getSumValue(),
            projection.getSumValueSquared(),
            projection.getAverageValue(),
            projection.getMedianValue(),
            projection.getStandardDeviation(),
            projection.getTotalCount(),
            projection.getAggregatedAt()
        );
    }
}