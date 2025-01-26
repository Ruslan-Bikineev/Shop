package edu.school21.repositories;

import edu.school21.models.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends ListCrudRepository<Category, UUID> {
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByName(String name);

    @Modifying
    @Transactional
    @Query("DELETE FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    int deleteByName(String name);
}
