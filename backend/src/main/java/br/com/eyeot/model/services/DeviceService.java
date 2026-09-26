package br.com.eyeot.model.services;

import org.springframework.stereotype.Service;

import br.com.eyeot.model.repositories.DeviceRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class DeviceService {
    private final DeviceRepository DeviceRepository;
}
