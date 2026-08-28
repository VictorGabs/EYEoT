package br.com.eyeot.model.entities.ids;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class TelemetryDataIndexId implements Serializable {

    @Column(name = "id_telemetrydata")
    private Long telemetryDataId;

    @Column(name = "index")
    private Short index;

    public TelemetryDataIndexId() {
    }

    public TelemetryDataIndexId(Long telemetryDataId, Short index) {
        this.telemetryDataId = telemetryDataId;
        this.index = index;
    }

    // getters e setters

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TelemetryDataIndexId that)) return false;

        return telemetryDataId != null
                && telemetryDataId.equals(that.telemetryDataId)
                && index != null
                && index.equals(that.index);
    }

    @Override
    public int hashCode() {
        int result = telemetryDataId != null
                ? telemetryDataId.hashCode()
                : 0;

        result = 31 * result + (
                index != null ? index.hashCode() : 0
        );

        return result;
    }
}