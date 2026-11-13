package br.com.eyeot.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.entities.User;
import br.com.eyeot.model.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

         if (request.getRequestURI().equals("/ws")) {
            System.out.println("Requisição WebSocket passou pelo filtro JWT");
        }

        var token = tokenService.recoverToken(request);

        if (request.getRequestURI().equals("/ws")) {
            System.out.println(
                "JWT encontrado: " + (token != null)
            );
        }
        
        if (token != null) {
            var email = tokenService.validarToken(token);

            if(email != null){
                User user = userRepository.findByEmail1(email).orElseThrow(() -> new RuntimeException("Usuario Não Encontrado"));
                
                 if (user != null) {
                
                    UserDTO userDTO = new UserDTO(
                        user.getId_user(),
                        user.getPapel(),
                        user.getName(),
                        user.getPhone(),
                        user.getEmail1(),
                        user.getEmail2());
                    var authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getPapel().toString()));
                    var authentication = new UsernamePasswordAuthenticationToken(userDTO, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        System.out.println(
            "Método: " + request.getMethod()
            + " | URI: " + request.getRequestURI()
            + " | Autenticado: "
            + (SecurityContextHolder.getContext().getAuthentication() != null)
        );
       
        filterChain.doFilter(request, response);
    }

    
}