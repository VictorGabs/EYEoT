package br.com.eyeot.model.services;

import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.eyeot.model.dtos.LoginRequestDTO;
import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.forms.UserFormDTO;
import br.com.eyeot.model.entities.User;
import br.com.eyeot.model.repositories.UserRepository;
import br.com.eyeot.security.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public ResponseEntity<ResponseDTO<Void>> login(LoginRequestDTO loginRequestDTO){

        Optional<User> user = this.userRepository.findByEmail1(loginRequestDTO.email());

        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder().status(HttpStatus.BAD_REQUEST.toString()).descricao("Email ou Senha incorretos!").build());
        }
        if(passwordEncoder.matches(loginRequestDTO.password(), user.get().getPassword())) {
            ResponseCookie token = this.tokenService.generarToken(user.get());

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, token.toString())        
            .body(ResponseDTO.<Void>builder()
                    .status(HttpStatus.OK.toString())
                    .descricao("Usuário logado com sucesso!")
                    .build()); 
        }
        return ResponseEntity.internalServerError().body(ResponseDTO.<Void>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.toString()).descricao("Email ou Senha incorretos!").build());
    }

    @Transactional
    public ResponseEntity<ResponseDTO<Void>> registrarUsuario(UserFormDTO userFormDTO){

        Optional<User> user = this.userRepository.findByEmail1(userFormDTO.email1());

        if (user.isPresent()) {
            return ResponseEntity.badRequest().body(ResponseDTO.<Void>builder().status(HttpStatus.BAD_REQUEST.toString()).descricao("Usuário já cadastrado!").data(null).build());
        }

        User novoUsuario = new User(null, 
                    userFormDTO.name(),
                    userFormDTO.phone(), 
                    userFormDTO.email1(),
                    userFormDTO.email2(),  
                    passwordEncoder.encode(userFormDTO.password()),
                    null);
        
        userRepository.save(novoUsuario);


        return ResponseEntity.ok().body(ResponseDTO.<Void>builder()
                .status(HttpStatus.OK.toString())
                .descricao("Usuário registrado com sucesso!").build()); 
    }
}
