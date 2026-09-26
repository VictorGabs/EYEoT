package br.com.eyeot.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data 
@NoArgsConstructor
@AllArgsConstructor 
public class DeviceDTO {

    private Integer idDevice;
    private String domainName;

    private String deviceId;
    private LocalDateTime creationDate;
    private Boolean enable;

    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal altitude;

    private List<SensorDTO> sensors;
}
