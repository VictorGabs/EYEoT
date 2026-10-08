package br.com.eyeot.model.dtos.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorTypeDTO {

    private Integer id;
    private String description;
    private Short dataType;

    private BigDecimal minValue;
    private BigDecimal maxValue;

    private String physicalQuantity;
    private String measureUnit;

    private Integer averageInterval;
}