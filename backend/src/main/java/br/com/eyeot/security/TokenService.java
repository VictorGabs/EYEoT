package br.com.eyeot.security;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import br.com.eyeot.model.entities.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String chaveSecreta;

    public ResponseCookie generarToken(User user ){
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chaveSecreta);

            String token = JWT.create()
                    .withIssuer("eyeot")
                    .withSubject(user.getEmail1())
                    .withClaim("nome", user.getName())
                    .withClaim("email1", user.getEmail1())
                    .withExpiresAt(gerarDataExpiracaoToken()).sign(algoritmo);

            ResponseCookie cookie = ResponseCookie.from("eyeot-token", token)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(Duration.ofHours(2))
                    .build();

            return cookie;

        } catch (JWTCreationException e) {
            throw new RuntimeException("Ocorreu um erro na autenticação");
        }
    }

    public String validarToken(String token){
        try {
            Algorithm algoritmo = Algorithm.HMAC256(chaveSecreta);

           return JWT.require(algoritmo)
                    .withIssuer("eyeot")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException e) {
            return null;
        }
    }

    public String recoverToken(HttpServletRequest request) {

        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("bmm-advocacia-token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    private Instant gerarDataExpiracaoToken(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
