package br.com.eyeot.model.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.TabDTO;
import br.com.eyeot.model.entities.User;
import br.com.eyeot.model.repositories.UserRepository;
import br.com.eyeot.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class TabService {

    private final TokenService tokenService;
    private final UserRepository usuarioRepository;

    public ResponseEntity<ResponseDTO<List<TabDTO>>> getList(HttpServletRequest request){
        String token = tokenService.recoverToken(request);
        String email = tokenService.validarToken(token);
        List<TabDTO> listTab = new ArrayList<>();

        try{
            Optional<User> usuario = usuarioRepository.findByEmail1(email);
            if(usuario.isEmpty()){
               return ResponseEntity.badRequest().body(ResponseDTO.<List<TabDTO>>builder().descricao("Usuario não encontrado").data(null).status(HttpStatus.BAD_REQUEST.toString()).build()); 
            }

            listTab.add(TabDTO.builder().uuid(UUID.randomUUID()).routerLink("/home").icon("home").span("Home").build());
            listTab.add(TabDTO.builder().uuid(UUID.randomUUID()).routerLink("/domains").icon("domain").span("Domínios").build());
            listTab.add(TabDTO.builder().uuid(UUID.randomUUID()).routerLink("/devices").icon("devices_other").span("Dispositivos").build());
            listTab.add(TabDTO.builder().uuid(UUID.randomUUID()).routerLink("/sensors").icon("developer_board").span("Sensores").build());
            if (usuario.get().getPapel().equals("ROLE_ADMIN")) {
                listTab.add(TabDTO.builder().uuid(UUID.randomUUID()).routerLink("/sensor-types").icon("device_hub").span("Tipos de Sensores").build());
                listTab.add(TabDTO.builder().uuid(UUID.randomUUID()).routerLink("/interno/usuarios").icon("people").span("Usuários").build());
            }
            
            return ResponseEntity.ok().body(ResponseDTO.<List<TabDTO>>builder().descricao("Lista de tabs").data(listTab).status(HttpStatus.OK.toString()).build()); 
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(ResponseDTO.<List<TabDTO>>builder().descricao(e.getMessage()).data(null).status(HttpStatus.INTERNAL_SERVER_ERROR.toString()).build()); 
        }
    }
}