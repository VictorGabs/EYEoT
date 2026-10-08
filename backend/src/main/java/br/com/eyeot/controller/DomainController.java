package br.com.eyeot.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.UserDTO;
import br.com.eyeot.model.dtos.domain.DomainFiltroDTO;
import br.com.eyeot.model.dtos.domain.DomainResumeDTO;
import br.com.eyeot.model.dtos.forms.DomainFormDTO;
import br.com.eyeot.model.services.DomainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("v1/domains")
@RequiredArgsConstructor 
public class DomainController {

    private final DomainService domainService;
    
    @PostMapping ("/register")
    public ResponseEntity<ResponseDTO<Void>> registrarDominio(@Valid @RequestBody DomainFormDTO domainFormDTO, @AuthenticationPrincipal UserDTO userDTO) {
        return domainService.registrarDominio(domainFormDTO, userDTO);
    }

    @GetMapping ("/my/all")
    public ResponseEntity<ResponseDTO<List<DomainResumeDTO>>> listarMeusDominios(@AuthenticationPrincipal UserDTO userDTO) {
        return domainService.listarMeusDominios(userDTO);
    }

    @GetMapping ("/my")
    public ResponseEntity<ResponseDTO<List<DomainResumeDTO>>> listarMeusDominiosPaginado(@ModelAttribute DomainFiltroDTO filtro, @PageableDefault(size=10) Pageable pageable, @AuthenticationPrincipal UserDTO userDTO) {
        return domainService.listarMeusDominiosPaginado(filtro,userDTO,pageable);
    }

}

