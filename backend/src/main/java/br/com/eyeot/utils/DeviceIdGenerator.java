package br.com.eyeot.utils;

import java.security.SecureRandom;
import java.util.HexFormat;

import org.springframework.stereotype.Service;

@Service
public class DeviceIdGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String gerar() {
        byte[] bytes = new byte[4];

        RANDOM.nextBytes(bytes);

        return HexFormat.of()
                .withUpperCase()
                .formatHex(bytes);
    }
}