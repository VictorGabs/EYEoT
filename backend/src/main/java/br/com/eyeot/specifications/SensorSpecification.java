package br.com.eyeot.specifications;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Sensor;
import br.com.eyeot.model.entities.SensorType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

public class SensorSpecification {

    public static Specification<Sensor> comFiltro(
            String busca,
            Integer idUser
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            Join<Sensor, Device> deviceJoin =
                    root.join("device");

            Join<Sensor, SensorType> sensorTypeJoin =
                    root.join("sensorType");

            predicates.add(
                cb.equal(
                    deviceJoin.get("owner").get("id_user"),
                    idUser
                )
            );

            if (busca != null && !busca.isEmpty()) {

                Predicate deviceIdPredicate = cb.like(
                    cb.lower(deviceJoin.get("deviceId")),
                    "%" + busca.toLowerCase() + "%"
                );

                Predicate sensorTypeDescriptionPredicate = cb.like(
                    cb.lower(sensorTypeJoin.get("description")),
                    "%" + busca.toLowerCase() + "%"
                );

                Predicate sensorTypePhysicalQuantityPredicate = cb.like(
                    cb.lower(sensorTypeJoin.get("physicalQuantity")),
                    "%" + busca.toLowerCase() + "%"
                );

                Predicate sensorTypeMeasureUnitPredicate = cb.like(
                    cb.lower(sensorTypeJoin.get("measureUnit")),
                    "%" + busca.toLowerCase() + "%"
                );



                predicates.add(
                    cb.or(
                        deviceIdPredicate,
                        sensorTypeDescriptionPredicate,
                        sensorTypePhysicalQuantityPredicate,
                        sensorTypeMeasureUnitPredicate                    )
                );
            }


         

            return cb.and(
                predicates.toArray(new Predicate[0])
            );
        };
    }
}