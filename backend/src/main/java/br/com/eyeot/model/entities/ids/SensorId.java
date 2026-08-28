package br.com.eyeot.model.entities.ids;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class SensorId implements Serializable {

    @Column(name = "id_device")
    private Integer id_device;

    @Column(name = "index")
    private Short index;

    public SensorId() {
    }

    public SensorId(Integer deviceId, Short index) {
        this.id_device = deviceId;
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SensorId)) return false;

        SensorId sensorId = (SensorId) o;

        return id_device != null
                && id_device.equals(sensorId.id_device)
                && index != null
                && index.equals(sensorId.index);
    }

    @Override
    public int hashCode() {
        int result = id_device != null ? id_device.hashCode() : 0;
        result = 31 * result + (index != null ? index.hashCode() : 0);
        return result;
    }

}
