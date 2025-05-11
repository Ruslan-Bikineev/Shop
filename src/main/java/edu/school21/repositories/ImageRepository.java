package edu.school21.repositories;

import edu.school21.models.Image;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends ListCrudRepository<Image, UUID> {
}
