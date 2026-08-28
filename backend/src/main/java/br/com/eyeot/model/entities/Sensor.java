package br.com.eyeot.model.entities;

import br.com.eyeot.model.entities.ids.SensorId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
@Table(name = "eyeot_sensor")
public class Sensor {

    @EmbeddedId
    SensorId sensorId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("id_device")
    @JoinColumn(name = "id_device", nullable = false)
    private Device device;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sensortype", nullable = false)
    private SensorType sensorType;

}
