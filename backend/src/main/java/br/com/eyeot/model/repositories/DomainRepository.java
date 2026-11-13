package br.com.eyeot.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import br.com.eyeot.model.entities.Device;
import br.com.eyeot.model.entities.Domain;

import java.util.List;
import java.util.Optional;


public interface DomainRepository extends JpaRepository<Domain, Integer>, JpaSpecificationExecutor<Domain> {

    @Query ("FROM Domain d WHERE d.domainName = :domainName AND d.owner.id_user = :id_user")
    Optional<Domain> findByDomainNameAndUser(String domainName, Integer id_user);

    @Query ("FROM Domain d WHERE d.owner.id_user = :id_user")
    List<Domain> findMyDomains (Integer id_user);


}
