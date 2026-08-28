package br.com.eyeot.model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.eyeot.model.entities.datas.AccelerometerData;
import br.com.eyeot.model.entities.datas.BooleanData;
import br.com.eyeot.model.entities.datas.GpsData;
import br.com.eyeot.model.entities.datas.IntegerData;
import br.com.eyeot.model.entities.datas.RealData;
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
@Table(name = "omni_telemetrydata")
public class TelemetryData {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "telemetry_seq"
    )
    @SequenceGenerator(
        name = "telemetry_seq",
        sequenceName = "omni_id_telemetry_sequence",
        allocationSize = 1
    )
    @Column(name = "id_telemetrydata")
    private Long id;

    @Column(name = "deviceid", nullable = false, length = 8)
    private String deviceId;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "sourceaddress")
    private String sourceAddress;

    @Column(name = "port")
    private Short port;

    @OneToMany(mappedBy = "telemetryData")
    private List<AccelerometerData> accelerometerData = new ArrayList<>();

    @OneToMany(mappedBy = "telemetryData")
    private List<GpsData> gpsData = new ArrayList<>();

    @OneToMany(mappedBy = "telemetryData")
    private List<IntegerData> integerData = new ArrayList<>();

    @OneToMany(mappedBy = "telemetryData")
    private List<RealData> realData = new ArrayList<>();

    @OneToMany(mappedBy = "telemetryData")
    private List<BooleanData> booleanData = new ArrayList<>();
}