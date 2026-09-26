package br.com.eyeot.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.eyeot.model.dtos.DomainFiltroDTO;
import br.com.eyeot.model.entities.Domain;
import br.com.eyeot.model.entities.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class DomainSpecification {

    public static Specification<Domain> comFiltro(DomainFiltroDTO filtro, Integer idUser) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            /*
            * Busca geral
            */
            if(idUser != null){
                Join<Domain, User> userJoin = root.join("user");

                predicates.add(
                    cb.equal(
                        userJoin.get("id_user"),
                        idUser
                    )
                );
            }

            if (filtro.getBusca() != null &&
                !filtro.getBusca().isBlank()) {

                String busca = "%" + filtro.getBusca().toLowerCase() + "%";

                Predicate domainName = cb.like(
                    cb.lower(root.get("domainName")),
                    busca
                );

                Predicate description = cb.like(
                    cb.lower(root.get("description")),
                    busca
                );

        

                predicates.add(
                    cb.or(
                        domainName,
                        description
                    )
                );
            }

             return cb.and(
                predicates.toArray(new Predicate[0])
            );
        };

    }

}
