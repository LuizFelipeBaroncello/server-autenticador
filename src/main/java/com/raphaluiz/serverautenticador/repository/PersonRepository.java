package com.raphaluiz.serverautenticador.repository;

import com.raphaluiz.serverautenticador.model.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> getByName(String name);
}
