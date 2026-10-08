package br.com.eyeot.model.services;

import java.time.LocalDateTime;
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
import org.springframework.transaction.annotation.Transactional;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.device.DeviceFilterDTO;
import br.com.eyeot.model.dtos.device.DeviceResumeDTO;
import br.com.eyeot.model.dtos.forms.DeviceFormDTO;
import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Domain;
import br.com.eyeot.model.entities.User;
import br.com.eyeot.model.repositories.DeviceRepository;
import br.com.eyeot.model.repositories.DomainRepository;
import br.com.eyeot.model.repositories.UserRepository;
import br.com.eyeot.specifications.DeviceSpecification;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final DomainRepository domainRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> registrarDispositivo(
            DeviceFormDTO dto,
            UserDTO userDTO) {

        Domain domain = null;

        if (dto.id_domain() != null) {

            Optional<Domain> domainOptional =
                    domainRepository.findById(dto.id_domain());

            if (domainOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .descricao("Domínio não encontrado!")
                        .build()
                );
            }

            domain = domainOptional.get();

            if (!domain.getUser().getId_user().equals(userDTO.id_user())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.toString())
                        .descricao("Você não possui acesso a este domínio!")
                        .build()
                );
            }
        }

        if (!domain.getUser().getId_user().equals(userDTO.id_user())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .descricao("Você não possui acesso a este domínio!")
                    .build()
            );
        }

        Optional<User> ownerOptional =
                userRepository.findById(userDTO.id_user());

        if (ownerOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Usuário não encontrado!")
                    .build()
            );
        }

        User owner = ownerOptional.get();

        Optional<Device> deviceExists =
                deviceRepository.findByDeviceIdAndDomain(
                    dto.deviceId(),
                    domain
                );

        if (deviceExists.isPresent()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Dispositivo já cadastrado neste domínio!")
                    .build()
            );
        }

        Device device = Device.builder()
                .deviceId(dto.deviceId())
                .domain(domain)
                .owner(owner)
                .creationDate(LocalDateTime.now())
                .enable(dto.enable() != null ? dto.enable() : true)
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .altitude(dto.altitude())
                .build();

        deviceRepository.save(device);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Dispositivo registrado com sucesso!")
                .build()
        );
    }

    public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarMeusDispositivos(
            UserDTO userDTO
    ) {

        try {

            List<Device> entities =
                    deviceRepository.findMyDevices(userDTO.id_user());

            List<DeviceResumeDTO> devices =
                    entities.stream()
                        .map(device ->
                            new DeviceResumeDTO(
                                device.getId_device(),
                                device.getDeviceId().trim(),
                                device.getDomain().getId_domain(),
                                device.getDomain().getDomainName(),
                                device.getCreationDate(),
                                device.getEnable(),
                                device.getLatitude(),
                                device.getLongitude(),
                                device.getAltitude(),
                                device.getSensors().size()
                            )
                        )
                        .toList();

            return ResponseEntity.ok(
                ResponseDTO.<List<DeviceResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Listagem dos meus dispositivos")
                    .data(devices)
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<List<DeviceResumeDTO>>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Houve um erro ao listar seus dispositivos, causa: "
                        + e.getMessage()
                    )
                    .data(null)
                    .build()
            );
        }
    }

    public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarMeusDispositivosPaginado(
            DeviceFilterDTO deviceFilterDTO, 
            UserDTO userDTO,
            Pageable pageable
    ) {

        try {

            Specification<Device> specification =
                DeviceSpecification.comFiltro(
                    deviceFilterDTO.busca(),
                    deviceFilterDTO.idDomain(),
                    userDTO.id_user()
                );

            Pageable pageableOrdenado =
                PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(
                        Sort.Order.asc("deviceId")
                    )
                );

            Page<Device> entities =
                deviceRepository.findAll(
                    specification,
                    pageableOrdenado
                );

            List<DeviceResumeDTO> devices =
                entities.getContent()
                    .stream()
                    .map(device ->
                        new DeviceResumeDTO(
                            device.getId_device(),
                            String.valueOf(device.getDeviceId()),
                            device.getDomain().getId_domain(),
                            device.getDomain().getDomainName(),
                            device.getCreationDate(),
                            device.getEnable(),
                            device.getLatitude(),
                            device.getLongitude(),
                            device.getAltitude(),
                            device.getSensors().size()
                        )
                    )
                    .toList();

            return ResponseEntity.ok(
                ResponseDTO.<List<DeviceResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Listagem dos dispositivos")
                    .data(devices)
                    .pageNumber(entities.getNumber())
                    .totalPages(entities.getTotalPages())
                    .totalElements(
                        (int) entities.getTotalElements()
                    )
                    .pageSize(entities.getSize())
                    .build()
            );

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(
                ResponseDTO.<List<DeviceResumeDTO>>builder()
                    .status(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString()
                    )
                    .descricao(
                        "Erro ao listar dispositivos: "
                        + e.getMessage()
                    )
                    .build()
            );
        }
    }
}
