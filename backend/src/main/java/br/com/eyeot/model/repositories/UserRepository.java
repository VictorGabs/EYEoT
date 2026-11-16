package br.com.eyeot.model.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.eyeot.model.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByEmail1(String email);

    @Query("""
    SELECT u
    FROM User u
    WHERE
        LOWER(u.name) LIKE LOWER(CONCAT('%', :busca, '%'))
        OR LOWER(u.email1) LIKE LOWER(CONCAT('%', :busca, '%'))
        OR LOWER(u.email2) LIKE LOWER(CONCAT('%', :busca, '%'))
        OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :busca, '%'))
    """)
    Page<User> buscarUsuariosPaginado(String busca, Pageable pageable);

}
