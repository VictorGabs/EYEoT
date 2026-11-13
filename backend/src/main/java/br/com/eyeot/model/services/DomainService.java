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

import br.com.eyeot.enuns.RolesEnum;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.domain.DomainFiltroDTO;
import br.com.eyeot.model.dtos.domain.DomainResumeDTO;
import br.com.eyeot.model.dtos.forms.DomainFormDTO;
import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Domain;
import br.com.eyeot.model.entities.User;
import br.com.eyeot.model.repositories.DeviceRepository;
import br.com.eyeot.model.repositories.DomainRepository;
import br.com.eyeot.model.repositories.UserRepository;
import br.com.eyeot.specifications.DomainSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class DomainService {

    private final DomainRepository domainRepository;
    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;


    @Transactional 
    public ResponseEntity<ResponseDTO<Void>> registrarDominio(DomainFormDTO domainFormDTO, UserDTO userDTO){
        
        Optional<Domain> domain = this.domainRepository.findByDomainNameAndUser(domainFormDTO.domainName(), userDTO.id_user());
        
        if (domain.isPresent()) {
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder().status(HttpStatus.BAD_REQUEST.toString()).descricao("Domínio já cadastrado!").data(null).build());
        }

        Domain newDomain = new Domain(null, userRepository.findById(userDTO.id_user()).get(), domainFormDTO.domainName(), domainFormDTO.description(), null);
        domainRepository.save(newDomain);

        return ResponseEntity.ok().body(ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Domínio registrado com sucesso!").build()); 
    }

    public ResponseEntity<ResponseDTO<List<DomainResumeDTO>>> listarMeusDominios(UserDTO userDTO){
        if (userDTO.role() != null && userDTO.role().name().equals("ROLE_ADMIN")) {
            return listarTodosDominios();
        }
        try {
            List<Domain> entities = domainRepository.findMyDomains(userDTO.id_user());
            List<DomainResumeDTO> domains = entities.stream().map((domain) -> 
                new DomainResumeDTO(
                    domain.getId_domain(),
                    domain.getOwner().getId_user(),
                    domain.getOwner().getName(),
                    domain.getOwner().getEmail1(),
                    domain.getDomainName(),
                    domain.getDescription(), 
                    domain.getDevices().size())).toList();

            return ResponseEntity.ok().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Listagem dos meus domínios")
                .data(domains).build());

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Houve um erro ao listar seus domínios, causa:" + e.getMessage())
                .data(null).build());        }
    }

    public ResponseEntity<ResponseDTO<List<DomainResumeDTO>>> listarMeusDominiosResumo(UserDTO userDTO){
        try {
            if (userDTO.role() != null && userDTO.role().name().equals("ADMIN")) {
                List<Domain> entities = domainRepository.findAll();
                List<DomainResumeDTO> domains = entities.stream().map((domain) -> 
                    new DomainResumeDTO(
                        domain.getId_domain(),
                        domain.getOwner().getId_user(),
                        domain.getOwner().getName(),
                        domain.getOwner().getEmail1(), 
                        domain.getDomainName(),
                        domain.getDescription(), 
                        domain.getDevices().size())).toList();

                return ResponseEntity.ok().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Listagem dos domínios")
                    .data(domains.subList(0, Math.min(5, domains.size()))).build());
            }
            List<Domain> entities = domainRepository.findMyDomains(userDTO.id_user());
            List<DomainResumeDTO> domains = entities.stream().map((domain) -> 
                new DomainResumeDTO(
                    domain.getId_domain(), 
                    domain.getOwner().getId_user(),
                    domain.getOwner().getName(),
                    domain.getOwner().getEmail1(), 
                    domain.getDomainName(),
                    domain.getDescription(), 
                    domain.getDevices().size())).toList();

            return ResponseEntity.ok().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Listagem dos meus domínios")
                .data(domains.subList(0, Math.min(5, domains.size()))).build());

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Houve um erro ao listar seus domínios, causa:" + e.getMessage())
                .data(null).build());        }
    }

    public ResponseEntity<ResponseDTO<List<DomainResumeDTO>>> listarMeusDominiosPaginado(DomainFiltroDTO domainFiltroDTO, UserDTO userDTO, Pageable pageable){
        try {
        
            Specification<Domain> specification = DomainSpecification.comFiltro(domainFiltroDTO, userDTO.id_user(), userDTO.role());

            
            Sort sort = Sort.by(Sort.Order.asc("domainName"));

            Pageable pageableOrdenado = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
            );

            Page<Domain> entities = domainRepository.findAll(specification,pageableOrdenado);

            List<DomainResumeDTO> domains = entities.getContent().stream().map((domain) -> 
                new DomainResumeDTO(
                    domain.getId_domain(),
                    domain.getOwner().getId_user(), 
                    domain.getOwner().getName(),
                    domain.getOwner().getEmail1(), 
                    domain.getDomainName(),
                    domain.getDescription(), 
                    domain.getDevices().size())).toList();

                
            return ResponseEntity.ok().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Listagem dos meus domínios")
                .data(domains)
                .pageNumber(entities.getNumber())
                .totalElements(entities.getTotalElements())
                .totalPages(entities.getTotalPages())
                .pageSize(entities.getSize()).build());
        

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Houve um erro ao listar seus domínios, causa:" + e.getMessage())
                .data(null).build());        }
    }

    public ResponseEntity<ResponseDTO<List<DomainResumeDTO>>> listarTodosDominios(){
        try {
            List<Domain> entities = domainRepository.findAll();
            List<DomainResumeDTO> domains = entities.stream().map((domain) -> 
                new DomainResumeDTO(
                    domain.getId_domain(),
                    domain.getOwner().getId_user(), 
                    domain.getOwner().getName(),
                    domain.getOwner().getEmail1(), 
                    domain.getDomainName(),
                    domain.getDescription(), 
                    domain.getDevices().size())).toList();

            return ResponseEntity.ok().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Listagem dos domínios")
                .data(domains).build());

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(ResponseDTO.<List<DomainResumeDTO>>builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .descricao("Houve um erro ao listar os domínios, causa:" + e.getMessage())
                .data(null).build());        }
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> deletarDominio(Integer id_domain, UserDTO userDTO){
        Optional<Domain> domain = domainRepository.findById(id_domain);
        
        if (domain.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder().status(HttpStatus.BAD_REQUEST.toString()).descricao("Domínio não encontrado!").data(null).build());
        }

        if (!domain.get().getOwner().getId_user().equals(userDTO.id_user()) && (userDTO.role() == null || !userDTO.role().name().equals("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ResponseDTO.<Void>builder().status(HttpStatus.FORBIDDEN.toString()).descricao("Você não tem permissão para deletar este domínio!").data(null).build());
        }

        List<Device> dispositivos = deviceRepository.findAllByDomain_Id_domain(id_domain);

        for (Device dispositivo : dispositivos) {
            dispositivo.setDomain(null);
        }

        deviceRepository.saveAll(dispositivos);

        domainRepository.delete(domain.get());

        return ResponseEntity.ok().body(ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Domínio deletado com sucesso!").build()); 
    }


    //TODO: Implementar a troca de owners pelo administrador apenas
    @Transactional
    public ResponseEntity<ResponseDTO<Void>> atualizarDominio(
            Integer id_domain,
            DomainFormDTO domainFormDTO,
            UserDTO userDTO) {

        boolean isAdmin = userDTO.role() == RolesEnum.ROLE_ADMIN;

        Optional<Domain> domainOptional =
            domainRepository.findById(id_domain);

        if (domainOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseDTO.<Void>builder()
                    .status(HttpStatus.NOT_FOUND.toString())
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
                    .descricao("Você não tem permissão para atualizar este domínio!")
                    .build()
            );
        }

        if (domainFormDTO.id_owner() != null) {

            if (!isAdmin) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.FORBIDDEN.toString())
                        .descricao("Somente administradores podem alterar o proprietário do domínio!")
                        .build()
                );
            }

            Optional<User> ownerOptional =
                userRepository.findById(domainFormDTO.id_owner());

            if (ownerOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ResponseDTO.<Void>builder()
                        .status(HttpStatus.BAD_REQUEST.toString())
                        .descricao("Novo proprietário não encontrado!")
                        .build()
                );
            }

            User novoOwner = ownerOptional.get();

            domain.setOwner(novoOwner);

            List<Device> devices =
                deviceRepository.findAllByDomain_Id_domain(id_domain);

            for (Device device : devices) {
                device.setOwner(novoOwner);
            }

            deviceRepository.saveAll(devices);
        }

        if (domainFormDTO.domainName() != null) {
            domain.setDomainName(domainFormDTO.domainName());
        }

        if (domainFormDTO.description() != null) {
            domain.setDescription(domainFormDTO.description());
        }

        

        domainRepository.save(domain);

        return ResponseEntity.ok(
            ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Domínio atualizado com sucesso!")
                .build()
        );
    }

    
}
