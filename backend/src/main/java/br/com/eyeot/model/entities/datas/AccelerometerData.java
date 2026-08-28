package br.com.eyeot.model.entities.datas;

import br.com.eyeot.model.entities.TelemetryData;
import br.com.eyeot.model.entities.ids.TelemetryDataIndexId;
import jakarta.persistence.Column;
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
@Table(name = "omni_telemetrydata_accelerometer")
public class AccelerometerData {

    @EmbeddedId
    private TelemetryDataIndexId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("telemetryDataId")
    @JoinColumn(
        name = "id_telemetrydata",
        nullable = false
    )
    private TelemetryData telemetryData;

    @Column(name = "xvalue")
    private Double xValue;

    @Column(name = "yvalue")
    private Double yValue;

    @Column(name = "zvalue")
    private Double zValue;

}
