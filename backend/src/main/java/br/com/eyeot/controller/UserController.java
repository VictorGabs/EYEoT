package br.com.eyeot.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.eyeot.model.dtos.LoginRequestDTO;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.UserResumeDTO;
import br.com.eyeot.model.dtos.forms.AlterarRoleFormDTO;
import br.com.eyeot.model.dtos.forms.UserFormDTO;
import br.com.eyeot.model.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<Void>> login(@Valid @RequestBody LoginRequestDTO loginDTO) {
        return userService.login(loginDTO);
    }
    
    @PostMapping("/cadastro")
    public ResponseEntity<ResponseDTO<Void>> registrarUsuario(@Valid @RequestBody UserFormDTO usuarioFormDTO) {
        return userService.registrarUsuario(usuarioFormDTO);
    }

    @GetMapping("/all/resume")
    public ResponseEntity<ResponseDTO<List<UserResumeDTO>>> listarUsuariosResumidos() {
        return userService.listarUsuariosResumidos();
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> listarTodosUsuariosPaginado(
        @RequestParam (defaultValue = "0") Integer page,
        @RequestParam (defaultValue = "10") Integer size,
        @RequestParam (required = false) String busca,
        @AuthenticationPrincipal UserDTO userDTO
    ) { 
        return userService.listarTodosUsuariosPaginado(busca, userDTO, page, size);
    }

    @PutMapping("/{id}/papel")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> alterarPapel(
            @PathVariable Integer id,
            @RequestBody AlterarRoleFormDTO dto,
            @AuthenticationPrincipal UserDTO userDTO
        ) {

        return userService.alterarRole(id, dto.role(), userDTO);
    }

    @DeleteMapping("/{idUser}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> excluirUsuario(
            @PathVariable Integer idUser,
            @AuthenticationPrincipal UserDTO usuarioLogado) {

        return userService.excluirUsuario(idUser, usuarioLogado);
    }

    @PostMapping("/{idUser}/reset-password")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ResponseDTO<String>> redefinirSenha(
            @PathVariable Integer idUser,
            @AuthenticationPrincipal UserDTO userDTO) {

        return ResponseEntity.ok(userService.redefinirSenha(idUser, userDTO));
    }
    

}
