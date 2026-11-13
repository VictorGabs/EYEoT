package br.com.eyeot.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.eyeot.enuns.RolesEnum;
import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Domain;
import br.com.eyeot.model.entities.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class DeviceSpecification {

    public static Specification<Device> comFiltro(
            String busca,
            Integer idDomain,
            Integer idUser,
            RolesEnum roleEnum
    ) {

          return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<Device, Domain> domainJoin =
                    root.join("domain", JoinType.LEFT);

            if (idUser != null && roleEnum != null
                    && roleEnum.name().equals("ROLE_USER")) {

                Join<Device, User> userJoin =
                        root.join("owner");

                predicates.add(
                    cb.equal(userJoin.get("id_user"), idUser)
                );
            }

            if (idDomain != null) {
                predicates.add(
                    cb.equal(domainJoin.get("id_domain"), idDomain)
                );
            }

            if (busca != null && !busca.isBlank()) {

                String valor = "%" + busca.toLowerCase() + "%";

                predicates.add(
                    cb.or(
                        cb.like(cb.lower(root.get("deviceId")), valor),
                        cb.like(cb.lower(domainJoin.get("domainName")), valor)
                    )
                );
            }

            return cb.and(
                predicates.toArray(new Predicate[0])
            );
        };
    }
}