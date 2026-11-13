package br.com.eyeot.enuns;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public enum SensorTypeDataTypeEnum {

    REAL((short) 1, "Real"),
    BOOLEAN((short) 2, "Booleano"),
    GPS((short) 3, "GPS"),
    ACCELEROMETER((short) 4, "Acelerômetro"),
    INTEGER((short)5,"Inteiro");

    private final Short code;
    private final String description;

    public static boolean isValid(Short code) {
        return Arrays.stream(values())
                .anyMatch(type -> type.code.equals(code));
    }

    public static SensorTypeDataTypeEnum fromCode(Short code) {
        return Arrays.stream(values())
                .filter(type -> type.code.equals(code))
                .findFirst()
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "Tipo de dado inválido: " + code
                    )
                );
    }
}