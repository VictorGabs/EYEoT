package br.com.eyeot.model.services;

import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.eyeot.security.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;

    public ResponseEntity<Boolean> validate(HttpServletRequest request){
        if (Objects.isNull(tokenService.validarToken(recoverToken(request)))) {
            System.out.println(false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
        return ResponseEntity.ok(true);
    }

    private String recoverToken(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("eyeot-token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

}