package br.com.eyeot.specifications;

import org.springframework.data.jpa.domain.Specification;

import br.com.eyeot.model.entities.SensorType;

public class SensorTypeSpecification {

    public static Specification<SensorType> comFiltro(
            String busca
    ) {

        return (root, query, cb) -> {

            if (busca == null || busca.isBlank()) {
                return cb.conjunction();
            }

            String valor =
                    "%" + busca.toLowerCase() + "%";

            return cb.or(
                cb.like(
                    cb.lower(root.get("description")),
                    valor
                ),
                cb.like(
                    cb.lower(root.get("physicalQuantity")),
                    valor
                ),
                cb.like(
                    cb.lower(root.get("measureUnit")),
                    valor
                )
            );
        };
    }
}