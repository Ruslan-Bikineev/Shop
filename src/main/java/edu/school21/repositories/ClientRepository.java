package edu.school21.repositories;

import edu.school21.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    @Query("SELECT c FROM Client c WHERE c.name = :name AND c.surname = :surname")
    List<Client> findByNameAndSurname(String name, String surname);
}
