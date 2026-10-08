package br.com.eyeot.model.dtos.sensor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class SensorDTO {

    private Integer idDevice;
    private Short index;

    private SensorTypeDTO sensorType;
}