package br.com.eyeot.model.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "eyeot_devices")
public class Device {

    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "device_seq"
    )
    @SequenceGenerator(
        name = "device_seq",
        sequenceName = "eyeot_id_device_sequence",
        allocationSize = 1
    )
    @Column(name = "id_device")
    private Integer id_device;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_domain")
    private Domain domain;

    @Column(name = "deviceid", nullable = false, length = 8, columnDefinition = "CHAR(8)")
    private char deviceId;

    @Column(name = "creationdate", nullable = false)
    private LocalDateTime creationDate;

    @Column(name ="enable", nullable = false)
    private Boolean enable;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 6)
    private BigDecimal longitude;

    @Column(name = "altitude", precision = 6, scale = 2)
    private BigDecimal altitude;

    @OneToMany(mappedBy = "device")
    private List<Sensor> sensors = new ArrayList<>();

}
