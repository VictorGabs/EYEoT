package br.com.eyeot.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController 
public class CsrfController {

    @GetMapping("/v1/auth/csrf")
    public CsrfToken csrfToken (CsrfToken csrfToken){
        return csrfToken;
    }
}
