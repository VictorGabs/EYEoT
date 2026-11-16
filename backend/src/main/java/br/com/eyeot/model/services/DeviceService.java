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
import org.springframework.web.server.ResponseStatusException;

import br.com.eyeot.enuns.RolesEnum;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.device.DeviceFilterDTO;
import br.com.eyeot.model.dtos.device.DeviceResumeDTO;
import br.com.eyeot.model.dtos.forms.DeviceFormDTO;
import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Domain;
import br.com.eyeot.model.entities.User;
import br.com.eyeot.model.repositories.DeviceDeletionRepository;
import br.com.eyeot.model.repositories.DeviceRepository;
import br.com.eyeot.model.repositories.DomainRepository;
import br.com.eyeot.model.repositories.UserRepository;
import br.com.eyeot.specifications.DeviceSpecification;
import br.com.eyeot.utils.DeviceIdGenerator;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class DeviceService {
    
    private final DeviceRepository deviceRepository;
    private final DomainRepository domainRepository;
    private final UserRepository userRepository;
    private final DeviceIdGenerator deviceIdGenerator;
    private final DeviceDeletionRepository deviceDeletionRepository;

    @Transactional
    public ResponseEntity<ResponseDTO<String>> registrarDispositivo(
            DeviceFormDTO dto,
            UserDTO userDTO) {

        Domain domain = null;

        if (dto.id_domain() != null) {

            Optional<Domain> domainOptional =
                    domainRepository.findById(dto.id_domain());

            if (domainOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ResponseDTO.<String>builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .descricao("Domínio não encontrado!")
                        .build()
                );
            }

            domain = domainOptional.get();
        }

        boolean isAdmin = userDTO.role() == RolesEnum.ROLE_ADMIN;

        if (domain != null) {
            if (!isAdmin &&
                !domain.getOwner().getId_user().equals(userDTO.id_user())) {
                throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não tem acesso ao domínio"
                );
            }
        }

        Optional<User> ownerOptional =
                userRepository.findById(userDTO.id_user());

        if (ownerOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Usuário não encontrado!")
                    .build()
            );
        }

        User usuarioLogado = ownerOptional.get();

        User owner = domain != null
                ? domain.getOwner()
                : usuarioLogado;

        String deviceId = this.gerarDeviceIdUnico();

        Optional<Device> deviceExists =
                deviceRepository.findByDeviceIdAndDomain(
                    deviceId,
                    domain
                );

        if (deviceExists.isPresent()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<String>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Dispositivo já cadastrado neste domínio!")
                    .build()
            );
        }

        Device device = Device.builder()
                .deviceId(deviceId)
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
            ResponseDTO.<String>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Dispositivo registrado com sucesso!")
                .data(device.getDeviceId())
                .build()
        );
    }

    public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarMeusDispositivos(UserDTO userDTO) {

        if(userDTO.role() != null && userDTO.role().name().equals("ROLE_ADMIN")) {
            return listarTodosDispositivos();
        }

        try {

            List<Device> entities =
                    deviceRepository.findMyDevices(userDTO.id_user());

            List<DeviceResumeDTO> devices = entities.stream().map(this::converterParaResumeDTO).toList();

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
                    userDTO.id_user(),
                    userDTO.role()
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

            List<DeviceResumeDTO> devices = entities.getContent().stream().map(this::converterParaResumeDTO).toList();

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

     public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarTodosDispositivos() {

        try {

            List<Device> entities =
                    deviceRepository.findAll();

            List<DeviceResumeDTO> devices = entities.stream().map(this::converterParaResumeDTO).toList();

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

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deletarDispositivo(Integer id_device, UserDTO userDTO) {

        Optional<Device> deviceOptional =
                deviceRepository.findById(id_device);

        if (deviceOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.toString())
                    .descricao("Dispositivo não encontrado!")
                    .build()
            );
        }

        Device device = deviceOptional.get();

        if (!device.getOwner()
                .getId_user()
                .equals(userDTO.id_user())
                && (userDTO.role() == null
                    || !userDTO.role().name().equals("ROLE_ADMIN"))) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .descricao("Você não possui acesso a este dispositivo!")
                    .build()
            );
        }

        String deviceId = device.getDeviceId().trim();

        deviceDeletionRepository.deletarDadosTipados(deviceId);

        deviceDeletionRepository.deletarTelemetrias(deviceId);

        deviceDeletionRepository.deletarSensores(id_device);

        deviceRepository.delete(device);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Dispositivo deletado com sucesso!")
                .data(null)
                .build()
        );
    }


   @Transactional
    public ResponseEntity<ResponseDTO<Void>> atualizarDispositivo(
            Integer id_device,
            DeviceFormDTO dto,
            UserDTO userDTO) {

        boolean isAdmin = userDTO.role() == RolesEnum.ROLE_ADMIN;

        Optional<Device> deviceOptional =
            deviceRepository.findById(id_device);

        if (deviceOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.NOT_FOUND.toString())
                    .descricao("Dispositivo não encontrado!")
                    .build()
            );
        }

        Device device = deviceOptional.get();

        if (!isAdmin &&
                (device.getOwner() == null ||
                !device.getOwner().getId_user().equals(userDTO.id_user()))) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.FORBIDDEN.toString())
                    .descricao("Você não possui acesso a este dispositivo!")
                    .build()
            );
        }

        if (dto.id_owner() != null) {

            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.toString())
                        .descricao("Somente administradores podem alterar o proprietário!")
                        .build()
                );
            }

            Optional<User> ownerOptional =
                userRepository.findById(dto.id_owner());

            if (ownerOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .descricao("Proprietário não encontrado!")
                        .build()
                );
            }

            device.setOwner(ownerOptional.get());

            device.setDomain(null);

        } else if (dto.id_domain() != null) {

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

            Domain domain = domainOptional.get();

            if (!isAdmin &&
                    !domain.getOwner().getId_user().equals(userDTO.id_user())) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.toString())
                        .descricao("Você não pode associar o dispositivo a este domínio!")
                        .build()
                );
            }

            device.setDomain(domain);

            device.setOwner(domain.getOwner());
        }

        if (dto.enable() != null) {
            device.setEnable(dto.enable());
        }

        if (dto.latitude() != null) {
            device.setLatitude(dto.latitude());
        }

        if (dto.longitude() != null) {
            device.setLongitude(dto.longitude());
        }

        if (dto.altitude() != null) {
            device.setAltitude(dto.altitude());
        }

        deviceRepository.save(device);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Dispositivo atualizado com sucesso!")
                .build()
        );
    }


    private String gerarDeviceIdUnico() {
        String deviceId;

        do {
            deviceId = deviceIdGenerator.gerar();
        } while (deviceRepository.existsByDeviceId(deviceId));

        return deviceId;
    }

    private DeviceResumeDTO converterParaResumeDTO(Device device) {

        Domain domain = device.getDomain();
        User owner = device.getOwner();

         return new DeviceResumeDTO(
            device.getId_device(),
            device.getDeviceId().trim(),
            domain != null ? domain.getId_domain() : null,
            domain != null ? domain.getDomainName() : null,
            owner != null ? owner.getId_user() : null,
            owner != null ? owner.getName() : null,
            owner != null ? owner.getEmail1() : null,
            device.getCreationDate(),
            device.getEnable(),
            device.getLatitude(),
            device.getLongitude(),
            device.getAltitude(),
            device.getSensors().size()
        );
    }
}
