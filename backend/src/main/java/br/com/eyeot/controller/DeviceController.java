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
import br.com.eyeot.model.dtos.device.DeviceFilterDTO;
import br.com.eyeot.model.dtos.device.DeviceResumeDTO;
import br.com.eyeot.model.dtos.forms.DeviceFormDTO;
import br.com.eyeot.model.services.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

     private final DeviceService deviceService;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<Void>> registrarDispositivo(
            @Valid @RequestBody DeviceFormDTO deviceFormDTO,
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return deviceService.registrarDispositivo(
            deviceFormDTO,
            userDTO
        );
    }

    @GetMapping("/my/all")
    public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarMeusDispositivos(
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return deviceService.listarMeusDispositivos(userDTO);
    }

    @GetMapping("/my")
    public ResponseEntity<ResponseDTO<List<DeviceResumeDTO>>> listarMeusDispositivosPaginado(
            @ModelAttribute DeviceFilterDTO filtro,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal UserDTO userDTO
    ) {
        return deviceService.listarMeusDispositivosPaginado(
            filtro,
            userDTO,
            pageable
        );
    }

}
