package br.com.eyeot.model.entities;

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
@Table(name = "eyeot_user")
public class User {

    @Id
     @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "user_seq"
    )
    @SequenceGenerator(
        name = "user_seq",
        sequenceName = "eyeot_id_user_sequence",
        allocationSize = 1
    )
    @Column(name = "id_user")
    private Integer id_user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", length = 14)
    private String phone;

    @Column(name = "email1", length = 128)
    private String email1;
    
    @Column(name = "email2", length = 128)
    private String email2;

    @Column(name = "pw")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Domain> domains  = new ArrayList<>();;

}
