package br.com.eyeot.model.entities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "eyeot_sensor_type")
public class SensorType {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "sensor_type_seq"
    )
    @SequenceGenerator(
        name = "sensor_type_seq",
        sequenceName = "eyeot_id_sensortype_sequence",
        allocationSize = 1
    )
    @Column(name = "id_sensortype")
    private Integer id;

    @Column(name = "description", nullable = false, length = 256)
    private String description;

    @Column(name = "datatype", nullable = false)
    private Short dataType;

    @Column(name = "minvalue")
    private BigDecimal minValue;

    @Column(name = "maxvalue")
    private BigDecimal maxValue;

    @Column(name = "physicalquantity", nullable = false, length = 64)
    private String physicalQuantity;

    @Column(name = "measureunit", nullable = false, length = 12)
    private String measureUnit;

    @Column(name = "average_interval")
    private Integer averageInterval;

    @OneToMany(mappedBy = "sensorType")
    private List<Sensor> sensors = new ArrayList<>();

}
