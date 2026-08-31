package br.com.eyeot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.eyeot.model.dtos.LoginRequestDTO;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.forms.UserFormDTO;
import br.com.eyeot.model.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("v1/users")
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

}
