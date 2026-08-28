package br.com.eyeot.model.entities;

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
@Table(name = "eyeot_domain")
public class Domain {

    @Id
     @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "domain_seq"
    )
    @SequenceGenerator(
        name = "domain_seq",
        sequenceName = "eyeot_id_domain_sequence",
        allocationSize = 1
    )
    @Column(name = "id_domain")
    private Integer id_domain;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user")
    private User user;

    @Column(name = "domainname", nullable = false)
    private String domainName;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "domain")
    private List<Device> devices  = new ArrayList<>();;



}
