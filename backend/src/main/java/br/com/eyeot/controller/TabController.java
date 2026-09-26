package br.com.eyeot.controller;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.eyeot.model.dtos.ResponseDTO;
import br.com.eyeot.model.dtos.TabDTO;
import br.com.eyeot.model.services.TabService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("v1/tab")
@RequiredArgsConstructor 
public class TabController {

    private final TabService tabService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<TabDTO>>> getList(HttpServletRequest request) {
        return tabService.getList(request);
    }
}