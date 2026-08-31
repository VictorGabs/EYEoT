package br.com.eyeot.model.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.eyeot.model.entities.User;

public interface UserRepository extends JpaRepository<User, Integer>{
    Optional<User> findByEmail1(String email);

}
